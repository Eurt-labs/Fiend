/**
 * Fiend Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.fiend.music.ui.liquidglass

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiend.music.utils.makeTimeString
import kotlin.math.sin

/**
 * Audio Soundwave / Waveform visualizer scrubber as seen in modern Liquid Glass players.
 * Features animated audio pulses, interactive drag-to-seek, and gradient illumination.
 */
@Composable
fun SoundwaveScrubber(
    position: Long,
    duration: Long,
    isPlaying: Boolean,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = Color(0xFFA855F7), // Vibrant purple
    activeColorEnd: Color = Color(0xFFEC4899), // Neon pink
    inactiveColor: Color = Color.White.copy(alpha = 0.22f),
    textColor: Color = Color.White.copy(alpha = 0.75f),
    barCount: Int = 38,
) {
    val progress = if (duration > 0) (position.toFloat() / duration).coerceIn(0f, 1f) else 0f
    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(0f) }

    val effectiveProgress = if (isDragging) dragProgress else progress

    val infiniteTransition = rememberInfiniteTransition(label = "soundwavePulse")
    val pulsePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "pulsePhase",
    )

    // Pre-generate pseudo-random base heights for the waveform
    val baseHeights = remember(barCount) {
        FloatArray(barCount) { i ->
            val normalized = i.toFloat() / barCount
            val curve = sin(normalized * Math.PI).toFloat() // Arch shape in center
            val randomFactor = (0.4f + 0.6f * ((i * 7 + 13) % 17 / 17f))
            (curve * randomFactor).coerceIn(0.20f, 1.0f)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Soundwave Canvas
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .pointerInput(duration) {
                    detectTapGestures { offset ->
                        val tappedProgress = (offset.x / size.width).coerceIn(0f, 1f)
                        val targetPos = (tappedProgress * duration).toLong()
                        onSeek(targetPos)
                    }
                }
                .pointerInput(duration) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            dragProgress = (offset.x / size.width).coerceIn(0f, 1f)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            dragProgress = (change.position.x / size.width).coerceIn(0f, 1f)
                        },
                        onDragEnd = {
                            isDragging = false
                            val targetPos = (dragProgress * duration).toLong()
                            onSeek(targetPos)
                        },
                        onDragCancel = {
                            isDragging = false
                        }
                    )
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val barWidth = (canvasWidth / barCount) * 0.62f
            val spacing = (canvasWidth - (barWidth * barCount)) / (barCount - 1).coerceAtLeast(1)

            val activeBrush = Brush.horizontalGradient(
                colors = listOf(activeColor, activeColorEnd)
            )

            for (i in 0 until barCount) {
                val x = i * (barWidth + spacing)
                val barProgress = (i.toFloat() / barCount)
                val isActive = barProgress <= effectiveProgress

                // Dynamic pulse height when playing
                val pulseModifier = if (isPlaying) {
                    0.85f + 0.25f * sin(pulsePhase + (i * 0.45f))
                } else {
                    1.0f
                }

                val currentBarHeight = (baseHeights[i] * canvasHeight * pulseModifier).coerceIn(6.dp.toPx(), canvasHeight)
                val y = (canvasHeight - currentBarHeight) / 2f

                drawRoundRect(
                    brush = if (isActive) activeBrush else Brush.linearGradient(listOf(inactiveColor, inactiveColor)),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, currentBarHeight),
                    cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f),
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Time labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val displayedPosition = if (isDragging) (dragProgress * duration).toLong() else position
            Text(
                text = makeTimeString(displayedPosition),
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )

            Text(
                text = if (duration > 0) makeTimeString(duration) else "0:00",
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
