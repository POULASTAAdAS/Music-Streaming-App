package com.poulastaa.kyoku.navigation

import androidx.navigation.NavHostController
import com.poulastaa.kyoku.data.model.screens.auth.UiEvent

fun NavHostController.navigate(event: UiEvent.Navigate) = this.navigate(event.route)
fun NavHostController.navigateWithData(event: UiEvent.NavigateWithData) = when (event.route) {
    Screens.SongView.route -> navigate(
        route = "${event.route}${event.itemsType.title}/${event.id}/${event.name}/${event.isApiCall}"
    )

    Screens.CreatePlaylist.route -> navigate(
        route = "${event.route}${event.id}/${event.name}/${event.longClickType}"
    )

    Screens.EditPlaylist.route -> navigate(
        route = "${event.route}${event.id}/${event.songType}"
    )

    else -> navigate(route = "${event.route}${event.name}/${event.isApiCall}")
}