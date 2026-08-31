package com.fiend.innertubex.models.body

import com.fiend.innertubex.models.Context
import kotlinx.serialization.Serializable

@Serializable
internal data class GetSearchSuggestionsBody(
    val context: Context,
    val input: String,
)
