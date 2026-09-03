/**
 * Fiend Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.fiend.music.ui.liquidglass

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Glassmorphic Linear Loader — Replaces generic circular spinners with a premium
 * horizontal linear glass loader that automatically adapts to the active theme color palette.
 */
@Composable
fun GlassmorphicLinearLoader(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    secondaryColor: Color = MaterialTheme.colorScheme.tertiary,
    barWidth: Dp = 160.dp,
    barHeight: Dp = 6.dp,
    message: String? = null,
    showCard: Boolean = true,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glassLinearLoader")

    val beamOffset by infiniteTransition.animateFloat(
        initialValue = -0.35f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "beamOffset",
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.70f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseAlpha",
    )

    val content: @Composable () -> Unit = {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // The glowing linear glass progress bar
            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(barHeight)
                    .clip(CircleShape)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height
                    val radius = CornerRadius(h / 2f, h / 2f)

                    // 1. Dark translucent track groove
                    drawRoundRect(
                        color = Color.Black.copy(alpha = 0.42f),
                        size = size,
                        cornerRadius = radius,
                    )

                    // 2. Track inner shadow / specular outline
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.22f),
                                Color.White.copy(alpha = 0.04f),
                            )
                        ),
                        size = size,
                        cornerRadius = radius,
                        style = Stroke(width = 1.dp.toPx()),
                    )

                    // 3. Sweeping theme-colored glowing beam
                    val beamWidth = w * 0.48f
                    val startX = w * beamOffset - beamWidth / 2f
                    val endX = startX + beamWidth

                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                color.copy(alpha = 0f),
                                color.copy(alpha = 0.65f * pulseAlpha),
                                Color.White.copy(alpha = 0.95f * pulseAlpha),
                                secondaryColor.copy(alpha = 0.70f * pulseAlpha),
                                color.copy(alpha = 0f),
                            ),
                            startX = startX,
                            endX = endX,
                        ),
                        size = size,
                        cornerRadius = radius,
                    )
                }
            }

            if (!message.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.3.sp,
                    ),
                    color = Color.White.copy(alpha = 0.75f * pulseAlpha),
                )
            }
        }
    }

    if (showCard) {
        val shape = RoundedCornerShape(22.dp)
        Box(
            modifier = modifier
                .clip(shape)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            GlassPalette.surfaceFrosted.copy(alpha = 0.85f),
                            GlassPalette.surface.copy(alpha = 0.90f),
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.12f),
                            Color.White.copy(alpha = 0.03f),
                        )
                    ),
                    shape = shape,
                )
                .padding(horizontal = 22.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    } else {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            content()
        }
    }
}

/**
 * Determinate Glassmorphic Linear Progress — Specifically designed for interval indicators
 * (e.g. musical breaks in lyrics) that fills fluidly from left to right according to [progress].
 */
@Composable
fun GlassmorphicLinearProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    secondaryColor: Color = MaterialTheme.colorScheme.tertiary,
    barWidth: Dp = 150.dp,
    barHeight: Dp = 7.dp,
) {
    val clampedProgress = progress.coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .width(barWidth)
            .height(barHeight)
            .clip(CircleShape)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val radius = CornerRadius(h / 2f, h / 2f)

            // 1. Dark translucent track groove
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.45f),
                size = size,
                cornerRadius = radius,
            )

            // 2. Track glass rim
            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.28f),
                        Color.White.copy(alpha = 0.05f),
                    )
                ),
                size = size,
                cornerRadius = radius,
                style = Stroke(width = 1.dp.toPx()),
            )

            // 3. Filled progress bar
            val fillWidth = w * clampedProgress
            if (fillWidth > 0f) {
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            color.copy(alpha = 0.60f),
                            color.copy(alpha = 0.90f),
                            Color.White.copy(alpha = 0.95f),
                        ),
                        startX = 0f,
                        endX = fillWidth,
                    ),
                    size = Size(fillWidth, h),
                    cornerRadius = radius,
                )

                // 4. Glowing pip at the leading edge
                drawCircle(
                    color = Color.White,
                    radius = (h / 2f) * 0.9f,
                    center = Offset(fillWidth.coerceAtMost(w - h / 2f), h / 2f),
                )
            }
        }
    }
}
