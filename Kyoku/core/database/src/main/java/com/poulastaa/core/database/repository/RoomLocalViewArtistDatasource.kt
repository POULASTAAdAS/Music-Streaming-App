package com.poulastaa.core.database.repository

import com.poulastaa.core.database.dao.CommonDao
import com.poulastaa.core.database.mapper.toArtist
import com.poulastaa.core.database.mapper.toArtistEntity
import com.poulastaa.core.domain.model.Artist
import com.poulastaa.core.domain.repository.view_artist.LocalViewArtistDatasource
import javax.inject.Inject

class RoomLocalViewArtistDatasource @Inject constructor(
    private val commonDao: CommonDao
) : LocalViewArtistDatasource {
    override suspend fun getArtist(artistId: Long): Artist? =
        commonDao.getArtistByIdOrNull(artistId)?.toArtist()

    override suspend fun saveArtist(artist: Artist) {
        commonDao.insertArtist(artist.toArtistEntity())
    }

    override suspend fun followArtist(artistId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun onFollowArtist(artistId: Long) {
        TODO("Not yet implemented")
    }
}