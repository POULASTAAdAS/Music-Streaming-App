package com.poulastaa.kyoku.data.model.api.service.home

import com.poulastaa.kyoku.data.model.api.service.ResponseSong
import kotlinx.serialization.Serializable

@Serializable
data class ResponseAlbum(
    val id: Long = -1,
    val name: String = "",
    val listOfSongs: List<ResponseSong> = emptyList()
)
