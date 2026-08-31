package com.fiend.innertubex.models.body

import com.fiend.innertubex.models.Context
import kotlinx.serialization.Serializable

@Serializable
internal data class GetTranscriptBody(
    val context: Context,
    val params: String,
)
