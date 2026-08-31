package com.fiend.innertubex.models.body

import com.fiend.innertubex.models.Context
import kotlinx.serialization.Serializable

@Serializable
internal data class SubscribeBody(
    val context: Context,
    val channelIds: List<String>,
    val params: String? = null,
)
