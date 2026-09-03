/**
 * Fiend Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.fiend.music.utils

import com.fiend.innertube.YouTube
import com.fiend.innertube.models.SongItem
import com.fiend.innertube.models.WatchEndpoint
import com.fiend.music.db.MusicDatabase
import com.fiend.music.db.entities.ArtistEntity
import com.fiend.music.db.entities.Event
import com.fiend.music.db.entities.RelatedSongMap
import com.fiend.music.db.entities.SearchHistory
import com.fiend.music.models.toMediaMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import java.time.LocalDateTime

object TasteRecommendationSeeder {
    private const val TAG = "TasteRecommendationSeeder"

    /**
     * Seeds the local Room database with songs, artists, play events, related song maps,
     * and search history derived from the user's selected genres, languages, and artists.
     *
     * Every seed track is firmly logged in the database with [liked = true], [inLibrary = now],
     * and recorded play time so they appear in Liked Songs and fuel Home recommendations.
     */
    suspend fun seedUserTaste(
        database: MusicDatabase,
        genres: List<String>,
        languages: List<String>,
        artists: List<String>,
        onProgress: (String) -> Unit = {},
    ) = withContext(Dispatchers.IO) {
        try {
            onProgress("Connecting to music service…")

            val seedArtists = artists.filter { it.isNotBlank() }.distinct()
            val seededSongIds = mutableListOf<String>()

            // 1. Seed Selected Artists & Their Top Hits as Liked Songs
            seedArtists.take(6).forEachIndexed { artistIndex, artistName ->
                onProgress("Discovering top tracks by $artistName…")
                withTimeoutOrNull(6000L) {
                    try {
                        val searchResult = YouTube.search(artistName, YouTube.SearchFilter.FILTER_SONG).getOrNull()
                        val songs = searchResult?.items?.filterIsInstance<SongItem>()?.take(3).orEmpty()

                        songs.forEachIndexed { songIdx, songItem ->
                            val mediaMetadata = songItem.toMediaMetadata()
                            val now = LocalDateTime.now().minusMinutes((songIdx * 20 + artistIndex * 35 + 15).toLong())

                            // Log song firmly in Room database with liked=true, inLibrary, and simulated play time
                            database.insert(mediaMetadata) { entity ->
                                entity.copy(
                                    liked = true,
                                    likedDate = now,
                                    inLibrary = now,
                                    totalPlayTime = (180_000L + songIdx * 30_000L),
                                )
                            }

                            // Bookmark artist so they show up under Library -> Artists
                            val artistId = mediaMetadata.artists.firstOrNull()?.id ?: ArtistEntity.generateArtistId()
                            val existingArtist = database.getArtistById(artistId)
                            if (existingArtist != null) {
                                database.update(existingArtist.copy(bookmarkedAt = now))
                            } else {
                                database.insert(
                                    ArtistEntity(
                                        id = artistId,
                                        name = mediaMetadata.artists.firstOrNull()?.name ?: artistName,
                                        thumbnailUrl = mediaMetadata.thumbnailUrl,
                                        bookmarkedAt = now,
                                    )
                                )
                            }

                            // Record playback event so Quick Picks & Most Played queries pick it up immediately
                            database.insert(
                                Event(
                                    songId = mediaMetadata.id,
                                    timestamp = now,
                                    playTime = 180_000L,
                                )
                            )

                            seededSongIds.add(mediaMetadata.id)
                        }

                        // Populate search history for the artist
                        database.insert(SearchHistory(query = artistName))
                    } catch (e: Exception) {
                        Timber.tag(TAG).w(e, "Failed seeding artist: $artistName")
                    }
                }
            }

            // 2. Seed Genre & Language Discovery Hits
            val discoveryQueries = (genres.take(3) + languages.take(2)).filter { it.isNotBlank() }
            discoveryQueries.forEach { query ->
                onProgress("Curating soundscape for $query…")
                withTimeoutOrNull(5000L) {
                    try {
                        val searchQuery = if (query.contains("hits", ignoreCase = true) || query.contains("music", ignoreCase = true)) {
                            query
                        } else {
                            "$query hits"
                        }
                        val result = YouTube.search(searchQuery, YouTube.SearchFilter.FILTER_SONG).getOrNull()
                        val songs = result?.items?.filterIsInstance<SongItem>()?.take(2).orEmpty()

                        songs.forEachIndexed { sIdx, songItem ->
                            val mediaMetadata = songItem.toMediaMetadata()
                            val now = LocalDateTime.now().minusHours((sIdx + 2).toLong())

                            database.insert(mediaMetadata) { entity ->
                                entity.copy(
                                    liked = true,
                                    likedDate = now,
                                    inLibrary = now,
                                    totalPlayTime = 160_000L,
                                )
                            }

                            database.insert(
                                Event(
                                    songId = mediaMetadata.id,
                                    timestamp = now,
                                    playTime = 160_000L,
                                )
                            )

                            seededSongIds.add(mediaMetadata.id)
                        }

                        database.insert(SearchHistory(query = query))
                    } catch (e: Exception) {
                        Timber.tag(TAG).w(e, "Failed seeding query: $query")
                    }
                }
            }

            // 3. Populate Related Song Maps for Quick Picks & Radios
            onProgress("Generating personalized Quick Picks…")
            val topSeedSongs = seededSongIds.distinct().take(3)
            topSeedSongs.forEach { seedSongId ->
                withTimeoutOrNull(5000L) {
                    try {
                        val nextResult = YouTube.next(WatchEndpoint(videoId = seedSongId)).getOrNull()
                        val relatedSongs = nextResult?.items?.filterIsInstance<SongItem>()?.take(4).orEmpty()

                        relatedSongs.forEach { relatedItem ->
                            val relatedMetadata = relatedItem.toMediaMetadata()
                            database.insert(relatedMetadata)
                            database.insert(
                                RelatedSongMap(
                                    songId = seedSongId,
                                    relatedSongId = relatedMetadata.id,
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Timber.tag(TAG).w(e, "Failed fetching related songs for $seedSongId")
                    }
                }
            }

            onProgress("Ready! Welcome to your sanctuary.")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error during taste recommendation seeding")
        }
    }
}
