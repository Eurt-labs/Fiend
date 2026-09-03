/**
 * Fiend Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.fiend.music.ui.player

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import com.fiend.music.ui.liquidglass.GlassmorphicLinearLoader
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.fiend.music.LocalPlayerConnection
import com.fiend.music.R
import com.fiend.music.db.entities.LyricsEntity
import com.fiend.music.models.MediaMetadata
import com.fiend.music.ui.component.Lyrics

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow
import com.fiend.music.lyrics.LyricsUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

data class MiniLyricsDisplay(
    val text: String,
    val isLive: Boolean,
    val isLoading: Boolean = false,
)

/**
 * Bottom "Lyrics Hill" Composable matching Image 1:
 * Rises up smoothly from the bottom between Shuffle and Repeat controls,
 * with an organic S-curve dome, subtle translucent glass fill, highlight stroke,
 * and live line-by-line mini lyrics with Apple Music spring animation transitions.
 * Tapping opens the expanded fullscreen lyrics view.
 */
@Composable
fun BottomLyricsHill(
    modifier: Modifier = Modifier,
    contentColor: Color = Color.White,
    lyrics: String? = null,
    positionProvider: () -> Long = { 0L },
    isPlaying: Boolean = false,
    onClick: () -> Unit,
) {
    val playerConnection = LocalPlayerConnection.current
    val currentLyricsEntity by playerConnection?.currentLyrics?.collectAsStateWithLifecycle(initialValue = null)
        ?: remember { mutableStateOf<LyricsEntity?>(null) }
    val effectiveLyrics = lyrics ?: currentLyricsEntity?.lyrics?.trim()

    val effectivePositionProvider: () -> Long = remember(positionProvider, playerConnection) {
        {
            val explicit = positionProvider()
            if (explicit != 0L) explicit else (playerConnection?.player?.currentPosition ?: 0L)
        }
    }

    val effectiveIsPlaying = isPlaying || (playerConnection?.player?.isPlaying == true)

    val parsedLines = remember(effectiveLyrics) {
        if (effectiveLyrics.isNullOrBlank() || effectiveLyrics == LyricsEntity.LYRICS_NOT_FOUND) {
            emptyList()
        } else if (effectiveLyrics.startsWith("[")) {
            LyricsUtils.parseLyrics(effectiveLyrics).filter { it.text.isNotBlank() }
        } else {
            emptyList()
        }
    }

    var currentLineIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(parsedLines, effectiveIsPlaying, effectiveLyrics) {
        if (parsedLines.isEmpty()) {
            currentLineIndex = -1
            return@LaunchedEffect
        }
        while (isActive) {
            val pos = effectivePositionProvider()
            val idx = LyricsUtils.findCurrentLineIndex(parsedLines, pos)
            if (idx != currentLineIndex) {
                currentLineIndex = idx
            }
            delay(50)
        }
    }

    val lyricsLoadingStr = stringResource(R.string.lyrics_loading)
    val lyricsDefaultStr = stringResource(R.string.lyrics)

    val displayState = remember(effectiveLyrics, parsedLines, currentLineIndex) {
        when {
            effectiveLyrics == null -> MiniLyricsDisplay(
                text = lyricsLoadingStr,
                isLive = false,
                isLoading = true,
            )
            effectiveLyrics == LyricsEntity.LYRICS_NOT_FOUND || effectiveLyrics.isBlank() -> MiniLyricsDisplay(
                text = lyricsDefaultStr,
                isLive = false,
                isLoading = false,
            )
            parsedLines.isNotEmpty() -> {
                if (currentLineIndex in parsedLines.indices) {
                    val lineText = parsedLines[currentLineIndex].text.trim()
                    MiniLyricsDisplay(
                        text = if (lineText.isNotBlank()) lineText else "♪",
                        isLive = true,
                        isLoading = false,
                    )
                } else if (currentLineIndex < 0) {
                    MiniLyricsDisplay(
                        text = "♪",
                        isLive = true,
                        isLoading = false,
                    )
                } else {
                    MiniLyricsDisplay(
                        text = lyricsDefaultStr,
                        isLive = false,
                        isLoading = false,
                    )
                }
            }
            else -> {
                // Unsynced plain text lyrics: show first non-blank line
                val firstLine = effectiveLyrics.lines().firstOrNull { it.isNotBlank() }?.trim()
                MiniLyricsDisplay(
                    text = firstLine ?: lyricsDefaultStr,
                    isLive = true,
                    isLoading = false,
                )
            }
        }
    }

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 140.dp)
            .height(44.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val startX = 0f
            val endX = w
            val midX = w / 2f
            val shoulderWidth = 24.dp.toPx()
            val leftCrest = shoulderWidth.coerceAtMost(midX - 24.dp.toPx())
            val rightCrest = (w - shoulderWidth).coerceAtLeast(midX + 24.dp.toPx())
            val peakY = 2.dp.toPx()

            val curvePath = Path().apply {
                moveTo(startX, h)
                cubicTo(
                    startX + (leftCrest - startX) * 0.45f, h,
                    leftCrest - (leftCrest - startX) * 0.40f, peakY,
                    leftCrest, peakY
                )
                lineTo(rightCrest, peakY)
                cubicTo(
                    rightCrest + (endX - rightCrest) * 0.40f, peakY,
                    endX - (endX - rightCrest) * 0.45f, h,
                    endX, h
                )
            }

            val fillPath = Path().apply {
                addPath(curvePath)
                lineTo(endX, h)
                lineTo(startX, h)
                close()
            }

            // Translucent glass fill
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    listOf(
                        contentColor.copy(alpha = 0.22f),
                        contentColor.copy(alpha = 0.07f),
                    ),
                    startY = peakY,
                    endY = h,
                )
            )

            // Top highlight stroke along the curve
            drawPath(
                path = curvePath,
                brush = Brush.verticalGradient(
                    listOf(
                        contentColor.copy(alpha = 0.52f),
                        contentColor.copy(alpha = 0.15f),
                    ),
                    startY = peakY,
                    endY = h,
                ),
                style = Stroke(
                    width = 1.6.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center,
        ) {
            AnimatedContent(
                targetState = displayState,
                transitionSpec = {
                    (slideInVertically(
                        animationSpec = spring(
                            dampingRatio = 0.82f,
                            stiffness = Spring.StiffnessMediumLow,
                        ),
                        initialOffsetY = { fullHeight -> (fullHeight * 0.8f).toInt() },
                    ) + fadeIn(
                        animationSpec = tween(durationMillis = 260, easing = LinearOutSlowInEasing),
                    ) + scaleIn(
                        initialScale = 0.90f,
                        animationSpec = spring(
                            dampingRatio = 0.82f,
                            stiffness = Spring.StiffnessMediumLow,
                        ),
                    )).togetherWith(
                        slideOutVertically(
                            animationSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing),
                            targetOffsetY = { fullHeight -> -(fullHeight * 0.8f).toInt() },
                        ) + fadeOut(
                            animationSpec = tween(durationMillis = 180),
                        ) + scaleOut(
                            targetScale = 0.95f,
                            animationSpec = tween(durationMillis = 180),
                        ),
                    )
                },
                label = "AppleMusicMiniLyrics",
                contentAlignment = Alignment.Center,
            ) { state ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = contentColor.copy(alpha = 0.80f),
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(12.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.expand_less),
                            contentDescription = null,
                            tint = contentColor.copy(alpha = if (state.isLive) 0.85f else 0.75f),
                            modifier = Modifier.size(15.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                    Text(
                        text = state.text,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (state.isLive) FontWeight.Bold else FontWeight.SemiBold,
                            letterSpacing = if (state.isLive) 0.2.sp else 0.5.sp,
                            fontSize = 13.sp,
                        ),
                        color = contentColor.copy(alpha = if (state.isLive) 0.98f else 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = if (state.isLive && state.text != "♪") {
                            Modifier.basicMarquee(
                                iterations = Int.MAX_VALUE,
                                initialDelayMillis = 1400,
                                velocity = 35.dp,
                            )
                        } else {
                            Modifier
                        },
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

/**
 * Top inverted "Lyrics Hill" Drop Notch:
 * Compact, bounded drop notch at the top of the lyrics view.
 * Clicking it dismisses lyrics.
 */
@Composable
fun TopLyricsHill(
    modifier: Modifier = Modifier,
    contentColor: Color = Color.White,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .width(140.dp)
            .height(38.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val startX = 0f
            val endX = w
            val midX = w / 2f
            val crestWidth = w * 0.45f
            val leftCrest = midX - crestWidth / 2f
            val rightCrest = midX + crestWidth / 2f
            val dropY = size.height - 2.dp.toPx()

            val curvePath = Path().apply {
                moveTo(startX, 0f)
                cubicTo(
                    startX + (leftCrest - startX) * 0.45f, 0f,
                    leftCrest - (leftCrest - startX) * 0.40f, dropY,
                    leftCrest, dropY
                )
                lineTo(rightCrest, dropY)
                cubicTo(
                    rightCrest + (endX - rightCrest) * 0.40f, dropY,
                    endX - (endX - rightCrest) * 0.45f, 0f,
                    endX, 0f
                )
            }

            val fillPath = Path().apply {
                addPath(curvePath)
                lineTo(endX, 0f)
                lineTo(startX, 0f)
                close()
            }

            // Translucent glass fill
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    listOf(
                        contentColor.copy(alpha = 0.06f),
                        contentColor.copy(alpha = 0.22f),
                    ),
                    startY = 0f,
                    endY = dropY,
                )
            )

            // Bottom highlight stroke along the curve
            drawPath(
                path = curvePath,
                brush = Brush.verticalGradient(
                    listOf(
                        contentColor.copy(alpha = 0.12f),
                        contentColor.copy(alpha = 0.48f),
                    ),
                    startY = 0f,
                    endY = dropY,
                ),
                style = Stroke(
                    width = 1.6.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 4.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.expand_more),
                contentDescription = null,
                tint = contentColor.copy(alpha = 0.85f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.lyrics),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    fontSize = 13.sp,
                ),
                color = contentColor.copy(alpha = 0.95f),
            )
        }
    }
}


/**
 * Expanded Lyrics Card matching Image 2:
 * Features a rounded squircle card with glass border, album artwork backdrop with dark scrim,
 * top inverted "Lyrics Hill" drop notch, and centered synchronized lyrics.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExpandedLyricsCard(
    mediaMetadata: MediaMetadata?,
    showLyrics: Boolean,
    positionProvider: () -> Long,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(32.dp)
    val playerConnection = LocalPlayerConnection.current ?: return
    val currentLyrics by playerConnection.currentLyrics.collectAsStateWithLifecycle(initialValue = null)
    val lyrics = remember(currentLyrics) { currentLyrics?.lyrics?.trim() }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        // 1. Album Artwork Backdrop with fluid blur
        if (!mediaMetadata?.thumbnailUrl.isNullOrEmpty()) {
            AsyncImage(
                model = mediaMetadata.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.25f)
                    .blur(40.dp),
            )
        }

        // 2. Dark Glass Scrim Overlay (ensures text readability)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color.Black.copy(alpha = 0.65f),
                        )
                    )
                )
        )

        // 3. Centered Lyrics View
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, bottom = 16.dp, start = 12.dp, end = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            when {
                lyrics == null -> {
                    GlassmorphicLinearLoader(message = stringResource(R.string.lyrics_loading))
                }

                lyrics == LyricsEntity.LYRICS_NOT_FOUND || lyrics.isBlank() -> {
                    Text(
                        text = stringResource(R.string.lyrics_not_found),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color.White.copy(alpha = 0.75f),
                        textAlign = TextAlign.Center,
                    )
                }

                else -> {
                    ProvideTextStyle(
                        value = MaterialTheme.typography.bodyLarge.copy(
                            textAlign = TextAlign.Center,
                            color = Color.White,
                        ),
                    ) {
                        Lyrics(
                            sliderPositionProvider = positionProvider,
                            modifier = Modifier.fillMaxSize(),
                            showLyrics = showLyrics,
                        )
                    }
                }
            }
        }

        // 4. Top Inverted "Lyrics Hill" Drop Notch (Click to dismiss)
        TopLyricsHill(
            modifier = Modifier.align(Alignment.TopCenter),
            contentColor = Color.White,
            onClick = onDismiss,
        )
    }
}
