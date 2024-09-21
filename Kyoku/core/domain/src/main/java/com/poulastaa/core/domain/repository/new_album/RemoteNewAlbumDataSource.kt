package com.poulastaa.core.domain.repository.new_album

import androidx.paging.PagingData
import com.poulastaa.core.domain.model.AlbumPagingType
import com.poulastaa.core.domain.model.AlbumWithSong
import com.poulastaa.core.domain.model.PagingAlbumData
import com.poulastaa.core.domain.utils.DataError
import com.poulastaa.core.domain.utils.Result
import kotlinx.coroutines.flow.Flow

interface RemoteNewAlbumDataSource {
    fun getPagingAlbum(
        query: String,
        type: AlbumPagingType,
    ): Flow<PagingData<PagingAlbumData>>

    suspend fun saveAlbums(list: List<Long>): Result<List<AlbumWithSong>, DataError.Network>
}