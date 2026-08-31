/**
 * Fiend Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.fiend.music.ui.liquidglass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Color tokens for the Liquid Glass aesthetic.
 */
object GlassPalette {
    val surface = Color(0xFF141416)
    val surfaceVariant = Color(0xFF222226)
    val surfaceFrosted = Color(0x9918181C)
    val surfaceFrostedLight = Color(0x662A2A30)
    val border = Color(0x33FFFFFF)
    val borderHighlighted = Color(0x66FFFFFF)
    val specularTop = Color(0x80FFFFFF)
    val specularBottom = Color(0x08FFFFFF)
}

/**
 * Liquid Glass Card — Replicates the iOS / liquid glass refraction aesthetic.
 * Features a frosted dark fill and a specular top-edge meniscus ring border.
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    rimAlpha: Float = 0.45f,
    innerPadding: Dp = 14.dp,
    backgroundColor: Color = GlassPalette.surfaceFrosted,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(cornerRadius)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        backgroundColor.copy(alpha = (backgroundColor.alpha * 1.15f).coerceAtMost(1f)),
                        backgroundColor,
                        backgroundColor.copy(alpha = (backgroundColor.alpha * 0.9f).coerceAtLeast(0f)),
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = rimAlpha),
                        Color.White.copy(alpha = rimAlpha * 0.35f),
                        Color.White.copy(alpha = 0.04f),
                    )
                ),
                shape = shape,
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                }
            )
            .padding(innerPadding),
        content = content,
    )
}

/**
 * Compact Liquid Glass Pill — for inline chips, tags, floating buttons, and toggles.
 */
@Composable
fun LiquidGlassPill(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    accentColor: Color = Color.White,
    cornerRadius: Dp = 20.dp,
    innerPadding: Dp = 8.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(cornerRadius)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    if (isSelected) {
                        listOf(
                            accentColor.copy(alpha = 0.28f),
                            accentColor.copy(alpha = 0.16f),
                        )
                    } else {
                        listOf(
                            GlassPalette.surfaceFrostedLight,
                            GlassPalette.surfaceFrosted,
                        )
                    }
                )
            )
            .border(
                width = if (isSelected) 1.2.dp else 0.8.dp,
                brush = Brush.verticalGradient(
                    if (isSelected) {
                        listOf(
                            accentColor.copy(alpha = 0.85f),
                            accentColor.copy(alpha = 0.35f),
                            accentColor.copy(alpha = 0.10f),
                        )
                    } else {
                        listOf(
                            Color.White.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.08f),
                        )
                    }
                ),
                shape = shape,
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                }
            )
            .padding(innerPadding),
        content = content,
    )
}

/**
 * Liquid Glass Capsule / Pill modifier for custom layouts.
 */
fun Modifier.liquidGlassCapsule(
    shape: Shape = CircleShape,
    backgroundColor: Color = GlassPalette.surfaceFrosted,
    rimAlpha: Float = 0.40f,
): Modifier = this
    .clip(shape)
    .background(
        brush = Brush.verticalGradient(
            listOf(
                backgroundColor.copy(alpha = (backgroundColor.alpha * 1.2f).coerceAtMost(1f)),
                backgroundColor,
            )
        )
    )
    .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = rimAlpha),
                Color.White.copy(alpha = rimAlpha * 0.3f),
                Color.White.copy(alpha = 0.03f),
            )
        ),
        shape = shape,
    )
