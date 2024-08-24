package com.poulastaa.play.presentation.view_artist

sealed interface ViewArtistUiEvent {
    data class OnSongClick(val id: Long) : ViewArtistUiEvent
    data object ExploreArtistClick : ViewArtistUiEvent

    data object FollowArtistClick : ViewArtistUiEvent
    data object UnFollowArtistClick : ViewArtistUiEvent
}