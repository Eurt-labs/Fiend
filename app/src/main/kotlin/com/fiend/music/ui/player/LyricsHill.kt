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
import androidx.compose.material3.ContainedLoadingIndicator
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

/**
 * Bottom "Lyrics Hill" Composable matching Image 1:
 * Rises up smoothly from the bottom between Shuffle and Repeat controls,
 * with an organic S-curve dome, subtle translucent glass fill, highlight stroke,
 * and bold "Lyrics" label in the center.
 */
@Composable
fun BottomLyricsHill(
    modifier: Modifier = Modifier,
    contentColor: Color = Color.White,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .width(140.dp)
            .height(42.dp)
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
            val crestWidth = w * 0.45f
            val leftCrest = midX - crestWidth / 2f
            val rightCrest = midX + crestWidth / 2f
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
                        contentColor.copy(alpha = 0.20f),
                        contentColor.copy(alpha = 0.06f),
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
                        contentColor.copy(alpha = 0.48f),
                        contentColor.copy(alpha = 0.12f),
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

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 4.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.expand_less),
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
                    ContainedLoadingIndicator()
                }

                lyrics == LyricsEntity.LYRICS_NOT_FOUND -> {
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
