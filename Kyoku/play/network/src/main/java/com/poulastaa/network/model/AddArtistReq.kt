package com.poulastaa.network.model

import kotlinx.serialization.Serializable

@Serializable
data class AddArtistReq(
    val list: List<Long>,
)
