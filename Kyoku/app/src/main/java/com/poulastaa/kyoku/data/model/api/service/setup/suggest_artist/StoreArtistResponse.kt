package com.poulastaa.kyoku.data.model.api.service.setup.suggest_artist

import kotlinx.serialization.Serializable

@Serializable
data class StoreArtistResponse(
    val status: ArtistResponseStatus
)
