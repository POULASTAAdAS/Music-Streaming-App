package com.poulastaa.core.domain.home

import com.poulastaa.core.domain.model.DayType
import com.poulastaa.core.domain.model.HomeData
import com.poulastaa.core.domain.utils.DataError
import com.poulastaa.core.domain.utils.EmptyResult
import com.poulastaa.core.domain.utils.SavedPlaylist
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    suspend fun storeNewHomeResponse(dayType: DayType): EmptyResult<DataError.Network>

    suspend fun isNewUser(): Boolean

    suspend fun loadHomeData(): HomeData

    fun loadSavedPlaylist(): Flow<SavedPlaylist>

    suspend fun isArtistIsInLibrary(artistId: Long): Boolean
    suspend fun isAlbumInLibrary(albumId: Long): Boolean
    suspend fun isSongInFavourite(songId: Long): Boolean
}