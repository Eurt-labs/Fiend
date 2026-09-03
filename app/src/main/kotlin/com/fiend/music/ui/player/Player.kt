/**
 * Fiend Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.fiend.music.ui.player

import androidx.activity.compose.BackHandler
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.fiend.music.ui.liquidglass.SoundwaveScrubber
import android.content.Intent
import android.content.res.Configuration
import android.view.WindowManager
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import com.fiend.music.ui.utils.fadingEdge
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import com.fiend.music.LocalNavController
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.fiend.music.LocalDatabase
import com.fiend.music.LocalDownloadUtil
import com.fiend.music.LocalPlayerConnection
import com.fiend.music.R
import com.fiend.music.constants.CropAlbumArtKey
import com.fiend.music.constants.DarkModeKey
import com.fiend.music.constants.HideNavigationBarKey
import com.fiend.music.constants.HidePlayerThumbnailKey
import com.fiend.music.constants.HideStatusBarOnFullscreenKey
import com.fiend.music.constants.KeepScreenOn
import com.fiend.music.constants.PlayerBackgroundStyle
import com.fiend.music.constants.PlayerBackgroundStyleKey
import com.fiend.music.constants.PlayerButtonsStyle
import com.fiend.music.constants.PlayerButtonsStyleKey
import com.fiend.music.constants.PlayerHorizontalPadding
import com.fiend.music.constants.QueuePeekHeight
import com.fiend.music.constants.SleepTimerDefaultKey
import com.fiend.music.constants.SleepTimerFadeOutKey
import com.fiend.music.constants.SleepTimerStopAfterCurrentSongKey
import com.fiend.music.constants.SliderStyle
import com.fiend.music.constants.SliderStyleKey
import com.fiend.music.constants.SquigglySliderKey
import com.fiend.music.constants.ThumbnailCornerRadius
import com.fiend.music.constants.UseNewPlayerDesignKey
import com.fiend.music.LocalDatabase
import com.fiend.music.db.entities.LyricsEntity
import com.fiend.music.extensions.metadata
import com.fiend.music.extensions.togglePlayPause
import com.fiend.music.extensions.toggleRepeatMode
import com.fiend.music.models.MediaMetadata
import com.fiend.music.ui.component.BottomSheet
import com.fiend.music.ui.component.BottomSheetState
import com.fiend.music.ui.component.LocalBottomSheetPageState
import com.fiend.music.ui.component.LocalMenuState
import com.fiend.music.ui.component.Lyrics
import com.fiend.music.ui.component.PlayerSliderTrack
import com.fiend.music.ui.component.ResizableIconButton
import com.fiend.music.ui.component.SquigglySlider
import com.fiend.music.ui.component.WavySlider
import com.fiend.music.ui.component.rememberBottomSheetState
import com.fiend.music.ui.menu.PlayerMenu
import com.fiend.music.ui.screens.settings.DarkMode
import com.fiend.music.ui.theme.PlayerColorExtractor
import com.fiend.music.ui.liquidglass.AppleMusicBackground
import com.fiend.music.ui.liquidglass.LiquidGlassPill
import com.fiend.music.ui.theme.PlayerSliderColors
import com.fiend.music.ui.utils.ShowMediaInfo
import com.fiend.music.ui.utils.ShowOffsetDialog
import com.fiend.music.utils.dataStore
import com.fiend.music.utils.makeTimeString
import com.fiend.music.utils.rememberEnumPreference
import com.fiend.music.utils.rememberPreference
import com.fiend.music.utils.safeDataStoreEdit
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.roundToInt
import com.fiend.music.ui.component.Icon as MIcon
import com.fiend.music.constants.SleepTimerDefaultKey
import com.fiend.music.constants.SleepTimerFadeOutKey
import com.fiend.music.constants.SleepTimerStopAfterCurrentSongKey
import com.fiend.music.constants.ShowLyricsKey
import com.fiend.music.lyrics.LyricsWithProvider
import kotlinx.coroutines.withTimeoutOrNull


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetPlayer(
    state: BottomSheetState,
    navController: NavController,
    modifier: Modifier = Modifier,
    pureBlack: Boolean,
) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val menuState = LocalMenuState.current
    val sleepTimerDefaultSetTemplate = stringResource(R.string.sleep_timer_default_set)
    val copiedTitleStr = stringResource(R.string.copied_title)
    val copiedArtistStr = stringResource(R.string.copied_artist)
    val bottomSheetPageState = LocalBottomSheetPageState.current
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val database = LocalDatabase.current

    val (useNewPlayerDesign, onUseNewPlayerDesignChange) =
        rememberPreference(
            UseNewPlayerDesignKey,
            defaultValue = true,
        )
    val (hidePlayerThumbnail, onHidePlayerThumbnailChange) = rememberPreference(HidePlayerThumbnailKey, false)
    val (hideStatusBarOnFullscreen) = rememberPreference(HideStatusBarOnFullscreenKey, false)
    val (hideNavigationBar) = rememberPreference(HideNavigationBarKey, defaultValue = true)
    val cropAlbumArt by rememberPreference(CropAlbumArtKey, false)

    var showInlineLyrics by rememberSaveable {
        mutableStateOf(false)
    }

    var isFullScreen by rememberSaveable {
        mutableStateOf(false)
    }

    val playerBackground by rememberEnumPreference(
        key = PlayerBackgroundStyleKey,
        defaultValue = PlayerBackgroundStyle.DEFAULT,
    )
    val playerButtonsStyle by rememberEnumPreference(
        key = PlayerButtonsStyleKey,
        defaultValue = PlayerButtonsStyle.DEFAULT,
    )

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.OFF)
    
    val useDarkTheme =
        when (playerBackground) {
            PlayerBackgroundStyle.BLUR, PlayerBackgroundStyle.GRADIENT -> true
            PlayerBackgroundStyle.DEFAULT -> if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
        }

    val shouldUseDarkButtonColors =
        remember(playerBackground, useDarkTheme) {
            when (playerBackground) {
                PlayerBackgroundStyle.BLUR, PlayerBackgroundStyle.GRADIENT -> true
                PlayerBackgroundStyle.DEFAULT -> useDarkTheme
            }
        }

    val isPlaying by playerConnection.isPlaying.collectAsState()
    val isKeepScreenOn by rememberPreference(KeepScreenOn, false)
    val keepScreenOn = isPlaying && isKeepScreenOn

    DisposableEffect(playerBackground, state.isExpanded, useDarkTheme, keepScreenOn, isFullScreen, hideStatusBarOnFullscreen, hideNavigationBar) {
        val window = (context as? android.app.Activity)?.window
        if (window != null && state.isExpanded) {
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)

            val isLightAppearance = !useDarkTheme
            insetsController.isAppearanceLightStatusBars = isLightAppearance
            insetsController.isAppearanceLightNavigationBars = isLightAppearance

            if (isFullScreen && hideStatusBarOnFullscreen) {
                insetsController.hide(WindowInsetsCompat.Type.statusBars())
                insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                insetsController.show(WindowInsetsCompat.Type.statusBars())
            }

            if (hideNavigationBar) {
                insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                insetsController.hide(WindowInsetsCompat.Type.navigationBars())
            }

            if (keepScreenOn && state.isExpanded) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }

        onDispose {
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController.isAppearanceLightStatusBars = !useDarkTheme
                insetsController.isAppearanceLightNavigationBars = !useDarkTheme
                insetsController.show(WindowInsetsCompat.Type.statusBars())
                if (hideNavigationBar) {
                    insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    insetsController.hide(WindowInsetsCompat.Type.navigationBars())
                } else {
                    insetsController.show(WindowInsetsCompat.Type.navigationBars())
                }
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    BackHandler(enabled = state.isExpanded) {
        state.collapseSoft()
    }

    val onBackgroundColor =
        when (playerBackground) {
            PlayerBackgroundStyle.DEFAULT -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.onSurface
        }
    val useBlackBackground =
        remember(isSystemInDarkTheme, darkTheme, pureBlack) {
            val useDarkTheme =
                if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
            useDarkTheme && pureBlack
        }

    val playbackState by playerConnection.playbackState.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val currentSong by playerConnection.currentSong.collectAsStateWithLifecycle(initialValue = null)
    val automix by playerConnection.service.automixItems.collectAsStateWithLifecycle()
    val repeatMode by playerConnection.repeatMode.collectAsStateWithLifecycle()
    val canSkipPrevious by playerConnection.canSkipPrevious.collectAsStateWithLifecycle()
    val canSkipNext by playerConnection.canSkipNext.collectAsStateWithLifecycle()
    val isMuted by playerConnection.isMuted.collectAsStateWithLifecycle()
    val shuffleModeEnabled by playerConnection.shuffleModeEnabled.collectAsStateWithLifecycle()
    val currentLyrics by playerConnection.currentLyrics.collectAsStateWithLifecycle(initialValue = null)

    LaunchedEffect(showInlineLyrics) {
        context.safeDataStoreEdit { it[ShowLyricsKey] = showInlineLyrics }
    }

    // Active Lyrics Fetcher: ensures lyrics are fetched as soon as track starts or lyrics is expanded,
    // and terminates gracefully if not found or on network timeout so the loader never spins endlessly.
    LaunchedEffect(mediaMetadata?.id, showInlineLyrics, currentLyrics) {
        val currentMeta = mediaMetadata
        if (currentMeta != null && (showInlineLyrics || currentLyrics == null)) {
            val currentInDb = withContext(Dispatchers.IO) {
                database.lyrics(currentMeta.id).first()
            }
            if (currentInDb == null) {
                withContext(Dispatchers.IO) {
                    try {
                        val entryPoint = EntryPointAccessors.fromApplication(
                            context.applicationContext,
                            com.fiend.music.di.LyricsHelperEntryPoint::class.java,
                        )
                        val lyricsHelper = entryPoint.lyricsHelper()
                        val fetchedLyricsWithProvider = withTimeoutOrNull(10000L) {
                            lyricsHelper.getLyrics(currentMeta)
                        } ?: LyricsWithProvider(LyricsEntity.LYRICS_NOT_FOUND, "")
                        database.query {
                            upsert(
                                LyricsEntity(
                                    id = currentMeta.id,
                                    lyrics = fetchedLyricsWithProvider.lyrics,
                                    provider = fetchedLyricsWithProvider.provider,
                                )
                            )
                        }
                    } catch (e: Exception) {
                        database.query {
                            upsert(
                                LyricsEntity(
                                    id = currentMeta.id,
                                    lyrics = LyricsEntity.LYRICS_NOT_FOUND,
                                    provider = "",
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    val sliderStyle by rememberEnumPreference(SliderStyleKey, SliderStyle.DEFAULT)
    val squigglySlider by rememberPreference(SquigglySliderKey, defaultValue = false)

    // Listen Together state (reactive)

    // Cast state - safely access castConnectionHandler to prevent crashes during service lifecycle changes
    val castHandler =
        remember(playerConnection) {
            try {
                playerConnection.service.castConnectionHandler
            } catch (e: Exception) {
                null
            }
        }
    val isCasting by castHandler?.isCasting?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(false) }
    val castPosition by castHandler?.castPosition?.collectAsStateWithLifecycle() ?: remember { mutableLongStateOf(0L) }
    val castDuration by castHandler?.castDuration?.collectAsStateWithLifecycle() ?: remember { mutableLongStateOf(0L) }
    val castIsPlaying by castHandler?.castIsPlaying?.collectAsState() ?: remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state.isExpanded) {
        if (state.isExpanded) {
            delay(100)
            try {
                focusRequester.requestFocus()
            } catch (e: Exception) {
                // Ignore if focus request fails
            }
        }
    }

    // Use Cast state when casting, otherwise local player
    val effectiveIsPlaying = if (isCasting) castIsPlaying else isPlaying

    // Use State objects for position/duration to pass to MiniPlayer without causing recomposition
    // These states persist across playback state changes to ensure continuous progress updates.
    // Seed from the player's current values so re-entering composition on resume shows the real
    // time immediately instead of flashing 0:00 until the first poll fires. runCatching guards the
    // player-not-ready race; the poll loop corrects duration if it isn't known yet.
    val positionState = remember { mutableLongStateOf(runCatching { playerConnection.player.currentPosition }.getOrDefault(0L)) }
    val durationState = remember {
        mutableLongStateOf(
            (mediaMetadata?.duration?.takeIf { it > 0 }?.toLong()?.times(1000L))
                ?: runCatching { playerConnection.player.duration }.getOrDefault(0L).coerceAtLeast(0L),
        )
    }

    // Convenience accessors for local use
    var position by positionState
    var duration by durationState

    val effectivePosition by remember {
        derivedStateOf {
            if (isCasting) {
                castPosition
            } else {
                position
            }
        }
    }

    var sliderPosition by remember {
        mutableStateOf<Long?>(null)
    }
    // Track when we last manually set position to avoid Cast overwriting it
    var lastManualSeekTime by remember { mutableLongStateOf(0L) }

    var gradientColors by remember {
        mutableStateOf<List<Color>>(emptyList())
    }
    val gradientColorsCache = remember { mutableMapOf<String, List<Color>>() }

    if (!canSkipNext && automix.isNotEmpty()) {
        playerConnection.service.addToQueueAutomix(automix[0], 0)
    }

    val defaultGradientColors = listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surfaceVariant)
    val fallbackColor = MaterialTheme.colorScheme.surface.toArgb()

    LaunchedEffect(mediaMetadata?.id, playerBackground) {
        if (playerBackground == PlayerBackgroundStyle.GRADIENT) {
            val currentMetadata = mediaMetadata
            if (currentMetadata != null && currentMetadata.thumbnailUrl != null) {
                val cachedColors = gradientColorsCache[currentMetadata.id]
                if (cachedColors != null) {
                    gradientColors = cachedColors
                    return@LaunchedEffect
                }
                withContext(Dispatchers.IO) {
                    val request =
                        ImageRequest
                            .Builder(context)
                            .data(currentMetadata.thumbnailUrl)
                            .size(100, 100)
                            .allowHardware(false)
                            .memoryCacheKey("gradient_${currentMetadata.id}")
                            .build()

                    val result = runCatching { context.imageLoader.execute(request) }.getOrNull()
                    if (result != null) {
                        val bitmap = result.image?.toBitmap()
                        if (bitmap != null) {
                            val palette =
                                withContext(Dispatchers.Default) {
                                    Palette
                                        .from(bitmap)
                                        .maximumColorCount(8)
                                        .resizeBitmapArea(100 * 100)
                                        .generate()
                                }
                            val extractedColors =
                                PlayerColorExtractor.extractGradientColors(
                                    palette = palette,
                                    fallbackColor = fallbackColor,
                                )
                            gradientColorsCache[currentMetadata.id] = extractedColors
                            withContext(Dispatchers.Main) { gradientColors = extractedColors }
                        }
                    }
                }
            }
        } else {
            gradientColors = emptyList()
        }
    }

    val TextBackgroundColor by animateColorAsState(
        targetValue =
            when (playerBackground) {
                PlayerBackgroundStyle.DEFAULT -> MaterialTheme.colorScheme.onBackground
                PlayerBackgroundStyle.BLUR, PlayerBackgroundStyle.GRADIENT -> {
                    if (useDarkTheme) Color.White else MaterialTheme.colorScheme.onSurface
                }
            },
        label = "TextBackgroundColor",
    )

    val icBackgroundColor by animateColorAsState(
        targetValue =
            when (playerBackground) {
                PlayerBackgroundStyle.DEFAULT -> MaterialTheme.colorScheme.surface
                PlayerBackgroundStyle.BLUR, PlayerBackgroundStyle.GRADIENT -> {
                    if (useDarkTheme) Color.Black else MaterialTheme.colorScheme.surface
                }
            },
        label = "icBackgroundColor",
    )

    val (textButtonColor, iconButtonColor) =
        when {
            playerBackground == PlayerBackgroundStyle.BLUR ||
                playerBackground == PlayerBackgroundStyle.GRADIENT -> {
                when (playerButtonsStyle) {
                    PlayerButtonsStyle.DEFAULT -> {
                        Pair(TextBackgroundColor, Color.Black)
                    }

                    PlayerButtonsStyle.PRIMARY -> {
                        Pair(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onPrimary,
                        )
                    }

                    PlayerButtonsStyle.TERTIARY -> {
                        Pair(
                            MaterialTheme.colorScheme.tertiary,
                            MaterialTheme.colorScheme.onTertiary,
                        )
                    }
                }
            }

            else -> {
                when (playerButtonsStyle) {
                    PlayerButtonsStyle.DEFAULT -> {
                        if (useDarkTheme) {
                            Pair(TextBackgroundColor, Color.Black)
                        } else {
                            Pair(Color.Black, TextBackgroundColor)
                        }
                    }

                    PlayerButtonsStyle.PRIMARY -> {
                        Pair(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onPrimary,
                        )
                    }

                    PlayerButtonsStyle.TERTIARY -> {
                        Pair(
                            MaterialTheme.colorScheme.tertiary,
                            MaterialTheme.colorScheme.onTertiary,
                        )
                    }
                }
            }
        }

    // Separate colors for Previous/Next buttons in PRIMARY/TERTIARY modes
    val (sideButtonContainerColor, sideButtonContentColor) =
        when {
            playerBackground == PlayerBackgroundStyle.BLUR ||
                playerBackground == PlayerBackgroundStyle.GRADIENT -> {
                when (playerButtonsStyle) {
                    PlayerButtonsStyle.DEFAULT -> {
                        Pair(
                            TextBackgroundColor.copy(alpha = 0.2f),
                            TextBackgroundColor,
                        )
                    }

                    PlayerButtonsStyle.PRIMARY -> {
                        Pair(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }

                    PlayerButtonsStyle.TERTIARY -> {
                        Pair(
                            MaterialTheme.colorScheme.tertiaryContainer,
                            MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                }
            }

            else -> {
                when (playerButtonsStyle) {
                    PlayerButtonsStyle.DEFAULT -> {
                        Pair(
                            MaterialTheme.colorScheme.surfaceContainerHighest,
                            MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    PlayerButtonsStyle.PRIMARY -> {
                        Pair(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }

                    PlayerButtonsStyle.TERTIARY -> {
                        Pair(
                            MaterialTheme.colorScheme.tertiaryContainer,
                            MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                }
            }
        }

    val download by LocalDownloadUtil.current
        .getDownload(mediaMetadata?.id ?: "")
        .collectAsStateWithLifecycle(initialValue = null)

    val sleepTimerEnabled =
        remember(
            playerConnection.service.sleepTimer?.triggerTime,
            playerConnection.service.sleepTimer?.pauseWhenSongEnd,
        ) {
            playerConnection.service.sleepTimer?.isActive ?: false
        }

    var sleepTimerTimeLeft by remember {
        mutableLongStateOf(0L)
    }

    LaunchedEffect(sleepTimerEnabled) {
        if (sleepTimerEnabled) {
            while (isActive) {
                sleepTimerTimeLeft =
                    if (playerConnection.service.sleepTimer?.pauseWhenSongEnd == true) {
                        playerConnection.player.duration - playerConnection.player.currentPosition
                    } else {
                        (playerConnection.service.sleepTimer?.triggerTime ?: 0L) - System.currentTimeMillis()
                    }
                delay(1000L)
            }
        }
    }

    val scope = rememberCoroutineScope()
    var showSleepTimerDialog by remember {
        mutableStateOf(false)
    }

    val sleepTimerDefault by rememberPreference(SleepTimerDefaultKey, 30f)
    var sleepTimerValue by remember { mutableFloatStateOf(sleepTimerDefault) }
    val isAtDefault by remember {
        derivedStateOf { sleepTimerValue.roundToInt() == sleepTimerDefault.roundToInt() }
    }
    LaunchedEffect(sleepTimerDefault) { sleepTimerValue = sleepTimerDefault }
    val sleepTimerStopAfterCurrentSong by rememberPreference(SleepTimerStopAfterCurrentSongKey, false)
    val sleepTimerFadeOut by rememberPreference(SleepTimerFadeOutKey, false)


    if (showSleepTimerDialog) {
        AlertDialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { showSleepTimerDialog = false },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.bedtime),
                    contentDescription = null,
                )
            },
            title = { Text(stringResource(R.string.sleep_timer)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSleepTimerDialog = false
                        playerConnection.service.sleepTimer?.start(
                            minute = sleepTimerValue.roundToInt(),
                            stopAfterCurrentSong = sleepTimerStopAfterCurrentSong,
                            fadeOut = sleepTimerFadeOut,
                        )
                    },
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSleepTimerDialog = false },
                ) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text =
                            pluralStringResource(
                                R.plurals.minute,
                                sleepTimerValue.roundToInt(),
                                sleepTimerValue.roundToInt(),
                            ),
                        style = MaterialTheme.typography.bodyLarge,
                    )

                    Slider(
                        value = sleepTimerValue,
                        onValueChange = { sleepTimerValue = it },
                        valueRange = 5f..120f,
                        steps = (120 - 5) / 5 - 1,
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (isAtDefault) {
                            FilledIconButton(
                                onClick = {
                                    scope.launch {
                                        context.safeDataStoreEdit { settings ->
                                            settings[SleepTimerDefaultKey] = sleepTimerValue
                                        }
                                    }
                                    Toast.makeText(
                                        context,
                                        String.format(sleepTimerDefaultSetTemplate, sleepTimerValue.roundToInt()),
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                            ) {
                                Text(stringResource(R.string.set_as_default))
                            }
                        } else {
                            OutlinedIconButton(
                                onClick = {
                                    scope.launch {
                                        context.safeDataStoreEdit { settings ->
                                            settings[SleepTimerDefaultKey] = sleepTimerValue
                                        }
                                    }
                                    Toast.makeText(
                                        context,
                                        String.format(sleepTimerDefaultSetTemplate, sleepTimerValue.roundToInt()),
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                },
                            ) {
                                Text(stringResource(R.string.set_as_default))
                            }
                        }

                        OutlinedIconButton(
                            onClick = {
                                showSleepTimerDialog = false
                                playerConnection.service.sleepTimer?.start(minute = -1)
                            },
                        ) {
                            Text(stringResource(R.string.end_of_song))
                        }
                    }
                }
            },
        )
    }

    var showChoosePlaylistDialog by rememberSaveable {
        mutableStateOf(false)
    }

    // Position update - only for local playback
    // When casting, we use castPosition directly to avoid sync issues
    // Use isPlaying instead of playbackState to ensure continuous updates during playback
    LaunchedEffect(isPlaying, isCasting) {
        if (!isCasting && isPlaying) {
            while (isActive) {
                delay(100) // Update more frequently for smoother progress bar
                if (sliderPosition == null) { // Only update if user isn't dragging
                    position = playerConnection.player.currentPosition
                    // Don't clobber a valid (metadata-derived) duration with 0/UNSET mid-resolve.
                    playerConnection.player.duration.takeIf { it > 0 }?.let { duration = it }
                }
            }
        }
    }

    // Also update position when playback state changes (e.g., song change, seek)
    LaunchedEffect(playbackState, mediaMetadata?.id) {
        if (!isCasting) {
            position = playerConnection.player.currentPosition
            // Prefer the song's known duration (from metadata, available instantly from the restored
            // queue) so the slider range is right even when restored paused / before the stream
            // resolves; fall back to the player's duration once it is known.
            duration = (mediaMetadata?.duration?.takeIf { it > 0 }?.toLong()?.times(1000L))
                ?: playerConnection.player.duration
        }
    }

    // Auto-switch from repeat one to repeat all when song ends naturally
    var previousMediaId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(playbackState, mediaMetadata?.id) {
        val currentId = mediaMetadata?.id

        // Only switch from REPEAT_ONE to REPEAT_ALL when playback naturally ended
        // (i.e., the player transitioned to ENDED state and then moved to next track).
        // Do NOT switch on manual skips.
        if (currentId != null &&
            currentId != previousMediaId &&
            previousMediaId != null &&
            playbackState == Player.STATE_ENDED &&
            repeatMode == Player.REPEAT_MODE_ONE &&
            true) {
            playerConnection.player.setRepeatMode(Player.REPEAT_MODE_ALL)
        }

        previousMediaId = currentId
    }

    // When casting, use Cast position/duration directly
    // But wait a bit after manual seeks to let Cast catch up
    LaunchedEffect(isCasting, castPosition, castDuration) {
        if (isCasting && sliderPosition == null) {
            val timeSinceManualSeek = System.currentTimeMillis() - lastManualSeekTime
            if (timeSinceManualSeek > 1500) {
                // Only update from Cast if we haven't manually seeked recently
                position = castPosition
                if (castDuration > 0) duration = castDuration
            }
        }
    }

    val queueSheetState =
        rememberBottomSheetState(
            dismissedBound = 0.dp,
            expandedBound = state.expandedBound,
            collapsedBound = 0.dp,
            initialAnchor = 0,
        )

    val bottomSheetBackgroundColor =
        when (playerBackground) {
            PlayerBackgroundStyle.BLUR, PlayerBackgroundStyle.GRADIENT -> {
                MaterialTheme.colorScheme.surfaceContainer
            }

            else -> {
                if (useBlackBackground) {
                    Color.Black
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                }
            }
        }

    val backgroundAlpha = state.progress.coerceIn(0f, 1f)

    BottomSheet(
        state = state,
        modifier = modifier,
        background = {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(bottomSheetBackgroundColor),
            ) {
                // Render Apple Music fluid ambient blurred artwork background
                AppleMusicBackground(
                    artworkUrl = mediaMetadata?.thumbnailUrl,
                    dominantColor = if (gradientColors.isNotEmpty()) gradientColors[0] else (if (useDarkTheme) Color(0xFF1E1E24) else Color(0xFFE2E2E6)),
                    dimAlpha = if (useDarkTheme) 0.55f else 0.40f,
                    isDarkTheme = useDarkTheme,
                    modifier = Modifier.fillMaxSize().alpha(backgroundAlpha)
                )
            }
        },
        onDismiss =
            if (true) {
                {
                    playerConnection.service.clearAutomix()
                    playerConnection.player.stop()
                    playerConnection.player.clearMediaItems()
                }
            } else {
                null
            },
        collapsedContent = {
            NewMiniPlayer(progressState = com.fiend.music.ui.player.ProgressState(positionState, durationState), onClick = { state.expandSoft() })
        },
    ) {
        val controlsContent: @Composable ColumnScope.(MediaMetadata) -> Unit = { mediaMetadata ->
            // 1. Technical Audio Specs Badge
                val currentFormat by playerConnection.currentFormat.collectAsStateWithLifecycle(initialValue = null)
                val automixItems by playerConnection.service.automixItems.collectAsStateWithLifecycle(initialValue = emptyList())

                val audioQualityText = remember(currentFormat) {
                    val format = currentFormat ?: return@remember "Opus • 160 kbps • 48.0 kHz • Stereo"
                    val codec = when {
                        format.codecs.contains("opus", ignoreCase = true) || format.mimeType.contains("opus", ignoreCase = true) -> "Opus"
                        format.codecs.contains("mp4a", ignoreCase = true) || format.mimeType.contains("mp4", ignoreCase = true) -> "AAC"
                        format.codecs.contains("flac", ignoreCase = true) -> "FLAC"
                        else -> format.codecs.takeIf { it.isNotBlank() } ?: "Opus"
                    }
                    val bitrate = if (format.bitrate > 0) "${format.bitrate / 1000} kbps" else "160 kbps"
                    val sampleRate = if (format.sampleRate != null && format.sampleRate > 0) {
                        String.format(java.util.Locale.US, "%.1f kHz", format.sampleRate / 1000.0)
                    } else "48.0 kHz"
                    listOf(codec, bitrate, sampleRate, "Stereo").joinToString(" • ")
                }

                val automixText = remember(automixItems) {
                    if (automixItems.isNotEmpty()) {
                        "Automix • this song analysing... next analysing..."
                    } else {
                        "Automix • ready"
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = audioQualityText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            letterSpacing = 0.4.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        color = TextBackgroundColor.copy(alpha = 0.55f),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = automixText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            letterSpacing = 0.3.sp,
                        ),
                        color = TextBackgroundColor.copy(alpha = 0.40f),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }

                Spacer(Modifier.height(14.dp))

                // 2. Song Title & Artist (Left-aligned) + Heart & More buttons (Right-aligned)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                    ) {
                        Text(
                            text = mediaMetadata.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 23.sp,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = TextBackgroundColor,
                            modifier = Modifier.basicMarquee(iterations = 1, initialDelayMillis = 3000, velocity = 30.dp)
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = mediaMetadata.artists.joinToString(", ") { it.name },
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal,
                            ),
                            color = TextBackgroundColor.copy(alpha = 0.70f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.basicMarquee(iterations = 1, initialDelayMillis = 3000, velocity = 30.dp)
                        )
                    }

                    // Right action buttons (Heart & Queue)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Heart / Like
                        val isEpisode = currentSong?.song?.isEpisode == true
                        val isFavorite = if (isEpisode) currentSong?.song?.inLibrary != null else currentSong?.song?.liked == true
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                playerConnection.toggleLike()
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isFavorite) Color(0xFFFF3B6B).copy(alpha = 0.18f)
                                    else TextBackgroundColor.copy(alpha = 0.12f)
                                )
                                .border(
                                    1.dp,
                                    Brush.verticalGradient(
                                        if (isFavorite) listOf(
                                            Color(0xFFFF3B6B).copy(alpha = 0.60f),
                                            Color(0xFFFF3B6B).copy(alpha = 0.20f),
                                        ) else listOf(
                                            TextBackgroundColor.copy(alpha = 0.25f),
                                            TextBackgroundColor.copy(alpha = 0.05f),
                                        )
                                    ),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                painter = painterResource(if (isFavorite) R.drawable.favorite else R.drawable.favorite_border),
                                contentDescription = null,
                                tint = if (isFavorite) Color(0xFFFF3B6B) else TextBackgroundColor.copy(alpha = 0.85f),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Queue button (relocated beside Like per user request)
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                coroutineScope.launch {
                                    queueSheetState.expandSoft()
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(TextBackgroundColor.copy(alpha = 0.12f))
                                .border(
                                    1.dp,
                                    Brush.verticalGradient(
                                        listOf(
                                            TextBackgroundColor.copy(alpha = 0.25f),
                                            TextBackgroundColor.copy(alpha = 0.05f),
                                        )
                                    ),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.queue_music),
                                contentDescription = stringResource(R.string.queue),
                                tint = TextBackgroundColor.copy(alpha = 0.90f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                // 3. Slider
                androidx.compose.material3.Slider(
                    value = (sliderPosition ?: effectivePosition).toFloat(),
                    onValueChange = { newPosition ->
                        sliderPosition = newPosition.toLong()
                    },
                    onValueChangeFinished = {
                        val finalPosition = sliderPosition ?: return@Slider
                        if (isCasting) {
                            castHandler?.seekTo(finalPosition)
                            lastManualSeekTime = System.currentTimeMillis()
                        } else {
                            playerConnection.player.seekTo(finalPosition)
                        }
                        sliderPosition = null
                    },
                    valueRange = 0f..(if (duration != C.TIME_UNSET && duration > 0) duration.toFloat() else 1f),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    colors = androidx.compose.material3.SliderDefaults.colors(
                        thumbColor = TextBackgroundColor,
                        activeTrackColor = TextBackgroundColor,
                        inactiveTrackColor = TextBackgroundColor.copy(alpha = 0.2f)
                    )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = android.text.format.DateUtils.formatElapsedTime((sliderPosition ?: effectivePosition) / 1000),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextBackgroundColor.copy(alpha = 0.7f)
                    )
                    Text(
                        text = if (duration != C.TIME_UNSET) android.text.format.DateUtils.formatElapsedTime(duration / 1000) else "0:00",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextBackgroundColor.copy(alpha = 0.7f)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // 4. Playback Controls Row: Previous - Play/Pause - Next (Centered)
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    // Previous
                    IconButton(
                        onClick = playerConnection::seekToPrevious,
                        enabled = canSkipPrevious,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(TextBackgroundColor.copy(alpha = 0.12f))
                            .border(
                                1.dp,
                                Brush.verticalGradient(
                                    listOf(
                                        TextBackgroundColor.copy(alpha = 0.25f),
                                        TextBackgroundColor.copy(alpha = 0.05f),
                                    )
                                ),
                                CircleShape
                            )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.skip_previous),
                            contentDescription = null,
                            tint = TextBackgroundColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(Modifier.width(28.dp))

                    // Play/Pause
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(TextBackgroundColor.copy(alpha = 0.14f))
                            .border(
                                1.dp,
                                Brush.verticalGradient(
                                    listOf(
                                        TextBackgroundColor.copy(alpha = 0.30f),
                                        TextBackgroundColor.copy(alpha = 0.06f),
                                    )
                                ),
                                CircleShape
                            )
                            .clickable {
                                playerConnection.togglePlayPause()
                            }
                    ) {
                        Icon(
                            painter = painterResource(if (effectiveIsPlaying) R.drawable.pause else R.drawable.play),
                            contentDescription = null,
                            tint = TextBackgroundColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(Modifier.width(28.dp))

                    // Next
                    IconButton(
                        onClick = playerConnection::seekToNext,
                        enabled = canSkipNext,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(TextBackgroundColor.copy(alpha = 0.12f))
                            .border(
                                1.dp,
                                Brush.verticalGradient(
                                    listOf(
                                        TextBackgroundColor.copy(alpha = 0.25f),
                                        TextBackgroundColor.copy(alpha = 0.05f),
                                    )
                                ),
                                CircleShape
                            )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.skip_next),
                            contentDescription = null,
                            tint = TextBackgroundColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // 5. Bottom Row: Shuffle (left) - BottomLyricsHill (center) - Repeat (right)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Left Shuffle button
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            playerConnection.player.shuffleModeEnabled = !shuffleModeEnabled
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (shuffleModeEnabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                                else TextBackgroundColor.copy(alpha = 0.10f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (shuffleModeEnabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
                                else TextBackgroundColor.copy(alpha = 0.18f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.shuffle),
                            contentDescription = null,
                            tint = if (shuffleModeEnabled) MaterialTheme.colorScheme.primary else TextBackgroundColor.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Center Lyrics Hill: Displays mini live lyrics line-by-line with Apple Music animation
                    BottomLyricsHill(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .padding(horizontal = 10.dp),
                        contentColor = TextBackgroundColor,
                        lyrics = currentLyrics?.lyrics,
                        positionProvider = {
                            sliderPosition ?: if (isCasting) castPosition else playerConnection.player.currentPosition
                        },
                        isPlaying = effectiveIsPlaying,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showInlineLyrics = true
                        },
                    )

                    // Right Repeat button
                    val isRepeatActive = repeatMode != Player.REPEAT_MODE_OFF
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            playerConnection.player.toggleRepeatMode()
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (isRepeatActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                                else TextBackgroundColor.copy(alpha = 0.10f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isRepeatActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
                                else TextBackgroundColor.copy(alpha = 0.18f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            painter = painterResource(
                                when (repeatMode) {
                                    Player.REPEAT_MODE_ONE -> R.drawable.repeat_one
                                    else -> R.drawable.repeat
                                }
                            ),
                            contentDescription = null,
                            tint = if (isRepeatActive) MaterialTheme.colorScheme.primary else TextBackgroundColor.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
        }

        when (LocalConfiguration.current.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                // Calculate vertical padding like OuterTune
                val density = LocalDensity.current
                val verticalPadding =
                    max(
                        WindowInsets.systemBars.getTop(density),
                        WindowInsets.systemBars.getBottom(density),
                    )
                val verticalPaddingDp = with(density) { verticalPadding.toDp() }
                val verticalWindowInsets = WindowInsets(left = 0.dp, top = verticalPaddingDp, right = 0.dp, bottom = verticalPaddingDp)

                Row(
                    modifier =
                        Modifier
                            .windowInsetsPadding(
                                WindowInsets.systemBars.only(WindowInsetsSides.Horizontal).add(verticalWindowInsets),
                            ).padding(bottom = 24.dp)
                            .fillMaxSize(),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier =
                            Modifier
                                .weight(1f)
                                .nestedScroll(state.preUpPostDownNestedScrollConnection),
                    ) {
                        // Remember lambdas to prevent unnecessary recomposition
                        val currentSliderPosition by rememberUpdatedState(sliderPosition)
                        val sliderPositionProvider = remember { { currentSliderPosition } }
                        val isExpandedProvider = remember(state) { { state.isExpanded } }
                        AnimatedContent(
                            targetState = showInlineLyrics,
                            label = "Lyrics",
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                        ) { showLyrics ->
                            if (showLyrics) {
                                InlineLyricsView(
                                    mediaMetadata = mediaMetadata,
                                    showLyrics = showLyrics,
                                    positionProvider = { effectivePosition },
                                    onDismiss = { showInlineLyrics = false },
                                )
                            } else {
                                Thumbnail(
                                    sliderPositionProvider = sliderPositionProvider,
                                    modifier = Modifier.animateContentSize(),
                                    isPlayerExpanded = isExpandedProvider,
                                    isLandscape = true,
                                )
                            }
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier =
                            Modifier
                                .weight(if (showInlineLyrics) 0.65f else 1f, false)
                                .animateContentSize()
                                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top)),
                    ) {
                        Spacer(Modifier.weight(1f))

                        mediaMetadata?.let {
                            controlsContent(it)
                        }

                        Spacer(Modifier.weight(1f))
                    }
                }
            }

            else -> {
                val navBarsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                val bottomPadding by animateDpAsState(
                    targetValue = if (isFullScreen) 0.dp else maxOf(queueSheetState.collapsedBound, navBarsBottom),
                    label = "bottomPadding",
                )

                AnimatedContent(
                    targetState = showInlineLyrics,
                    label = "PlayerLyricsTransition",
                    transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                    modifier = Modifier.fillMaxSize(),
                ) { isLyricsMode ->
                    if (isLyricsMode) {
                        // FULL-SCREEN UNIFIED LYRICS VIEW (NO PARTITIONS, NO WHITE BANDS)
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // 1. Fluid Blurred Artwork Backdrop across whole player
                            if (!mediaMetadata?.thumbnailUrl.isNullOrEmpty()) {
                                AsyncImage(
                                    model = mediaMetadata?.thumbnailUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .scale(1.35f)
                                        .blur(50.dp),
                                )
                            }
                            // Dark Glass Scrim Overlay (ensures text and controls readability in both light & dark mode)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color.Black.copy(alpha = 0.45f),
                                                Color.Black.copy(alpha = 0.70f),
                                            )
                                        )
                                    )
                            )

                            // 2. Centered Lyrics View (padded to leave room for top bar and floating bottom controls)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
                                    .padding(
                                        top = 68.dp,
                                        bottom = 190.dp,
                                        start = 16.dp,
                                        end = 16.dp
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                val lyrics = remember(currentLyrics) { currentLyrics?.lyrics?.trim() }

                                when {
                                    lyrics == null -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                        ) {
                                            androidx.compose.material3.CircularProgressIndicator(
                                                color = Color.White,
                                                strokeWidth = 3.dp,
                                                modifier = Modifier.size(36.dp),
                                            )
                                            Spacer(Modifier.height(14.dp))
                                            Text(
                                                text = stringResource(R.string.lyrics_loading),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White.copy(alpha = 0.70f),
                                                textAlign = TextAlign.Center,
                                            )
                                        }
                                    }

                                    lyrics == LyricsEntity.LYRICS_NOT_FOUND || lyrics.isBlank() -> {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.padding(horizontal = 24.dp),
                                        ) {
                                            Text(
                                                text = stringResource(R.string.lyrics_not_found),
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.SemiBold,
                                                ),
                                                color = Color.White.copy(alpha = 0.85f),
                                                textAlign = TextAlign.Center,
                                            )
                                            Spacer(Modifier.height(14.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(20.dp))
                                                    .background(Color.White.copy(alpha = 0.15f))
                                                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                                                    .clickable {
                                                        mediaMetadata?.let { meta ->
                                                            coroutineScope.launch(Dispatchers.IO) {
                                                                try {
                                                                    database.query {
                                                                        currentLyrics?.let(::delete)
                                                                    }
                                                                    val entryPoint = EntryPointAccessors.fromApplication(
                                                                        context.applicationContext,
                                                                        com.fiend.music.di.LyricsHelperEntryPoint::class.java,
                                                                    )
                                                                    val lyricsHelper = entryPoint.lyricsHelper()
                                                                    val fetched = withTimeoutOrNull(10000L) {
                                                                        lyricsHelper.getLyrics(meta)
                                                                    } ?: LyricsWithProvider(LyricsEntity.LYRICS_NOT_FOUND, "")
                                                                    database.query {
                                                                        upsert(LyricsEntity(meta.id, fetched.lyrics, fetched.provider))
                                                                    }
                                                                } catch (e: Exception) {
                                                                    database.query {
                                                                        upsert(LyricsEntity(meta.id, LyricsEntity.LYRICS_NOT_FOUND, ""))
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    .padding(horizontal = 18.dp, vertical = 8.dp),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.sync),
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(16.dp),
                                                    )
                                                    Spacer(Modifier.width(6.dp))
                                                    Text(
                                                        text = stringResource(R.string.retry),
                                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                        color = Color.White,
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    else -> {
                                        ProvideTextStyle(
                                            value = MaterialTheme.typography.bodyLarge.copy(
                                                textAlign = TextAlign.Center,
                                                color = Color.White,
                                            ),
                                        ) {
                                            Lyrics(
                                                sliderPositionProvider = { effectivePosition },
                                                modifier = Modifier.fillMaxSize(),
                                                showLyrics = true,
                                            )
                                        }
                                    }
                                }
                            }

                            // 3. Top bar in Lyrics mode: [Collapse] - [TopLyricsHill Notch] - [Queue]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top))
                                    .padding(horizontal = 20.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                IconButton(
                                    onClick = { showInlineLyrics = false },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.12f))
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.expand_more),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                TopLyricsHill(
                                    contentColor = Color.White,
                                    onClick = { showInlineLyrics = false },
                                )

                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            queueSheetState.expandSoft()
                                        }
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.12f))
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.queue_music),
                                        contentDescription = stringResource(R.string.queue),
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            // 4. Floating bottom compact controls on the fluid backdrop
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                                    .padding(start = 24.dp, end = 24.dp, bottom = 12.dp)
                            ) {
                                mediaMetadata?.let { meta ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                            Text(
                                                text = meta.title,
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                    fontWeight = FontWeight.Bold,
                                                ),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                color = Color.White,
                                                modifier = Modifier.basicMarquee(iterations = 1, initialDelayMillis = 3000, velocity = 30.dp)
                                            )
                                            Spacer(Modifier.height(2.dp))
                                            Text(
                                                text = meta.artists.joinToString(", ") { it.name },
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White.copy(alpha = 0.70f),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.basicMarquee(iterations = 1, initialDelayMillis = 3000, velocity = 30.dp)
                                            )
                                        }

                                        val isEpisode = currentSong?.song?.isEpisode == true
                                        val isFavorite = if (isEpisode) currentSong?.song?.inLibrary != null else currentSong?.song?.liked == true
                                        IconButton(
                                            onClick = playerConnection::toggleLike,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(Color.White.copy(alpha = 0.12f))
                                        ) {
                                            Icon(
                                                painter = painterResource(if (isFavorite) R.drawable.favorite else R.drawable.favorite_border),
                                                contentDescription = null,
                                                tint = if (isFavorite) Color(0xFFFF3B6B) else Color.White.copy(alpha = 0.90f),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    // Seeker Slider
                                    androidx.compose.material3.Slider(
                                        value = (sliderPosition ?: effectivePosition).toFloat(),
                                        onValueChange = { newPosition ->
                                            sliderPosition = newPosition.toLong()
                                        },
                                        onValueChangeFinished = {
                                            val finalPosition = sliderPosition ?: return@Slider
                                            if (isCasting) {
                                                castHandler?.seekTo(finalPosition)
                                                lastManualSeekTime = System.currentTimeMillis()
                                            } else {
                                                playerConnection.player.seekTo(finalPosition)
                                            }
                                            sliderPosition = null
                                        },
                                        valueRange = 0f..(if (duration != C.TIME_UNSET && duration > 0) duration.toFloat() else 1f),
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = androidx.compose.material3.SliderDefaults.colors(
                                            thumbColor = Color.White,
                                            activeTrackColor = Color.White,
                                            inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                                        )
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = android.text.format.DateUtils.formatElapsedTime((sliderPosition ?: effectivePosition) / 1000),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.70f)
                                        )
                                        Text(
                                            text = if (duration != C.TIME_UNSET) android.text.format.DateUtils.formatElapsedTime(duration / 1000) else "0:00",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.70f)
                                        )
                                    }

                                    Spacer(Modifier.height(6.dp))

                                    // Compact Playback Controls Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        IconButton(
                                            onClick = playerConnection::seekToPrevious,
                                            enabled = canSkipPrevious,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(Color.White.copy(alpha = 0.12f))
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.skip_previous),
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }

                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(CircleShape)
                                                .background(Color.White.copy(alpha = 0.20f))
                                                .border(1.dp, Color.White.copy(alpha = 0.40f), CircleShape)
                                                .clickable {
                                                    playerConnection.togglePlayPause()
                                                }
                                        ) {
                                            Icon(
                                                painter = painterResource(if (effectiveIsPlaying) R.drawable.pause else R.drawable.play),
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(30.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = playerConnection::seekToNext,
                                            enabled = canSkipNext,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(Color.White.copy(alpha = 0.12f))
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.skip_next),
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // NORMAL EXPANDED PLAYER (APPLE MUSIC FADED HERO VIEW)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier =
                                Modifier
                                    .windowInsetsPadding(
                                        WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                                            .add(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                                    )
                                    .padding(bottom = maxOf(bottomPadding, 12.dp)),
                        ) {
                            // Top bar — "Now Playing"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                IconButton(
                                    onClick = { state.collapseSoft() },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(TextBackgroundColor.copy(alpha = 0.08f))
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.expand_more),
                                        contentDescription = null,
                                        tint = TextBackgroundColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Text(
                                    text = stringResource(R.string.now_playing),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = TextBackgroundColor,
                                )

                                IconButton(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        mediaMetadata?.let { meta ->
                                            menuState.show {
                                                PlayerMenu(
                                                    mediaMetadata = meta,
                                                    playerBottomSheetState = state,
                                                    onShowDetailsDialog = {
                                                        meta.id.let {
                                                            bottomSheetPageState.show {
                                                                ShowMediaInfo(it)
                                                            }
                                                        }
                                                    },
                                                    onDismiss = menuState::dismiss,
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(TextBackgroundColor.copy(alpha = 0.08f))
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.more_horiz),
                                        contentDescription = null,
                                        tint = TextBackgroundColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            // Apple Music Faded Hero Artwork View (instead of boxed thumbnail)
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .nestedScroll(state.preUpPostDownNestedScrollConnection)
                                    .padding(horizontal = 24.dp),
                            ) {
                                AsyncImage(
                                    model = mediaMetadata?.thumbnailUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(24.dp))
                                        .fadingEdge(bottom = 140.dp),
                                )
                            }

                            mediaMetadata?.let {
                                controlsContent(it)
                            }

                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = !isFullScreen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Top) + slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        ) {
            Queue(
                state = queueSheetState,
                playerBottomSheetState = state,
                background =
                    if (useBlackBackground) {
                        Color.Black
                    } else {
                        MaterialTheme.colorScheme.surfaceContainer
                    },
                onBackgroundColor = onBackgroundColor,
                TextBackgroundColor = TextBackgroundColor,
                textButtonColor = textButtonColor,
                iconButtonColor = iconButtonColor,
                pureBlack = pureBlack,
                showInlineLyrics = showInlineLyrics,
                playerBackground = playerBackground,
                onToggleLyrics = {
                    showInlineLyrics = !showInlineLyrics
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InlineLyricsView(
    mediaMetadata: MediaMetadata?,
    showLyrics: Boolean,
    positionProvider: () -> Long,
    onDismiss: () -> Unit = {},
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val currentLyrics by playerConnection.currentLyrics.collectAsStateWithLifecycle(initialValue = null)
    val queueWindows by playerConnection.queueWindows.collectAsStateWithLifecycle(initialValue = emptyList())
    val currentWindowIndex by playerConnection.currentWindowIndex.collectAsStateWithLifecycle(initialValue = -1)
    val lyrics = remember(currentLyrics) { currentLyrics?.lyrics?.trim() }
    val context = LocalContext.current
    val database = LocalDatabase.current
    val coroutineScope = rememberCoroutineScope()

    var appInForeground by remember {
        mutableStateOf(
            ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED),
        )
    }
    DisposableEffect(Unit) {
        val lifecycle = ProcessLifecycleOwner.get().lifecycle
        val observer =
            LifecycleEventObserver { _, _ ->
                appInForeground = lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
            }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    val nextMetadata =
        remember(queueWindows, currentWindowIndex) {
            if (currentWindowIndex >= 0 && currentWindowIndex + 1 < queueWindows.size) {
                queueWindows[currentWindowIndex + 1].mediaItem.metadata
            } else {
                null
            }
        }

    LaunchedEffect(mediaMetadata?.id, currentLyrics) {
        if (mediaMetadata != null && currentLyrics == null) {
            withContext(Dispatchers.IO) {
                try {
                    val entryPoint =
                        EntryPointAccessors.fromApplication(
                            context.applicationContext,
                            com.fiend.music.di.LyricsHelperEntryPoint::class.java,
                        )
                    val lyricsHelper = entryPoint.lyricsHelper()
                    val fetchedLyricsWithProvider = withTimeoutOrNull(10000L) {
                        lyricsHelper.getLyrics(mediaMetadata)
                    } ?: LyricsWithProvider(LyricsEntity.LYRICS_NOT_FOUND, "")
                    database.query {
                        upsert(LyricsEntity(mediaMetadata.id, fetchedLyricsWithProvider.lyrics, fetchedLyricsWithProvider.provider))
                    }
                } catch (e: Exception) {
                    database.query {
                        upsert(LyricsEntity(mediaMetadata.id, LyricsEntity.LYRICS_NOT_FOUND, ""))
                    }
                }
            }
        }
    }

    // Prefetch lyrics for the next queue item only while the lyrics pane is visible, the app is in the
    // foreground, and the current track's lyrics row has finished loading (avoids competing with the
    // active fetch).
    LaunchedEffect(
        nextMetadata?.id,
        showLyrics,
        appInForeground,
        mediaMetadata?.id,
        currentLyrics,
    ) {
        if (!showLyrics || !appInForeground || nextMetadata == null) return@LaunchedEffect
        val loadedForCurrent =
            currentLyrics?.let { lyrics ->
                mediaMetadata == null || lyrics.id == mediaMetadata.id
            } == true
        if (mediaMetadata != null && !loadedForCurrent) return@LaunchedEffect
        val nextId = nextMetadata.id
        delay(400)
        if (!showLyrics || !appInForeground || !isActive) return@LaunchedEffect
        withContext(Dispatchers.IO) {
            try {
                val existing = database.lyrics(nextId).first()
                if (existing != null) return@withContext
                val entryPoint =
                    EntryPointAccessors.fromApplication(
                        context.applicationContext,
                        com.fiend.music.di.LyricsHelperEntryPoint::class.java,
                    )
                val lyricsHelper = entryPoint.lyricsHelper()
                val fetched = lyricsHelper.getLyrics(nextMetadata)
                database.query {
                    upsert(LyricsEntity(nextId, fetched.lyrics, fetched.provider))
                }
            } catch (_: Exception) {
            }
        }
    }

    // Render ExpandedLyricsCard matching Image 2
    ExpandedLyricsCard(
        mediaMetadata = mediaMetadata,
        showLyrics = showLyrics,
        positionProvider = positionProvider,
        onDismiss = onDismiss,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun PlayerMoreMenuButton(
    mediaMetadata: MediaMetadata,
    state: BottomSheetState,
    textButtonColor: Color,
    iconButtonColor: Color,
) {
    val navController = LocalNavController.current
    val menuState = LocalMenuState.current
    val bottomSheetPageState = LocalBottomSheetPageState.current

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(textButtonColor)
                .clickable {
                    menuState.show {
                        PlayerMenu(
                            mediaMetadata = mediaMetadata,
                            playerBottomSheetState = state,
                            onShowDetailsDialog = {
                                mediaMetadata.id.let {
                                    bottomSheetPageState.show {
                                        ShowMediaInfo(it)
                                    }
                                }
                            },
                            onDismiss = menuState::dismiss,
                        )
                    }
                },
    ) {
        Image(
            painter = painterResource(R.drawable.more_horiz),
            contentDescription = null,
            colorFilter = ColorFilter.tint(iconButtonColor),
        )
    }
}
