package com.poulastaa.play.presentation


sealed interface OtherScreens {
    data class AddAsPlaylist(
        val songId: Long
    ) : OtherScreens
}