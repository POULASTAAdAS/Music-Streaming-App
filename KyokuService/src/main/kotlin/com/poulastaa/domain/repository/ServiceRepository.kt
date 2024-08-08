package com.poulastaa.domain.repository

import com.poulastaa.data.model.*
import com.poulastaa.data.model.home.HomeDto
import com.poulastaa.domain.model.ReqUserPayload
import com.poulastaa.domain.model.route_model.req.home.HomeReq
import com.poulastaa.domain.model.route_model.req.playlist.SavePlaylistReq

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
        req: HomeReq,
    ): HomeDto

    suspend fun getLoginData(
        userType: String,
        token: String,
    ): LogInDto

    suspend fun addToFavourite(
        id: Long,
        userPayload: ReqUserPayload,
    ): SongDto

    suspend fun removeFromFavourite(
        id: Long,
        userPayload: ReqUserPayload,
    ): Boolean

    suspend fun addArtist(
        artistId: Long,
        payload: ReqUserPayload,
    ): ArtistDto

    suspend fun removeArtist(
        id: Long,
        userPayload: ReqUserPayload,
    ): Boolean

    suspend fun addAlbum(
        albumId: Long,
        payload: ReqUserPayload,
    ): AlbumWithSongDto

    suspend fun removeAlbum(
        id: Long,
        userPayload: ReqUserPayload,
    ): Boolean

    suspend fun savePlaylist(
        req: SavePlaylistReq,
        payload: ReqUserPayload,
    ): PlaylistDto
}