package com.poulastaa.setup.network.model.res

import kotlinx.serialization.Serializable

@Serializable
data class ArtistDto(
    val id: Long,
    val name: String,
    val coverImage: String?,
)
