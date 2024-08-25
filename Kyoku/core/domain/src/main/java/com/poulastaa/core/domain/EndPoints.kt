package com.poulastaa.core.domain

sealed class EndPoints(val route: String) {
    data object Auth : EndPoints(route = "/api/auth")

    data object SignUpEmailVerificationCheck :
        EndPoints(route = "/api/auth/signUpEmailVerificationCheck")

    data object LogInEmailVerificationCheck :
        EndPoints(route = "/api/auth/logInEmailVerificationCheck")

    data object ForgotPassword : EndPoints(route = "/api/auth/forgotPassword")

    data object RefreshToken : EndPoints(route = "/api/auth/refreshToken")

    data object GetSpotifyPlaylistSong : EndPoints(route = "/api/authorised/spotifyPlaylist")

    data object StoreBDate : EndPoints(route = "/api/authorised/storeBDate")

    data object SuggestGenre : EndPoints(route = "/api/authorised/suggestGenre")
    data object StoreGenre : EndPoints(route = "/api/authorised/storeGenre")

    data object SuggestArtist : EndPoints(route = "/api/authorised/suggestArtist")
    data object StoreArtist : EndPoints(route = "/api/authorised/storeArtist")

    data object NewHome : EndPoints(route = "/api/authorised/newHome")

    data object AddToFavourite : EndPoints(route = "/api/authorised/addToFavourite")
    data object RemoveFromFavourite : EndPoints(route = "/api/authorised/removeFromFavourite")

    data object AddArtist : EndPoints(route = "/api/authorised/addArtist")
    data object RemoveArtist : EndPoints(route = "/api/authorised/removeArtist")

    data object AddAlbum : EndPoints(route = "/api/authorised/addAlbum")
    data object RemoveAlbum : EndPoints(route = "/api/authorised/removeAlbum")

    data object SavePlaylist : EndPoints(route = "/api/authorised/savePlaylist")

    data object GetSong : EndPoints(route = "/api/authorised/getSong")
    data object UpdatePlaylist : EndPoints(route = "/api/authorised/updatePlaylist")
    data object UpdateFavourite : EndPoints(route = "/api/authorised/updateFavourite")

    data object CreatePlaylist : EndPoints(route = "/api/authorised/createPlaylist")

    data object PinData : EndPoints(route = "/api/authorised/pinData")
    data object UnPinData : EndPoints(route = "/api/authorised/unPinData")

    data object DeleteSavedData : EndPoints(route = "/api/authorised/deleteSavedData")

    data object GetTypeData : EndPoints(route = "/api/authorised/getTypeData")

    data object ExploreArtist : EndPoints(route = "/api/authorised/exploreArtist")
    data object FollowArtist : EndPoints(route = "/api/authorised/followArtist")
    data object UnFollowArtist : EndPoints(route = "/api/authorised/unFollowArtist")
}