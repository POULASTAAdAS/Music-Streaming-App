package com.poulastaa.data.model

sealed class EndPoints(val route: String) {
    data object GetSpotifyPlaylistSong : EndPoints(route = "/api/authorised/spotifyPlaylist")

    data object StoreBDate : EndPoints(route = "/api/authorised/storeBDate")

    data object SuggestGenre : EndPoints(route = "/api/authorised/suggestGenre")
    data object StoreGenre: EndPoints(route = "/api/authorised/storeGenre")

    data object SelectArtists : EndPoints(route = "/api/authorised/selectArtists")

    data object PlaySongMaster : EndPoints(route = "/api/authorised/playSong/master")
    data object PlaySongPlaylist : EndPoints(route = "/api/authorised/playSong/playlist")
    data object PlaySong : EndPoints(route = "/api/authorised/playSong/song")

    data object CoverImage : EndPoints(route = "/api/authorised/coverImage")

    data object UnAuthorised : EndPoints(route = "/api/unauthorised")
}