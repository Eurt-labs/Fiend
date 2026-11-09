package com.fiend.music.ui.theme

import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FiendThemeTest {

    @Test
    fun fiendDarkScheme_usesBrandColors() {
        val scheme = Md3SchemeBuilder.buildFiendDarkScheme(amoled = false)

        assertThat(scheme.primary).isEqualTo(Color(0xFFC9FB00))
        assertThat(scheme.onPrimary).isEqualTo(Color(0xFF012226))
        assertThat(scheme.surface).isEqualTo(Color(0xFF012226))
        assertThat(scheme.background).isEqualTo(Color(0xFF01181B))
    }

    @Test
    fun fiendDarkScheme_amoled_pureBlackSurfaces() {
        val scheme = Md3SchemeBuilder.buildFiendDarkScheme(amoled = true)

        assertThat(scheme.primary).isEqualTo(Color(0xFFC9FB00))
        assertThat(scheme.onPrimary).isEqualTo(Color(0xFF012226))
        assertThat(scheme.background).isEqualTo(Color.Black)
        assertThat(scheme.surface).isEqualTo(Color.Black)
        assertThat(scheme.surfaceContainerLowest).isEqualTo(Color.Black)
        assertThat(scheme.surfaceContainerLow).isEqualTo(Color.Black)
    }

    @Test
    fun fiendLightScheme_usesBrandHighlights() {
        val scheme = Md3SchemeBuilder.buildFiendLightScheme()

        assertThat(scheme.onPrimaryContainer).isEqualTo(Color(0xFF012226))
        assertThat(scheme.inversePrimary).isEqualTo(Color(0xFFC9FB00))
        assertThat(scheme.inverseSurface).isEqualTo(Color(0xFF012226))
    }
}
