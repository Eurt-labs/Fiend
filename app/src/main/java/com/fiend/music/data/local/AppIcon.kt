package com.fiend.music.data.local

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.fiend.music.R

enum class AppIcon(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int,
    val componentName: String,
    @DrawableRes val logoRes: Int,
    @ColorRes val backgroundRes: Int,
) {
    DEFAULT(
        id = "default",
        titleRes = R.string.app_icon_default,
        subtitleRes = R.string.app_icon_default_sub,
        componentName = "com.fiend.music.MainActivity",
        logoRes = R.drawable.fiend_logo,
        backgroundRes = R.color.ic_launcher_background,
    ),
    DESKTOP(
        id = "desktop",
        titleRes = R.string.app_icon_desktop,
        subtitleRes = R.string.app_icon_desktop_sub,
        componentName = "com.fiend.music.MainActivityDesktop",
        logoRes = R.drawable.fiend_logo_desktop,
        backgroundRes = R.color.ic_launcher_background_desktop,
    ),
    WHITE(
        id = "white",
        titleRes = R.string.app_icon_white,
        subtitleRes = R.string.app_icon_white_sub,
        componentName = "com.fiend.music.MainActivityWhite",
        logoRes = R.drawable.fiend_logo_white,
        backgroundRes = R.color.ic_launcher_background_white,
    ),
    BLACK(
        id = "black",
        titleRes = R.string.app_icon_black,
        subtitleRes = R.string.app_icon_black_sub,
        componentName = "com.fiend.music.MainActivityBlack",
        logoRes = R.drawable.fiend_logo_black,
        backgroundRes = R.color.ic_launcher_background_black,
    );

    companion object {
        fun fromId(id: String?): AppIcon =
            entries.firstOrNull { it.id == id } ?: DEFAULT
    }
}
