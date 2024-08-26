package com.poulastaa.core.domain.repository.view_artist

import com.poulastaa.core.domain.model.Artist
import com.poulastaa.core.domain.model.ViewArtistData
import com.poulastaa.core.domain.utils.DataError
import com.poulastaa.core.domain.utils.EmptyResult
import com.poulastaa.core.domain.utils.Result

interface ViewArtistRepository {
    suspend fun getData(artistId: Long): Result<ViewArtistData, DataError.Network>
    suspend fun isArtistAlreadyFollowed(artistId: Long): Boolean

    suspend fun followArtist(artistId: Long): Result<Artist, DataError.Network>
    suspend fun onFollowArtist(artistId: Long): EmptyResult<DataError.Network>
}