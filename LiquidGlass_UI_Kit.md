# Liquid Glass UI Kit (Jetpack Compose)

Here is the self-contained boilerplate for the **Liquid Glass UI**, including the frosted refraction cards, the pills, and the highly-requested **Animated Floating Cloud Capsule** (with the squish physics). 

You can copy-paste this entire file into your next AI prompt and say: *"Build a new app layout using the provided LiquidGlass components."*

## 1. The Core Components (Cards & Pills)

```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Define your color palette here (or swap for MaterialTheme.colorScheme)
object GlassPalette {
    val surface = Color(0xFF1E1E1E)
    val surfaceVariant = Color(0xFF2C2C2C)
    val border = Color(0xFF3D3D3D)
}

/**
 * Liquid Glass Card — Replicates the iOS / WebGL liquid glass refraction shader aesthetic.
 * Features a frosted dark fill and a specular top-edge meniscus ring.
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    rimAlpha: Float = 0.55f,
    innerPadding: Dp = 12.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            // Layer 1: Opaque frosted dark fill
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        GlassPalette.surfaceVariant, // slightly lighter top
                        GlassPalette.surface,        // core frosted dark
                        GlassPalette.surface         // bottom edge fade
                    )
                )
            )
            // Layer 2: Specular top-meniscus rim border
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = rimAlpha),         // bright meniscus top
                        Color.White.copy(alpha = rimAlpha * 0.35f), // mid-fade
                        Color.White.copy(alpha = 0.03f)             // near-invisible bottom
                    )
                ),
                shape = shape
            )
            .padding(innerPadding),
        content = content
    )
}

/**
 * Compact variant — for inline pills, chips, and small selectable items.
 */
@Composable
fun LiquidGlassPill(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    accentColor: Color = Color.White,
    cornerRadius: Dp = 18.dp,
    innerPadding: Dp = 0.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    if (isSelected) {
                        listOf(GlassPalette.border, GlassPalette.surfaceVariant)
                    } else {
                        listOf(GlassPalette.surfaceVariant, GlassPalette.surface)
                    }
                )
            )
            .border(
                width = if (isSelected) 1.2.dp else 0.8.dp,
                brush = Brush.verticalGradient(
                    if (isSelected) {
                        listOf(
                            Color.White.copy(alpha = 0.85f),
                            Color.White.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.08f)
                        )
                    } else {
                        listOf(
                            Color.White.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.08f)
                        )
                    }
                ),
                shape = shape
            )
            .then(if (innerPadding > 0.dp) Modifier.padding(innerPadding) else Modifier),
        content = content
    )
}
```

## 2. The Animated "Floating Cloud Capsule" (Toggle Switch)

This is the exact code that powers the toggle switch inside the Settings screen (the one that squishes and stretches as it travels between options).

```kotlin
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedCloudToggle(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(GlassPalette.surfaceVariant, GlassPalette.surface)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.30f), Color.White.copy(alpha = 0.05f))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(4.dp)
    ) {
        val density = androidx.compose.ui.platform.LocalDensity.current
        val itemWidth = maxWidth / options.size
        
        // Target offset based on selected index
        val targetOffset = itemWidth * selectedIndex
        
        // Spring animation for position
        val bubbleOffset by animateDpAsState(
            targetValue = targetOffset,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "bubbleOffset"
        )

        // Calculate squish effect based on travel distance
        val distanceToTarget = with(density) { Math.abs(bubbleOffset.toPx() - targetOffset.toPx()) }
        val isTraveling = distanceToTarget > 2f
        
        val targetScaleX = if (isTraveling) 1.15f else 1.0f
        val targetScaleY = if (isTraveling) 0.85f else 1.0f
        
        val liquidStretchX by animateFloatAsState(
            targetValue = targetScaleX,
            animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
            label = "stretchX"
        )
        val liquidShrinkY by animateFloatAsState(
            targetValue = targetScaleY,
            animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
            label = "shrinkY"
        )

        // The Floating Glass Pill (Travels behind text)
        LiquidGlassPill(
            isSelected = true,
            cornerRadius = 20.dp,
            modifier = Modifier
                .offset(x = bubbleOffset)
                .width(itemWidth)
                .height(38.dp)
                .scale(scaleX = liquidStretchX, scaleY = liquidShrinkY)
        ) { /* Empty, just background */ }

        // The Text Options Overlay
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            options.forEachIndexed { index, text ->
                val isSelected = selectedIndex == index
                Box(
                    modifier = Modifier
                        .width(itemWidth)
                        .height(38.dp)
                        .clickable { onOptionSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        color = if (isSelected) Color.White else Color.Gray
                    )
                }
            }
        }
    }
}
```
