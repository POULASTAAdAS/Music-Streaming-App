package com.poulastaa.data.model.setup.spotify

import com.poulastaa.data.model.common.ResponseSong
import kotlinx.serialization.Serializable

@Serializable
data class SpotifyPlaylistResponse(
    val status: HandleSpotifyPlaylistStatus = HandleSpotifyPlaylistStatus.FAILURE,
    val name: String = "",
    val listOfResponseSong: List<ResponseSong> = emptyList()
)