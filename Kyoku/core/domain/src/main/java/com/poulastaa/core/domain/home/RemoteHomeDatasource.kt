package com.poulastaa.core.domain.home

import com.poulastaa.core.domain.model.DayType
import com.poulastaa.core.domain.model.NewHome
import com.poulastaa.core.domain.model.Song
import com.poulastaa.core.domain.utils.DataError
import com.poulastaa.core.domain.utils.Result

interface RemoteHomeDatasource {
    suspend fun getNewHomeResponse(dayType: DayType): Result<NewHome, DataError.Network>

    suspend fun insertIntoFavourite(id: Long): Result<Song, DataError.Network>
    suspend fun removeFromFavourite(id: Long): Result<Unit, DataError.Network>
}