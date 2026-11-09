package com.fiend.music.widget.wave

import androidx.annotation.LayoutRes
import com.fiend.music.R

enum class WaveWidgetKind(
    @LayoutRes val layoutRes: Int,
    val targetCellWidth: Int,
    val targetCellHeight: Int,
) {
    STRIP(R.layout.widget_wave_strip, 4, 1),
    GLASS(R.layout.widget_wave_glass, 2, 2),
    CIRCULAR(R.layout.widget_wave_circular, 2, 2),
    LIBRARY(R.layout.widget_wave_library, 4, 2),
}
