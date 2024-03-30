package com.poulastaa.domain.repository

import com.poulastaa.data.model.artist.ArtistMostPopularSongReq
import com.poulastaa.data.model.artist.ArtistMostPopularSongRes
import com.poulastaa.data.model.artist.ArtistPageReq
import com.poulastaa.data.model.artist.ArtistPageResponse
import com.poulastaa.data.model.home.HomeReq
import com.poulastaa.data.model.home.HomeResponse
import com.poulastaa.data.model.setup.artist.StoreArtistReq
import com.poulastaa.data.model.setup.artist.StoreArtistResponse
import com.poulastaa.data.model.setup.artist.SuggestArtistReq
import com.poulastaa.data.model.setup.artist.SuggestArtistResponse
import com.poulastaa.data.model.setup.genre.StoreGenreReq
import com.poulastaa.data.model.setup.genre.StoreGenreResponse
import com.poulastaa.data.model.setup.genre.SuggestGenreReq
import com.poulastaa.data.model.setup.genre.SuggestGenreResponse
import com.poulastaa.data.model.setup.set_b_date.SetBDateResponse
import com.poulastaa.data.model.setup.spotify.SpotifyPlaylistResponse
import com.poulastaa.data.model.utils.UserTypeHelper

interface UserServiceRepository {
    suspend fun getFoundSpotifySongs(json: String, helper: UserTypeHelper): SpotifyPlaylistResponse

    suspend fun storeBDate(date: Long, helper: UserTypeHelper): SetBDateResponse

    suspend fun suggestGenre(req: SuggestGenreReq, helper: UserTypeHelper): SuggestGenreResponse
    suspend fun storeGenre(req: StoreGenreReq, helper: UserTypeHelper): StoreGenreResponse

    suspend fun suggestArtist(req: SuggestArtistReq, helper: UserTypeHelper): SuggestArtistResponse
    suspend fun storeArtist(req: StoreArtistReq, helper: UserTypeHelper): StoreArtistResponse

    suspend fun generateHomeResponse(req: HomeReq, helper: UserTypeHelper): HomeResponse

    suspend fun getMostPopularSongOfArtist(req: ArtistMostPopularSongReq): ArtistMostPopularSongRes

    suspend fun getArtistPageResponse(req: ArtistPageReq, helper: UserTypeHelper): ArtistPageResponse
}