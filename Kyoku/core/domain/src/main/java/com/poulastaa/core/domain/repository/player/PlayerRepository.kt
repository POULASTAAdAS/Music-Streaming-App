package com.poulastaa.core.domain.repository.player

import com.poulastaa.core.domain.PlayType
import com.poulastaa.core.domain.PlayerInfo
import com.poulastaa.core.domain.model.PlayerSong
import com.poulastaa.core.domain.utils.DataError
import com.poulastaa.core.domain.utils.EmptyResult
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    suspend fun loadData(id: Long, type: PlayType): EmptyResult<DataError.Network>
    fun getInfo(): Flow<PlayerInfo>
    fun getSongs(): Flow<List<PlayerSong>>
}