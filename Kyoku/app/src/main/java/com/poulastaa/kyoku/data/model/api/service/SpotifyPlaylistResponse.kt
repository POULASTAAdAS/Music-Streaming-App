package com.poulastaa.kyoku.data.model.api.service

import kotlinx.serialization.Serializable

@Serializable
data class SpotifyPlaylistResponse(
    val status: HandleSpotifyPlaylistStatus = HandleSpotifyPlaylistStatus.FAILURE,
    val listOfResponseSong: List<ResponseSong> = emptyList()
)
