package com.poulastaa.data.model.auth.auth_response

import kotlinx.serialization.Serializable

@Serializable
data class SongPreview(
    val id: String = "",
    val title: String = "",
    val coverImage: String = "",
    val artist: String = "",
    val album: String = "",
    val points: Long = 0,
    val year: String = "",
)
