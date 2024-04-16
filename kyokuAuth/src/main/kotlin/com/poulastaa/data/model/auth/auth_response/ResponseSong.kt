package com.poulastaa.data.model.auth.auth_response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseSong(
    val id: Long = -1,
    val coverImage: String = "",
    val masterPlaylistUrl: String = "",
    val totalTime: String = "",
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val date: String = ""
)
