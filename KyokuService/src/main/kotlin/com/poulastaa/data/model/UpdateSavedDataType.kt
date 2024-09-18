package com.poulastaa.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class UpdateSavedDataType {
    ALBUM,
    PLAYLIST,
    PLAYLIST_SONG,
    ARTIST,
    FEV
}