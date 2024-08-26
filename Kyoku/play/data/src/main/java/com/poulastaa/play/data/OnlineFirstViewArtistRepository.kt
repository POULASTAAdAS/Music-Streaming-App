package com.poulastaa.play.data

import com.poulastaa.core.domain.model.Artist
import com.poulastaa.core.domain.model.ViewArtistData
import com.poulastaa.core.domain.repository.view_artist.LocalViewArtistDatasource
import com.poulastaa.core.domain.repository.view_artist.RemoveViewArtistDatasource
import com.poulastaa.core.domain.repository.view_artist.ViewArtistRepository
import com.poulastaa.core.domain.utils.DataError
import com.poulastaa.core.domain.utils.EmptyResult
import com.poulastaa.core.domain.utils.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import javax.inject.Inject

class OnlineFirstViewArtistRepository @Inject constructor(
    private val local: LocalViewArtistDatasource,
    private val remote: RemoveViewArtistDatasource,
    private val application: CoroutineScope
) : ViewArtistRepository {
    override suspend fun getData(artistId: Long): Result<ViewArtistData, DataError.Network> {
        val result = remote.getData(artistId)
        if (result is Result.Success) application.async { local.followArtist(result.data.artist) }
            .await()

        return result
    }

    override suspend fun isArtistAlreadyFollowed(artistId: Long): Boolean =
        application.async { local.getArtist(artistId) != null }.await()

    override suspend fun followArtist(artistId: Long): Result<Artist, DataError.Network> {
        val result = remote.followArtist(artistId)
        if (result is Result.Success) application.async { local.followArtist(result.data) }.await()

        return result
    }

    override suspend fun onFollowArtist(artistId: Long): EmptyResult<DataError.Network> {
        val remote = application.async { remote.unFollowArtist(artistId) }.await()
        if (remote is Result.Success) application.async { local.unFollowArtist(artistId) }.await()

        return remote
    }
}