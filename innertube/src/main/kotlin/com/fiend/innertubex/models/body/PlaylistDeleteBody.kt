package com.fiend.innertubex.models.body

import com.fiend.innertubex.models.Context
import kotlinx.serialization.Serializable

@Serializable
internal data class PlaylistDeleteBody(
    val context: Context,
    val playlistId: String,
)
