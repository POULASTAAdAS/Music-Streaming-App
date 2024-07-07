package com.poulastaa.domain.repository

import com.poulastaa.data.model.PlaylistDto
import com.poulastaa.data.model.SuggestArtistDao
import com.poulastaa.data.model.SuggestGenreDto
import com.poulastaa.data.model.home.HomeDto
import com.poulastaa.domain.model.ReqUserPayload
import com.poulastaa.domain.model.route_model.req.home.HomeReq

interface ServiceRepository {
    suspend fun getSpotifyPlaylist(
        userPayload: ReqUserPayload,
        spotifyPayload: List<SpotifySongTitle>,
    ): PlaylistDto

    suspend fun updateBDate(
        userPayload: ReqUserPayload,
        date: Long,
    ): Boolean

    suspend fun getGenre(
        userPayload: ReqUserPayload,
        genreIds: List<Int>,
    ): SuggestGenreDto

    suspend fun storeGenre(
        userPayload: ReqUserPayload,
        genreIds: List<Int>,
    ): Boolean

    suspend fun getArtist(
        userPayload: ReqUserPayload,
        artistIds: List<Long>,
    ): SuggestArtistDao

    suspend fun storeArtist(
        userPayload: ReqUserPayload,
        artistIds: List<Long>,
    ): Boolean

    suspend fun homeReq(
        userPayload: ReqUserPayload,
        req: HomeReq
    ): HomeDto
}