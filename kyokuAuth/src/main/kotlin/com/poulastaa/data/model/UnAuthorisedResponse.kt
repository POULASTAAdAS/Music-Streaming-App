package com.poulastaa.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UnAuthorisedResponse(
    val message: String
)