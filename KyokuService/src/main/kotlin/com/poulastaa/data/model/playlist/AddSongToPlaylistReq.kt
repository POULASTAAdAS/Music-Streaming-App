package com.poulastaa.data.model.playlist

import kotlinx.serialization.Serializable

@Serializable
data class AddSongToPlaylistReq(
    val songId: Long = 0,
    val isAddToFavourite: Boolean = false,
    val add: List<Long> = emptyList(),
    val remove: List<Long> = emptyList()
)