package com.poulastaa.core.domain.repository.create_playlist

import androidx.paging.PagingData
import com.poulastaa.core.domain.model.CreatePlaylistType
import com.poulastaa.core.domain.model.Song
import com.poulastaa.core.domain.utils.DataError
import com.poulastaa.core.domain.utils.EmptyResult
import com.poulastaa.core.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface CreatePlaylistRepository {
    suspend fun getStaticData(): Result<List<Pair<CreatePlaylistType, List<Song>>>, DataError.Network>
    suspend fun getPagingSong(query: String): Flow<PagingData<Song>>

    suspend fun saveSong(song: Song, playlistId: Long): EmptyResult<DataError.Network>
}