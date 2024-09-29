package com.poulastaa.core.domain.repository.player

import com.poulastaa.core.domain.RecentHistoryOtherType
import com.poulastaa.core.domain.model.AlbumWithSong
import com.poulastaa.core.domain.model.PlaylistData
import com.poulastaa.core.domain.model.SongOtherData
import com.poulastaa.core.domain.utils.DataError
import com.poulastaa.core.domain.utils.EmptyResult
import com.poulastaa.core.domain.utils.Result

interface RemotePlayerDatasource {
    suspend fun getAlbum(id: Long): Result<AlbumWithSong, DataError.Network>
    suspend fun getPlaylist(id: Long): Result<PlaylistData, DataError.Network>
    suspend fun loadOtherInfo(songId: Long): Result<SongOtherData, DataError.Network>

    suspend fun addSongToHistory(
        songId: Long,
        otherId: Long,
        type: RecentHistoryOtherType
    ): EmptyResult<DataError.Network>
}