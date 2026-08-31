package com.fiend.innertubex.models.body

import com.fiend.innertubex.models.Context
import kotlinx.serialization.Serializable

@Serializable
internal data class GetQueueBody(
    val context: Context,
    val videoIds: List<String>?,
    val playlistId: String?,
)
