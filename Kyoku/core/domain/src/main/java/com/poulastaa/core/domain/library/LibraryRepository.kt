package com.poulastaa.core.domain.library

import com.poulastaa.core.domain.model.PrevAlbum
import com.poulastaa.core.domain.utils.SavedAlbum
import com.poulastaa.core.domain.utils.SavedArtist
import com.poulastaa.core.domain.utils.SavedPlaylist
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    fun getPlaylist(): Flow<SavedPlaylist>
    fun getAlbum(): Flow<SavedAlbum>
    fun getArtist(): Flow<SavedArtist>
    suspend fun isFavourite(): Boolean
}