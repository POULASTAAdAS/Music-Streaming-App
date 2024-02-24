package com.poulastaa.kyoku.data.repository

import com.poulastaa.kyoku.data.model.api.service.setup.spotiry_playlist.HandleSpotifyPlaylistStatus
import com.poulastaa.kyoku.data.model.api.service.setup.spotiry_playlist.SpotifyPlaylistResponse
import com.poulastaa.kyoku.data.model.api.service.setup.set_b_date.SetBDateReq
import com.poulastaa.kyoku.data.model.api.service.setup.set_b_date.SetBDateResponse
import com.poulastaa.kyoku.data.model.api.service.setup.set_b_date.SetBDateResponseStatus
import com.poulastaa.kyoku.data.remote.ServiceApi
import com.poulastaa.kyoku.domain.repository.ServiceRepository
import javax.inject.Inject

class ServiceRepositoryImpl @Inject constructor(
    private val serviceApi: ServiceApi
) : ServiceRepository {
    override suspend fun getSpotifyPlaylist(
        playlistId: String,
    ): SpotifyPlaylistResponse {
        return try {
            serviceApi.getSpotifyPlaylistSong(playlistId = playlistId)
        } catch (e: Exception) {
            SpotifyPlaylistResponse(
                status = HandleSpotifyPlaylistStatus.FAILURE,
                listOfResponseSong = emptyList()
            )
        }
    }

    override suspend fun sendBDateToServer(req: SetBDateReq): SetBDateResponse {
        return try {
            serviceApi.sendBDateToServer(req)
        } catch (e: Exception) {
            SetBDateResponse(SetBDateResponseStatus.FAILURE)
        }
    }
}