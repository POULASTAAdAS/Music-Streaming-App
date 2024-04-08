package com.poulastaa.data.model.common

import kotlinx.serialization.Serializable

@Serializable
data class ResponseSong( // todo add id to response song also fix auth api
    val id: Long = -1,
    val coverImage: String = "",
    val masterPlaylistUrl: String = "",
    val totalTime: String = "",
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val genre: String = "",
    val composer: String = "",
    val publisher: String = "",
    val albumArtist: String = "",
    val description: String = "",
    val track: String = "",
    val date: String = ""
)
