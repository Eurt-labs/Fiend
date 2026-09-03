/**
 * Fiend Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.fiend.music.ui.screens.setup

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.rotate
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
import com.fiend.music.LocalDatabase
import com.fiend.music.R
import com.fiend.music.constants.DarkModeKey
import com.fiend.music.constants.OnboardingArtistsKey
import com.fiend.music.constants.OnboardingCompletedKey
import com.fiend.music.constants.OnboardingGenresKey
import com.fiend.music.constants.OnboardingLanguagesKey
import com.fiend.music.db.MusicDatabase
import com.fiend.music.ui.screens.settings.DarkMode
import com.fiend.music.utils.TasteRecommendationSeeder
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
    "Bollywood & Desi" to "🪕",
    "Punjabi" to "🔥",
    "K-Pop" to "🌸",
    "Jazz & Blues" to "🎷",
    "Acoustic" to "🎻",
    "Metal" to "🤘",
    "Chill & Ambient" to "🌙",
    "Phonk" to "🏎️",
    "Latin" to "💃",
    "Classical" to "🎼",
    "Reggae & Tropical" to "🌴",
)

// Curated music languages
private val CURATED_LANGUAGES = listOf(
    "English" to "🌐",
    "Hindi" to "🇮🇳",
    "Punjabi" to "🎵",
    "Spanish" to "🇪🇸",
    "Korean" to "🇰🇷",
    "Japanese" to "🇯🇵",
    "French" to "🇫🇷",
    "Tamil" to "🎧",
    "Telugu" to "🎶",
    "German" to "🇩🇪",
    "Arabic" to "🌙",
    "Russian" to "❄️",
    "Portuguese" to "🇧🇷",
    "Turkish" to "🇹🇷",
)

// Curated popular seed artists
private data class SeedArtist(val name: String, val query: String)
private val POPULAR_ARTISTS = listOf(
    SeedArtist("The Weeknd", "The Weeknd"),
    SeedArtist("Taylor Swift", "Taylor Swift"),
    SeedArtist("Drake", "Drake"),
    SeedArtist("Billie Eilish", "Billie Eilish"),
    SeedArtist("Travis Scott", "Travis Scott"),
    SeedArtist("Arijit Singh", "Arijit Singh"),
    SeedArtist("Diljit Dosanjh", "Diljit Dosanjh"),
    SeedArtist("Imagine Dragons", "Imagine Dragons"),
    SeedArtist("Post Malone", "Post Malone"),
    SeedArtist("Dua Lipa", "Dua Lipa"),
    SeedArtist("Kendrick Lamar", "Kendrick Lamar"),
    SeedArtist("Bruno Mars", "Bruno Mars"),
    SeedArtist("Ed Sheeran", "Ed Sheeran"),
    SeedArtist("Ariana Grande", "Ariana Grande"),
    SeedArtist("Coldplay", "Coldplay"),
    SeedArtist("Eminem", "Eminem"),
    SeedArtist("BTS", "BTS"),
    SeedArtist("Olivia Rodrigo", "Olivia Rodrigo"),
    SeedArtist("Lana Del Rey", "Lana Del Rey"),
    SeedArtist("SZA", "SZA"),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetupScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val database = LocalDatabase.current
    val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.OFF)
    val isSystemDark = isSystemInDarkTheme()
    val isDark = remember(darkTheme, isSystemDark) {
        if (darkTheme == DarkMode.AUTO) isSystemDark else darkTheme == DarkMode.ON
    }

    val (_, setOnboardingCompleted) = rememberPreference(OnboardingCompletedKey, defaultValue = false)
    val (_, setOnboardingGenres) = rememberPreference(OnboardingGenresKey, defaultValue = "")
    val (_, setOnboardingLanguages) = rememberPreference(OnboardingLanguagesKey, defaultValue = "")
    val (_, setOnboardingArtists) = rememberPreference(OnboardingArtistsKey, defaultValue = "")

    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    var currentStep by rememberSaveable { mutableIntStateOf(0) }
    val selectedGenres = remember { mutableStateListOf<String>() }
    val selectedLanguages = remember { mutableStateListOf<String>() }
    val selectedArtists = remember { mutableStateListOf<String>() }

    // Search state for artists
    var searchQuery by remember { mutableStateOf("") }
    val searchResults = remember { mutableStateListOf<ArtistItem>() }
    var isSearching by remember { mutableStateOf(false) }
    var searchJob by remember { mutableStateOf<Job?>(null) }

    // Calibration & seeding state
    var isSeeding by remember { mutableStateOf(false) }
    var seedingProgressText by remember { mutableStateOf("") }

    // Live search debouncer for YouTube Music artists
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

    // Algorithm execution to seed database with liked songs, artists, events, and related tracks
    val startTasteSeedingAndFinish: (Boolean) -> Unit = { isSkip ->
        coroutineScope.launch {
            isSeeding = true
            seedingProgressText = "Calibrating your music universe…"

            // Save taste preferences
            setOnboardingGenres(selectedGenres.joinToString(","))
            setOnboardingLanguages(selectedLanguages.joinToString(","))
            setOnboardingArtists(selectedArtists.joinToString(","))

            // Run recommendation seeding algorithm into Room database
            val seedGenres = if (selectedGenres.isNotEmpty()) selectedGenres.toList() else if (isSkip) listOf("Pop") else emptyList()
            val seedLanguages = selectedLanguages.toList()
            val seedArtists = if (selectedArtists.isNotEmpty()) selectedArtists.toList() else if (isSkip) listOf("The Weeknd") else emptyList()

            TasteRecommendationSeeder.seedUserTaste(
                database = database,
                genres = seedGenres,
                languages = seedLanguages,
                artists = seedArtists,
                onProgress = { status ->
                    seedingProgressText = status
                }
            )

            setOnboardingCompleted(true)
            onComplete()
        }
    }

    // Dynamic background and theme styling
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val bgColor = if (isDark) Color(0xFF0D0D12) else Color(0xFFF6F5FA)
    val cardBg = if (isDark) Color(0xFF1B1B24).copy(alpha = 0.75f) else Color.White.copy(alpha = 0.85f)
    val cardBorder = if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f)
    val primaryText = if (isDark) Color.White else Color(0xFF111115)
    val secondaryText = if (isDark) Color.White.copy(alpha = 0.65f) else Color(0xFF111115).copy(alpha = 0.65f)

    // Ambient floating background glows
    val infiniteTransition = rememberInfiniteTransition(label = "ambientGlow")
    val glowOffset1 by infiniteTransition.animateFloat(
        initialValue = -70f,
        targetValue = 70f,
        animationSpec = infiniteRepeatable(tween(7000, easing = LinearEasing), RepeatMode.Reverse),
        label = "glow1"
    )
    val vinylRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "vinylRotation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Ambient background glow orbs matching current theme
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopStart)
                .scale(1f + glowOffset1 / 300f)
                .blur(100.dp)
                .background(Brush.radialGradient(listOf(primaryColor.copy(alpha = if (isDark) 0.35f else 0.20f), Color.Transparent)))
        )

        if (isSeeding) {
            // SEEDING / CALIBRATION SCREEN OVERLAY
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Spinning Vinyl Music Disc
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .shadow(32.dp, CircleShape, spotColor = primaryColor.copy(0.6f))
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    primaryColor.copy(alpha = 0.90f),
                                    Color(0xFF111115),
                                )
                            )
                        )
                        .border(2.dp, primaryColor.copy(alpha = 0.5f), CircleShape)
                        .rotate(vinylRotation),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(primaryColor)
                            .border(1.5.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.music_note),
                            contentDescription = null,
                            tint = onPrimaryColor,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }

                Spacer(Modifier.height(36.dp))

                Text(
                    text = stringResource(R.string.onboarding_tuning_title),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                    ),
                    color = primaryText,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(14.dp))

                Text(
                    text = seedingProgressText.ifEmpty { stringResource(R.string.onboarding_curating) },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 22.sp,
                    ),
                    color = secondaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )

                Spacer(Modifier.height(32.dp))

                CircularProgressIndicator(
                    color = primaryColor,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(36.dp),
                )
            }
        } else {
            // TASTE DISCOVERY MAIN CONTENT
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                // Header Bar with Step Progress Indicator & Skip Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Step Progress Bars (3 Steps: Genres -> Languages -> Artists)
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
                                    .width(if (isActive) 28.dp else 10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isActive) primaryColor
                                        else if (isDone) primaryColor.copy(alpha = 0.5f)
                                        else primaryText.copy(alpha = 0.16f)
                                    )
                            )
                        }
                    }

                    // Skip Button
                    TextButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            startTasteSeedingAndFinish(true)
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

                Spacer(Modifier.height(6.dp))

                // Animated Flow Between Taste Profiling Steps
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
                    label = "tasteStep",
                ) { step ->
                    when (step) {
                        // STEP 0: Pick Music Genres & Vibes
                        0 -> {
                            Column(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = stringResource(R.string.onboarding_step_vibe),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = (-0.5).sp,
                                    ),
                                    color = primaryText,
                                )

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = stringResource(R.string.onboarding_step_vibe_desc),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = secondaryText,
                                )

                                Spacer(Modifier.height(18.dp))

                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(bottom = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    item {
                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalArrangement = Arrangement.spacedBy(10.dp),
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            CURATED_GENRES.forEach { (genre, emoji) ->
                                                val isSelected = selectedGenres.contains(genre)
                                                SetupTasteChip(
                                                    title = genre,
                                                    icon = emoji,
                                                    isSelected = isSelected,
                                                    primaryColor = primaryColor,
                                                    onPrimaryColor = onPrimaryColor,
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        if (isSelected) selectedGenres.remove(genre)
                                                        else selectedGenres.add(genre)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // STEP 1: Pick Music Languages / Regions
                        1 -> {
                            Column(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = stringResource(R.string.onboarding_step_languages),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = (-0.5).sp,
                                    ),
                                    color = primaryText,
                                )

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = stringResource(R.string.onboarding_step_languages_desc),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = secondaryText,
                                )

                                Spacer(Modifier.height(18.dp))

                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(bottom = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    item {
                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalArrangement = Arrangement.spacedBy(10.dp),
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            CURATED_LANGUAGES.forEach { (language, flag) ->
                                                val isSelected = selectedLanguages.contains(language)
                                                SetupTasteChip(
                                                    title = language,
                                                    icon = flag,
                                                    isSelected = isSelected,
                                                    primaryColor = primaryColor,
                                                    onPrimaryColor = onPrimaryColor,
                                                    onClick = {
                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                        if (isSelected) selectedLanguages.remove(language)
                                                        else selectedLanguages.add(language)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // STEP 2: Pick Favorite Artists (with Live YouTube Search)
                        2 -> {
                            Column(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = stringResource(R.string.onboarding_step_artists),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = (-0.5).sp,
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

                                // Modern Glass Search Bar
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
                                            cursorBrush = SolidColor(primaryColor),
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
                                                color = primaryColor,
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

                                // Live YouTube Search Results OR Curated Artists Grid
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
                                                primaryColor = primaryColor,
                                                onPrimaryColor = onPrimaryColor,
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
                                                primaryColor = primaryColor,
                                                onPrimaryColor = onPrimaryColor,
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

                // Bottom Action Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(16.dp, RoundedCornerShape(28.dp), spotColor = primaryColor.copy(0.4f))
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    primaryColor,
                                    primaryColor.copy(alpha = 0.85f),
                                )
                            )
                        )
                        .border(1.2.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(28.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (currentStep < 2) {
                                currentStep++
                            } else {
                                startTasteSeedingAndFinish(false)
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = when (currentStep) {
                            0 -> stringResource(R.string.onboarding_continue) + if (selectedGenres.isNotEmpty()) " (${selectedGenres.size})" else ""
                            1 -> stringResource(R.string.onboarding_continue) + if (selectedLanguages.isNotEmpty()) " (${selectedLanguages.size})" else ""
                            else -> stringResource(R.string.onboarding_start_listening) + if (selectedArtists.isNotEmpty()) " (${selectedArtists.size})" else ""
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                        ),
                        color = onPrimaryColor,
                    )
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SetupTasteChip(
    title: String,
    icon: String,
    isSelected: Boolean,
    primaryColor: Color,
    onPrimaryColor: Color,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = Modifier
            .clip(shape)
            .background(
                if (isSelected) {
                    primaryColor
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.65f)
                }
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) primaryColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 11.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                ),
                color = if (isSelected) onPrimaryColor else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun SetupPopularArtistCard(
    artist: SeedArtist,
    isSelected: Boolean,
    primaryColor: Color,
    onPrimaryColor: Color,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(shape)
            .background(
                if (isSelected) {
                    primaryColor.copy(alpha = 0.20f)
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.50f)
                }
            )
            .border(
                width = if (isSelected) 1.8.dp else 1.dp,
                color = if (isSelected) primaryColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f),
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
                        if (isSelected) primaryColor else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = artist.name.take(1),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = if (isSelected) onPrimaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.width(10.dp))

            Text(
                text = artist.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )

            if (isSelected) {
                Icon(
                    painter = painterResource(R.drawable.check),
                    contentDescription = null,
                    tint = primaryColor,
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
    primaryColor: Color,
    onPrimaryColor: Color,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(shape)
            .background(
                if (isSelected) {
                    primaryColor.copy(alpha = 0.20f)
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.50f)
                }
            )
            .border(
                width = if (isSelected) 1.8.dp else 1.dp,
                color = if (isSelected) primaryColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.30f),
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
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = artist.title.take(1),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = artist.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )

            if (isSelected) {
                Icon(
                    painter = painterResource(R.drawable.check),
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
