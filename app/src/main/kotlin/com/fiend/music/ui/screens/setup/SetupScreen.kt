/**
 * Fiend Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.fiend.music.ui.screens.setup

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.fiend.innertube.YouTube
import com.fiend.innertube.models.ArtistItem
import com.fiend.music.R
import com.fiend.music.ui.screens.settings.DarkMode
import com.fiend.music.constants.DarkModeKey
import com.fiend.music.constants.OnboardingArtistsKey
import com.fiend.music.constants.OnboardingCompletedKey
import com.fiend.music.constants.OnboardingGenresKey
import com.fiend.music.utils.rememberEnumPreference
import com.fiend.music.utils.rememberPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Curated genres with aesthetic icons
private val CURATED_GENRES = listOf(
    "Pop" to "🎧",
    "Hip-Hop" to "🎤",
    "Rock" to "🎸",
    "R&B" to "✨",
    "Lo-Fi" to "🌌",
    "Electronic" to "⚡",
    "Indie" to "🌿",
    "K-Pop" to "🌸",
    "Desi & Bollywood" to "🪕",
    "Jazz & Blues" to "🎷",
    "Acoustic" to "🎻",
    "Metal" to "🤘",
    "Chill & Ambient" to "🌙",
    "Reggae & Tropical" to "🌴",
    "Classical" to "🎼",
    "Phonk" to "🔥",
)

// Curated popular seed artists with identifiers
private data class SeedArtist(val name: String, val query: String)
private val POPULAR_ARTISTS = listOf(
    SeedArtist("The Weeknd", "The Weeknd"),
    SeedArtist("Taylor Swift", "Taylor Swift"),
    SeedArtist("Drake", "Drake"),
    SeedArtist("Billie Eilish", "Billie Eilish"),
    SeedArtist("Travis Scott", "Travis Scott"),
    SeedArtist("Imagine Dragons", "Imagine Dragons"),
    SeedArtist("Post Malone", "Post Malone"),
    SeedArtist("Dua Lipa", "Dua Lipa"),
    SeedArtist("Kendrick Lamar", "Kendrick Lamar"),
    SeedArtist("Bruno Mars", "Bruno Mars"),
    SeedArtist("Ed Sheeran", "Ed Sheeran"),
    SeedArtist("Ariana Grande", "Ariana Grande"),
    SeedArtist("Coldplay", "Coldplay"),
    SeedArtist("Eminem", "Eminem"),
    SeedArtist("Arijit Singh", "Arijit Singh"),
    SeedArtist("BTS", "BTS"),
    SeedArtist("Olivia Rodrigo", "Olivia Rodrigo"),
    SeedArtist("Lana Del Rey", "Lana Del Rey"),
    SeedArtist("Justin Bieber", "Justin Bieber"),
    SeedArtist("SZA", "SZA"),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetupScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.OFF)
    val isSystemDark = isSystemInDarkTheme()
    val isDark = remember(darkTheme, isSystemDark) {
        if (darkTheme == DarkMode.AUTO) isSystemDark else darkTheme == DarkMode.ON
    }

    val (_, setOnboardingCompleted) = rememberPreference(OnboardingCompletedKey, defaultValue = false)
    val (_, setOnboardingGenres) = rememberPreference(OnboardingGenresKey, defaultValue = "")
    val (_, setOnboardingArtists) = rememberPreference(OnboardingArtistsKey, defaultValue = "")

    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    var currentStep by rememberSaveable { mutableIntStateOf(0) }
    val selectedGenres = remember { mutableStateListOf<String>() }
    val selectedArtists = remember { mutableStateListOf<String>() }

    // Search state for artists
    var searchQuery by remember { mutableStateOf("") }
    val searchResults = remember { mutableStateListOf<ArtistItem>() }
    var isSearching by remember { mutableStateOf(false) }
    var searchJob by remember { mutableStateOf<Job?>(null) }

    // Live search debouncer
    LaunchedEffect(searchQuery) {
        searchJob?.cancel()
        if (searchQuery.trim().length >= 2) {
            isSearching = true
            searchJob = coroutineScope.launch {
                delay(300)
                withContext(Dispatchers.IO) {
                    try {
                        val result = YouTube.search(searchQuery.trim(), YouTube.SearchFilter.FILTER_ARTIST).getOrNull()
                        val artists = result?.items?.filterIsInstance<ArtistItem>().orEmpty()
                        withContext(Dispatchers.Main) {
                            searchResults.clear()
                            searchResults.addAll(artists.take(8))
                            isSearching = false
                        }
                    } catch (_: Exception) {
                        withContext(Dispatchers.Main) {
                            isSearching = false
                        }
                    }
                }
            }
        } else {
            searchResults.clear()
            isSearching = false
        }
    }

    // Finish onboarding logic
    val finishOnboarding = {
        coroutineScope.launch {
            setOnboardingGenres(selectedGenres.joinToString(","))
            setOnboardingArtists(selectedArtists.joinToString(","))
            setOnboardingCompleted(true)
            onComplete()
        }
    }

    // Dynamic background colors
    val bgColor = if (isDark) Color(0xFF0F0F14) else Color(0xFFF7F6FB)
    val cardBg = if (isDark) Color(0xFF1E1E28).copy(alpha = 0.70f) else Color.White.copy(alpha = 0.75f)
    val cardBorder = if (isDark) Color.White.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.85f)
    val primaryText = if (isDark) Color.White else Color(0xFF111115)
    val secondaryText = if (isDark) Color.White.copy(alpha = 0.65f) else Color(0xFF111115).copy(alpha = 0.65f)
    val accentPurple = Color(0xFFA855F7)
    val accentIndigo = Color(0xFF6366F1)

    // Animated ambient glow orbs
    val infiniteTransition = rememberInfiniteTransition(label = "ambientGlow")
    val glowOffset1 by infiniteTransition.animateFloat(
        initialValue = -80f,
        targetValue = 80f,
        animationSpec = infiniteRepeatable(tween(7000, easing = LinearEasing), RepeatMode.Reverse),
        label = "glow1"
    )
    val glowOffset2 by infiniteTransition.animateFloat(
        initialValue = 60f,
        targetValue = -60f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Reverse),
        label = "glow2"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // 1. Ambient Background Glowing Mesh
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopStart)
                .scale(1f + glowOffset1 / 300f)
                .blur(90.dp)
                .background(Brush.radialGradient(listOf(accentPurple.copy(alpha = if (isDark) 0.35f else 0.20f), Color.Transparent)))
        )
        Box(
            modifier = Modifier
                .size(360.dp)
                .align(Alignment.BottomEnd)
                .scale(1f + glowOffset2 / 300f)
                .blur(100.dp)
                .background(Brush.radialGradient(listOf(accentIndigo.copy(alpha = if (isDark) 0.35f else 0.20f), Color.Transparent)))
        )

        // 2. Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            // Header Bar with Step Indicator & Skip Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Step Progress Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(3) { index ->
                        val isActive = index == currentStep
                        val isDone = index < currentStep
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (isActive) 24.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isActive) accentPurple
                                    else if (isDone) accentPurple.copy(alpha = 0.5f)
                                    else primaryText.copy(alpha = 0.18f)
                                )
                        )
                    }
                }

                // Skip Button
                if (currentStep < 2) {
                    TextButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            finishOnboarding()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.onboarding_skip),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = secondaryText,
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Step Content Animated Flow
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { it } + fadeIn()) togetherWith (slideOutHorizontally { -it } + fadeOut())
                    } else {
                        (slideInHorizontally { -it } + fadeIn()) togetherWith (slideOutHorizontally { it } + fadeOut())
                    }
                },
                modifier = Modifier.weight(1f),
                label = "setupStep",
            ) { step ->
                when (step) {
                    // STEP 0: Welcome & Tagline
                    0 -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            // Hero Glowing Music Icon
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .shadow(24.dp, CircleShape, spotColor = accentPurple.copy(0.6f))
                                    .clip(CircleShape)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                accentPurple.copy(alpha = 0.85f),
                                                accentIndigo.copy(alpha = 0.90f),
                                            )
                                        )
                                    )
                                    .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.music_note),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(54.dp),
                                )
                            }

                            Spacer(Modifier.height(32.dp))

                            Text(
                                text = stringResource(R.string.welcome_to_fiend),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp,
                                ),
                                color = primaryText,
                                textAlign = TextAlign.Center,
                            )

                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = stringResource(R.string.onboarding_tagline),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 22.sp,
                                ),
                                color = secondaryText,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )

                            Spacer(Modifier.height(36.dp))

                            // Glass Feature Badges
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                SetupFeatureBadge("✨  Glass Skeuomorphic Interface", cardBg, cardBorder, primaryText)
                                SetupFeatureBadge("⚡  Real-time Tailored Recommendations", cardBg, cardBorder, primaryText)
                                SetupFeatureBadge("🎵  Synchronized Lyrics & Lossless Sound", cardBg, cardBorder, primaryText)
                            }
                        }
                    }

                    // STEP 1: Pick Genres / Vibes
                    1 -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = stringResource(R.string.onboarding_step_vibe),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = primaryText,
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = stringResource(R.string.onboarding_step_vibe_desc),
                                style = MaterialTheme.typography.bodyMedium,
                                color = secondaryText,
                            )

                            Spacer(Modifier.height(16.dp))

                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(bottom = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                item {
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        CURATED_GENRES.forEach { (genre, emoji) ->
                                            val isSelected = selectedGenres.contains(genre)
                                            SetupGenreChip(
                                                genre = genre,
                                                emoji = emoji,
                                                isSelected = isSelected,
                                                onClick = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    if (isSelected) {
                                                        selectedGenres.remove(genre)
                                                    } else {
                                                        selectedGenres.add(genre)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // STEP 2: Pick Favorite Artists (with Live Realtime Search)
                    2 -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = stringResource(R.string.onboarding_step_artists),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = primaryText,
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = stringResource(R.string.onboarding_step_artists_desc),
                                style = MaterialTheme.typography.bodyMedium,
                                color = secondaryText,
                            )

                            Spacer(Modifier.height(14.dp))

                            // Glass Search Bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(26.dp))
                                    .background(cardBg)
                                    .border(1.2.dp, cardBorder, RoundedCornerShape(26.dp))
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.search),
                                        contentDescription = null,
                                        tint = secondaryText,
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    BasicTextField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        singleLine = true,
                                        textStyle = TextStyle(
                                            color = primaryText,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                        ),
                                        cursorBrush = SolidColor(accentPurple),
                                        modifier = Modifier.weight(1f),
                                        decorationBox = { innerTextField ->
                                            if (searchQuery.isEmpty()) {
                                                Text(
                                                    text = stringResource(R.string.onboarding_search_artists),
                                                    color = secondaryText.copy(alpha = 0.7f),
                                                    fontSize = 15.sp,
                                                )
                                            }
                                            innerTextField()
                                        }
                                    )
                                    if (isSearching) {
                                        CircularProgressIndicator(
                                            strokeWidth = 2.dp,
                                            color = accentPurple,
                                            modifier = Modifier.size(18.dp),
                                        )
                                    } else if (searchQuery.isNotEmpty()) {
                                        IconButton(
                                            onClick = { searchQuery = "" },
                                            modifier = Modifier.size(24.dp),
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.close),
                                                contentDescription = null,
                                                tint = secondaryText,
                                                modifier = Modifier.size(16.dp),
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            // Live Search Results or Curated Popular Artists
                            if (searchQuery.trim().length >= 2 && searchResults.isNotEmpty()) {
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(bottom = 16.dp),
                                ) {
                                    items(searchResults, key = { it.id }) { artist ->
                                        val isSelected = selectedArtists.contains(artist.title)
                                        SetupArtistSearchResultRow(
                                            artist = artist,
                                            isSelected = isSelected,
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                if (isSelected) selectedArtists.remove(artist.title)
                                                else selectedArtists.add(artist.title)
                                            }
                                        )
                                    }
                                }
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    contentPadding = PaddingValues(bottom = 16.dp),
                                ) {
                                    items(POPULAR_ARTISTS, key = { it.name }) { artist ->
                                        val isSelected = selectedArtists.contains(artist.name)
                                        SetupPopularArtistCard(
                                            artist = artist,
                                            isSelected = isSelected,
                                            onClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                if (isSelected) selectedArtists.remove(artist.name)
                                                else selectedArtists.add(artist.name)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Bottom Navigation Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(16.dp, RoundedCornerShape(28.dp), spotColor = accentPurple.copy(0.4f))
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                accentPurple,
                                accentIndigo,
                            )
                        )
                    )
                    .border(1.2.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(28.dp))
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (currentStep < 2) {
                            currentStep++
                        } else {
                            finishOnboarding()
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = when (currentStep) {
                        0 -> stringResource(R.string.onboarding_continue)
                        1 -> stringResource(R.string.onboarding_continue) + if (selectedGenres.isNotEmpty()) " (${selectedGenres.size})" else ""
                        else -> stringResource(R.string.onboarding_start_listening) + if (selectedArtists.isNotEmpty()) " (${selectedArtists.size})" else ""
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                    ),
                    color = Color.White,
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SetupFeatureBadge(
    text: String,
    cardBg: Color,
    cardBorder: Color,
    textColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
            .border(1.dp, cardBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = textColor,
        )
    }
}

@Composable
private fun SetupGenreChip(
    genre: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val accentColor = Color(0xFFA855F7)
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = Modifier
            .clip(shape)
            .background(
                if (isSelected) {
                    Brush.horizontalGradient(
                        listOf(
                            accentColor.copy(alpha = 0.35f),
                            Color(0xFF6366F1).copy(alpha = 0.30f),
                        )
                    )
                } else {
                    SolidColor(Color.White.copy(alpha = 0.08f))
                }
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                brush = if (isSelected) {
                    Brush.horizontalGradient(
                        listOf(accentColor, Color(0xFF6366F1))
                    )
                } else {
                    SolidColor(Color.White.copy(alpha = 0.16f))
                },
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(text = emoji, fontSize = 16.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                text = genre,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                ),
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.85f),
            )
        }
    }
}

@Composable
private fun SetupPopularArtistCard(
    artist: SeedArtist,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val accentColor = Color(0xFFA855F7)
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(shape)
            .background(
                if (isSelected) {
                    Brush.horizontalGradient(
                        listOf(
                            accentColor.copy(alpha = 0.35f),
                            Color(0xFF6366F1).copy(alpha = 0.30f),
                        )
                    )
                } else {
                    SolidColor(Color.White.copy(alpha = 0.08f))
                }
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                brush = if (isSelected) {
                    Brush.horizontalGradient(listOf(accentColor, Color(0xFF6366F1)))
                } else {
                    SolidColor(Color.White.copy(alpha = 0.16f))
                },
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) accentColor else Color.White.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = artist.name.take(1),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = Color.White,
                )
            }

            Spacer(Modifier.width(10.dp))

            Text(
                text = artist.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                ),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )

            if (isSelected) {
                Icon(
                    painter = painterResource(R.drawable.check),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun SetupArtistSearchResultRow(
    artist: ArtistItem,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val accentColor = Color(0xFFA855F7)
    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(shape)
            .background(
                if (isSelected) {
                    Brush.horizontalGradient(
                        listOf(
                            accentColor.copy(alpha = 0.35f),
                            Color(0xFF6366F1).copy(alpha = 0.28f),
                        )
                    )
                } else {
                    SolidColor(Color.White.copy(alpha = 0.08f))
                }
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                brush = if (isSelected) {
                    Brush.horizontalGradient(listOf(accentColor, Color(0xFF6366F1)))
                } else {
                    SolidColor(Color.White.copy(alpha = 0.16f))
                },
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (artist.thumbnail != null) {
                AsyncImage(
                    model = artist.thumbnail,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape),
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = artist.title.take(1),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = Color.White,
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = artist.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                ),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )

            if (isSelected) {
                Icon(
                    painter = painterResource(R.drawable.check),
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
