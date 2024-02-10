package com.poulastaa.kyoku.domain.repository

import com.poulastaa.kyoku.data.model.api.service.SpotifyPlaylistResponse

interface ServiceRepository {
    suspend fun getSpotifyPlaylist(tokenOrCookie:String,playlistId: String): SpotifyPlaylistResponse?
}