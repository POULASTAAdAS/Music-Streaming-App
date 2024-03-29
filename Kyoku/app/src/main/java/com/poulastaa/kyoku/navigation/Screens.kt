package com.poulastaa.kyoku.navigation

sealed class Screens(val route: String) {
    data object Auth : Screens("/auth")

    data object AuthEmailLogin : Screens("/auth/emailLogin")
    data object AuthEmailSignUp : Screens("/auth/emailSignUp")

    data object ForgotPassword : Screens("/auth/forgotPassword")

    data object GetSpotifyPlaylist : Screens("/setup/getSpotifyPlaylist")
    data object SetBirthDate : Screens("/setup/setBirthDate")
    data object SuggestGenre : Screens("/setup/suggestGenre")
    data object SuggestArtist : Screens("/setup/suggestArtist")

    data object HomeRoot : Screens("/app/homeRoot")

    data object Home : Screens("/app/homeRoot/home")
    data object Library : Screens("/app/homeRoot/library")
    data object Favourite : Screens("/app/homeRoot/addAlbum")

    data object CreatePlaylist : Screens("/app/homeRoot/library/createPlaylist")
    data object AddArtist : Screens("/app/homeRoot/library/addArtist")
    data object AddAlbum : Screens("/app/homeRoot/library/addAlbum")

    data object Profile : Screens("/app/homeRoot/profile")
    data object History : Screens("/app/homeRoot/history")
    data object Settings : Screens("/app/homeRoot/settings")

    data object Search : Screens("/route/homeRoot/search")
}