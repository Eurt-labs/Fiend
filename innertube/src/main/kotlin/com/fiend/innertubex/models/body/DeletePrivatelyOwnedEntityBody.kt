package com.fiend.innertubex.models.body

import com.fiend.innertubex.models.Context
import kotlinx.serialization.Serializable

@Serializable
internal data class DeletePrivatelyOwnedEntityBody(
    val context: Context,
    val entityId: String,
)
