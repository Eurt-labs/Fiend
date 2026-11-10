package com.fiend.music.ui.splash

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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fiend.music.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FiendBrandSplashScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(true) }
    var subtitleVisible by remember { mutableStateOf(false) }

    // Entry scale & alpha transition for the logo
    val entranceScale = remember { Animatable(0.72f) }
    val entranceAlpha = remember { Animatable(0f) }
    val titleSlide = remember { Animatable(18f) }
    val titleAlpha = remember { Animatable(0f) }

    // Continuous ambient glow & pulse
    val infiniteTransition = rememberInfiniteTransition(label = "ambientGlow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoPulse"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    // Waveform heights for animated music equalizer bars
    val bar1 by infiniteTransition.animateFloat(
        initialValue = 4f, targetValue = 16f,
        animationSpec = infiniteRepeatable(tween(450, easing = LinearEasing), RepeatMode.Reverse),
        label = "b1"
    )
    val bar2 by infiniteTransition.animateFloat(
        initialValue = 16f, targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(550, easing = LinearEasing), RepeatMode.Reverse),
        label = "b2"
    )
    val bar3 by infiniteTransition.animateFloat(
        initialValue = 8f, targetValue = 20f,
        animationSpec = infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Reverse),
        label = "b3"
    )
    val bar4 by infiniteTransition.animateFloat(
        initialValue = 18f, targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(600, easing = LinearEasing), RepeatMode.Reverse),
        label = "b4"
    )

    LaunchedEffect(Unit) {
        coroutineScope {
            launch {
                entranceScale.animateTo(1f, animationSpec = tween(650, easing = FastOutSlowInEasing))
            }
            launch {
                entranceAlpha.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
            }
        }
        delay(100)
        coroutineScope {
            launch {
                titleSlide.animateTo(0f, animationSpec = tween(500, easing = FastOutSlowInEasing))
            }
            launch {
                titleAlpha.animateTo(1f, animationSpec = tween(500, easing = FastOutSlowInEasing))
            }
        }
        delay(200)
        subtitleVisible = true

        // Hold and graceful exit
        delay(1300)
        visible = false
        delay(400)
        onDismiss()
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(250)),
        exit = fadeOut(tween(400)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF000000))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    visible = false
                    onDismiss()
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Ambient Radial Glow & Breathing App Icon
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .scale(entranceScale.value * pulseScale)
                        .alpha(entranceAlpha.value),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer diffuse aura
                    Box(
                        modifier = Modifier
                            .size(126.dp)
                            .clip(CircleShape)
                            .alpha(glowAlpha)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF00D2FF).copy(alpha = 0.45f),
                                        Color(0xFF6366F1).copy(alpha = 0.25f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Inner crisp logo
                    Image(
                        painter = painterResource(id = R.drawable.fiend_logo),
                        contentDescription = "Fiend Logo",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Brand Title with smooth slide-up
                Text(
                    text = "Fiend",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color(0xFFF8FAFC),
                    letterSpacing = 1.6.sp,
                    modifier = Modifier
                        .offset { IntOffset(0, titleSlide.value.toInt()) }
                        .alpha(titleAlpha.value)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Audio equalizer animation wave
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(22.dp)
                        .alpha(titleAlpha.value * 0.85f)
                ) {
                    val barColor = Color(0xFF38BDF8)
                    Box(Modifier.width(3.dp).height(bar1.dp).clip(RoundedCornerShape(2.dp)).background(barColor))
                    Box(Modifier.width(3.dp).height(bar2.dp).clip(RoundedCornerShape(2.dp)).background(barColor))
                    Box(Modifier.width(3.dp).height(bar3.dp).clip(RoundedCornerShape(2.dp)).background(barColor))
                    Box(Modifier.width(3.dp).height(bar4.dp).clip(RoundedCornerShape(2.dp)).background(barColor))
                }

                Spacer(modifier = Modifier.height(10.dp))

                AnimatedVisibility(
                    visible = subtitleVisible,
                    enter = fadeIn(tween(400))
                ) {
                    Text(
                        text = stringResource(id = R.string.splash_made_by),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }
    }
}
