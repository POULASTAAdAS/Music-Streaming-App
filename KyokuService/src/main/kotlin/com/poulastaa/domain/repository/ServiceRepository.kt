package com.poulastaa.domain.repository

import com.poulastaa.data.model.*
import com.poulastaa.data.model.home.HomeDto
import com.poulastaa.domain.model.ReqUserPayload
import com.poulastaa.domain.model.route_model.req.home.HomeReq
import com.poulastaa.domain.model.route_model.req.pin.PinReq
import com.poulastaa.domain.model.route_model.req.playlist.CreatePlaylistWithSongReq
import com.poulastaa.domain.model.route_model.req.playlist.SavePlaylistReq
import com.poulastaa.domain.model.route_model.req.playlist.UpdatePlaylistReq

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

    suspend fun getSong(
        songId: Long,
    ): SongDto

    suspend fun updatePlaylist(
        req: UpdatePlaylistReq,
        payload: ReqUserPayload,
    ): Boolean

    suspend fun createPlaylist(
        req: CreatePlaylistWithSongReq,
        payload: ReqUserPayload,
    ): PlaylistDto

    suspend fun pinData(
        req: PinReq,
        payload: ReqUserPayload,
    ): Boolean

    suspend fun unPinData(
        req: PinReq,
        payload: ReqUserPayload,
    ): Boolean

    suspend fun deleteSavedData(
        id: Long,
        type: String,
        payload: ReqUserPayload,
    ): Boolean

    suspend fun getListOfData(
        req: GetDataReq,
        payload: ReqUserPayload,
    ): Any

    suspend fun getViewArtistData(
        artistId: Long,
        payload: ReqUserPayload,
    ): ViewArtistDto

    suspend fun getArtistOnId(
        artistId: Long,
        payload: ReqUserPayload,
    ): ArtistDto

    suspend fun getArtistSongPagingData(
        artistId: Long,
        page: Int,
        size: Int,
        payload: ReqUserPayload,
    ): ArtistPagerDataDto

    suspend fun getArtistAlbumPagingData(
        artistId: Long,
        page: Int,
        size: Int,
        payload: ReqUserPayload,
    ): ArtistPagerDataDto
}