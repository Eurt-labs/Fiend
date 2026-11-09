package com.fiend.music.data.repository

import com.fiend.music.data.music.YouTubeMusicEntity
import com.fiend.music.data.music.YouTubeMusicEntityKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ArtistMatchTest {
    private fun artist(name: String, id: String) =
        YouTubeMusicEntity(kind = YouTubeMusicEntityKind.ARTIST, name = name, browseId = id)

    @Test
    fun picksTheExactArtistEvenWhenNotRankedFirst() {
        val results = listOf(artist("Drake Bell", "UCbell"), artist("Drake", "UCdrake"))
        assertEquals("UCdrake", pickArtistMatch(results, "Drake")?.browseId)
    }

    @Test
    fun neverFallsBackToAnUnrelatedFirstResult() {
        val results = listOf(artist("Drake Bell", "UCbell"), artist("Drakeo the Ruler", "UCdrakeo"))
        assertNull(pickArtistMatch(results, "Drake"))
    }

    @Test
    fun toleratesCaseAccentsAndPunctuation() {
        assertEquals("UCb", pickArtistMatch(listOf(artist("Beyoncé", "UCb")), "beyonce")?.browseId)
        assertEquals("UCac", pickArtistMatch(listOf(artist("ACDC", "UCac")), "AC/DC")?.browseId)
    }
}
