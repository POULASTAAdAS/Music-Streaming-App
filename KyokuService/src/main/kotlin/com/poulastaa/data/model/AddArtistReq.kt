package com.poulastaa.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AddArtistReq(
    val list: List<Long>
)
