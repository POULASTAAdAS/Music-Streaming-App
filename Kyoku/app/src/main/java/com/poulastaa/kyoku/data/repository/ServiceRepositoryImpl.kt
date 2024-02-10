package com.poulastaa.kyoku.data.repository

import com.poulastaa.kyoku.data.model.api.service.SpotifyPlaylistResponse
import com.poulastaa.kyoku.data.remote.ServiceApi
import com.poulastaa.kyoku.domain.repository.ServiceRepository
import javax.inject.Inject

class ServiceRepositoryImpl @Inject constructor(
    private val api: ServiceApi
) : ServiceRepository {
    override suspend fun getSpotifyPlaylist(
        tokenOrCookie: String,
        playlistId: String
    ): SpotifyPlaylistResponse? {
        return try {
            api.getSpotifyPlaylistSong(tokenOrCookie = tokenOrCookie, playlistId = playlistId)
        } catch (e: Exception) {
            null
        }
    }
}