/**
 * Fiend Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.fiend.music.ui.theme

import android.app.Activity
import android.graphics.Bitmap
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.palette.graphics.Palette
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme
import com.materialkolor.score.Score

val DefaultThemeColor = Color(0xFFED5564)

@Composable
fun FiendTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    pureBlack: Boolean = false,
    themeColor: Color = DefaultThemeColor,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    // Determine if system dynamic colors should be used (Android S+ and default theme color)
    val useSystemDynamicColor = (themeColor == DefaultThemeColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)

    // Select the appropriate color scheme generation method
    val baseColorScheme = if (useSystemDynamicColor) {
        // Use standard Material 3 dynamic color functions for system wallpaper colors
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        // Use materialKolor with Vibrant style to dynamically reflect the song's vibrant mood
        rememberDynamicColorScheme(
            seedColor = themeColor,
            isDark = darkTheme,
            specVersion = ColorSpec.SpecVersion.SPEC_2025,
            style = PaletteStyle.Vibrant,
        )
    }

    // Apply optimized Apple Music dark theme / pureBlack modifications
    val colorScheme = remember(baseColorScheme, pureBlack, darkTheme) {
        if (darkTheme) {
            if (pureBlack) {
                baseColorScheme.copy(
                    surface = Color.Black,
                    background = Color.Black,
                    surfaceContainer = Color(0xFF0C0C10),
                    surfaceContainerLow = Color(0xFF060608),
                    surfaceContainerHigh = Color(0xFF14141A),
                    surfaceContainerHighest = Color(0xFF1C1C24),
                    onSurface = Color(0xFFF6F6FA),
                    onSurfaceVariant = Color(0xFFA1A1B2),
                )
            } else {
                baseColorScheme.copy(
                    surface = Color(0xFF0E0E14),
                    background = Color(0xFF09090D),
                    surfaceContainer = Color(0xFF15151D),
                    surfaceContainerLow = Color(0xFF101017),
                    surfaceContainerHigh = Color(0xFF1D1D27),
                    surfaceContainerHighest = Color(0xFF252532),
                    onSurface = Color(0xFFF6F6FA),
                    onSurfaceVariant = Color(0xFFA1A1B2),
                )
            }
        } else {
            baseColorScheme
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    // Use standard MaterialTheme instead of MaterialExpressiveTheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}

fun Bitmap.extractThemeColor(): Color {
    val palette = Palette.from(this)
        .maximumColorCount(24)
        .generate()

    val bestSwatch = palette.vibrantSwatch
        ?: palette.lightVibrantSwatch
        ?: palette.darkVibrantSwatch
        ?: palette.dominantSwatch
        ?: palette.mutedSwatch

    return if (bestSwatch != null) {
        Color(bestSwatch.rgb)
    } else {
        Color(palette.rankedColors(1, DefaultThemeColor.toArgb()).first())
    }
}

internal fun Palette.rankedColors(
    desiredColorCount: Int,
    fallbackColor: Int,
): List<Int> = Score.score(
    swatches.associate { it.rgb to it.population },
    desiredColorCount,
    fallbackColor,
    true,
)

fun ColorScheme.pureBlack(apply: Boolean) =
    if (apply) copy(
        surface = Color.Black,
        background = Color.Black,
        surfaceContainer = Color(0xFF0C0C10),
        surfaceContainerLow = Color(0xFF060608),
        surfaceContainerHigh = Color(0xFF14141A),
        surfaceContainerHighest = Color(0xFF1C1C24),
        onSurface = Color(0xFFF6F6FA),
        onSurfaceVariant = Color(0xFFA1A1B2),
    ) else this

val ColorSaver = object : Saver<Color, Int> {
    override fun restore(value: Int): Color = Color(value)
    override fun SaverScope.save(value: Color): Int = value.toArgb()
}
