package com.poulastaa.data.model.home

import com.poulastaa.data.model.common.ResponseSong
import kotlinx.serialization.Serializable

@Serializable
data class ResponseAlbum(
    val id: Long = -1,
    val name: String = "",
    val listOfSongs: List<ResponseSong> = emptyList()
)
