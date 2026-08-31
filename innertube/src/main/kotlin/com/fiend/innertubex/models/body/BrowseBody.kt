package com.fiend.innertubex.models.body

import com.fiend.innertubex.models.Context
import kotlinx.serialization.Serializable

@Serializable
internal data class BrowseBody(
    val context: Context,
    val browseId: String? = null,
    val params: String? = null,
    val continuation: String? = null,
)
