/**
 * Fiend Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.fiend.music.ui.liquidglass

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

/**
 * Apple Music style dynamic blurred backdrop.
 * Combines heavily blurred album artwork with fluid animated color orbs and a dark glass scrim.
 */
@Composable
fun AppleMusicBackground(
    artworkUrl: String?,
    modifier: Modifier = Modifier,
    dominantColor: Color = Color(0xFF1E1E24),
    dimAlpha: Float = 0.50f,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val infiniteTransition = rememberInfiniteTransition(label = "meshFluid")
    val animOffset1 by infiniteTransition.animateFloat(
        initialValue = -40f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "animOffset1",
    )
    val animOffset2 by infiniteTransition.animateFloat(
        initialValue = 30f,
        targetValue = -30f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "animOffset2",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C0C0E)),
    ) {
        // Base blurred artwork layer
        if (!artworkUrl.isNullOrEmpty()) {
            AsyncImage(
                model = artworkUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.4f)
                    .blur(70.dp)
                    .alpha(0.65f),
            )
        }

        // Fluid colored ambient light orbs
        Box(
            modifier = Modifier
                .size(screenWidth * 1.1f)
                .offset(x = animOffset1.dp, y = (screenHeight * 0.15f) + animOffset2.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            dominantColor.copy(alpha = 0.55f),
                            dominantColor.copy(alpha = 0.15f),
                            Color.Transparent,
                        )
                    )
                )
                .blur(80.dp),
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(screenWidth * 0.9f)
                .offset(x = animOffset2.dp, y = animOffset1.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            dominantColor.copy(alpha = 0.40f),
                            Color.Transparent,
                        )
                    )
                )
                .blur(70.dp),
        )

        // Dark glass scrim for contrast & readability
        val bottomAlpha = (dimAlpha * 1.3f).coerceAtMost(0.92f)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = dimAlpha * 0.7f),
                            Color.Black.copy(alpha = dimAlpha),
                            Color.Black.copy(alpha = bottomAlpha),
                        )
                    )
                ),
        )
    }
}
