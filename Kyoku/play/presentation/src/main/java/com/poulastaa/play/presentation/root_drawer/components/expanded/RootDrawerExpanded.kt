package com.poulastaa.play.presentation.root_drawer.components.expanded

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.poulastaa.core.domain.ScreenEnum
import com.poulastaa.core.presentation.designsystem.dimens
import com.poulastaa.play.domain.DrawerScreen
import com.poulastaa.play.domain.SaveScreen
import com.poulastaa.play.domain.TopBarToDrawerEvent
import com.poulastaa.play.presentation.player.PlayerUiEvent
import com.poulastaa.play.presentation.player.small_player.SmallCompactPlayer
import com.poulastaa.play.presentation.player.small_player.SmallExpandedPlayer
import com.poulastaa.play.presentation.root_drawer.RootDrawerUiEvent
import com.poulastaa.play.presentation.root_drawer.RootDrawerUiState
import com.poulastaa.play.presentation.root_drawer.home.HomeCompactScreen
import com.poulastaa.play.presentation.root_drawer.home.HomeOtherScreens
import com.poulastaa.play.presentation.root_drawer.library.LibraryCompactScreen
import com.poulastaa.play.presentation.root_drawer.library.LibraryOtherScreen
import com.poulastaa.play.presentation.settings.SettingsRootScreen
import com.poulastaa.play.presentation.view_artist.ViewArtistCompactRootScreen
import com.poulastaa.play.presentation.view_artist.ViewArtistExpandedRootScreen
import com.poulastaa.play.presentation.view_artist.ViewArtistOtherScreen

@Composable
fun RowScope.RootDrawerExpanded(
    navController: NavHostController,
    state: RootDrawerUiState,
    onSaveScreenToggle: (SaveScreen) -> Unit,
    onEvent: (RootDrawerUiEvent) -> Unit,
    onPlayerEvent: (PlayerUiEvent) -> Unit,
) {
    var toggle by remember {
        mutableStateOf(false)
    }

    val config = LocalConfiguration.current

    Surface(
        modifier = Modifier
            .wrapContentWidth()
            .fillMaxHeight(),
    ) {
        ExpandedDrawerContent(
            userName = state.username,
            profilePicUrl = state.profilePicUrl,
            isExpanded = toggle,
            isExpandSmall = false,
            saveScreen = state.saveScreen,
            onExpandToggle = {
                toggle = !toggle
            },
            navigate = {
                toggle = false

                when (it.screen) {
                    ScreenEnum.PROFILE -> navController.navigate(DrawerScreen.Profile.route) {
                        popUpTo(DrawerScreen.Profile.route) { inclusive = true }
                        launchSingleTop = true
                    }

                    ScreenEnum.HISTORY -> navController.navigate(DrawerScreen.History.route) {
                        popUpTo(DrawerScreen.History.route) { inclusive = true }
                        launchSingleTop = true
                    }

                    ScreenEnum.SETTINGS -> navController.navigate(DrawerScreen.Settings.route) {
                        popUpTo(DrawerScreen.Settings.route) { inclusive = true }
                        launchSingleTop = true
                    }

                    else -> onEvent(it)
                }
            },
            onSaveScreenToggle = onSaveScreenToggle,
        )
    }

    Box {
        NavHost(
            navController = navController,
            startDestination = state.startDestination,
            modifier = Modifier
                .then(
                    if (config.screenWidthDp > 980 &&
                        (state.addToPlaylistUiState.isOpen ||
                                state.viewUiState.isOpen ||
                                state.newAlbumUiState.isOpen ||
                                state.newArtisUiState.isOpen ||
                                state.createPlaylistUiState.isOpen ||
                                state.exploreArtistUiState.isOpen ||
                                state.player.isPlayerExtended)
                    ) Modifier
                        .fillMaxWidth(.6f)
                        .fillMaxHeight()
                    else Modifier.fillMaxSize()
                )
        ) {
            composable(route = DrawerScreen.Home.route) {
                HomeCompactScreen(
                    profileUrl = state.profilePicUrl,
                    navigate = { screen ->
                        when (screen) {
                            is HomeOtherScreens.AddAsPlaylist -> onEvent(
                                RootDrawerUiEvent.AddSongToPlaylist(
                                    screen.songId
                                )
                            )

                            is HomeOtherScreens.View -> onEvent(
                                RootDrawerUiEvent.View(
                                    screen.id,
                                    screen.type
                                )
                            )

                            is HomeOtherScreens.ViewArtist -> {
                                navController.navigate(
                                    route = DrawerScreen.ViewArtist.route + "/${screen.id}"
                                )
                            }
                        }
                    },
                    onEvent = { event ->
                        when (event) {
                            TopBarToDrawerEvent.PROFILE_CLICK -> Unit

                            TopBarToDrawerEvent.SEARCH_CLICK -> onEvent(
                                RootDrawerUiEvent.Navigate(
                                    screen = ScreenEnum.HOME_SEARCH
                                )
                            )
                        }
                    }
                )
            }

            composable(route = DrawerScreen.Library.route) {
                LibraryCompactScreen(
                    isExpanded = true,
                    profileUrl = state.profilePicUrl,
                    onProfileClick = {},
                    navigate = { screen ->
                        when (screen) {
                            is LibraryOtherScreen.View -> onEvent(
                                RootDrawerUiEvent.View(
                                    screen.id,
                                    screen.type
                                )
                            )

                            is LibraryOtherScreen.ViewArtist -> navController.navigate(
                                route = DrawerScreen.ViewArtist.route + "/${screen.id}"
                            )

                            LibraryOtherScreen.NewAlbum -> onEvent(RootDrawerUiEvent.NewAlbum)

                            LibraryOtherScreen.NewArtist -> onEvent(RootDrawerUiEvent.NewArtist)
                        }
                    }
                )
            }

            composable(route = DrawerScreen.Profile.route) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Profile",
                        fontSize = MaterialTheme.typography.displayLarge.fontSize,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            composable(route = DrawerScreen.History.route) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "History",
                        fontSize = MaterialTheme.typography.displayLarge.fontSize,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            composable(route = DrawerScreen.Settings.route) {
                SettingsRootScreen(
                    navigate = {
                        when (it) {
                            ScreenEnum.INTRO -> onEvent(
                                RootDrawerUiEvent.Navigate(
                                    screen = ScreenEnum.INTRO
                                )
                            )

                            else -> {
                                navController.popBackStack()
                                navController.navigate(it.name)
                            }
                        }
                    },
                    navigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = DrawerScreen.ViewArtist.route + DrawerScreen.ViewArtist.PARAM,
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.LongType
                    }
                )
            ) {
                val id = it.arguments?.getLong("id") ?: -1

                if (state.addToPlaylistUiState.isOpen ||
                    state.viewUiState.isOpen ||
                    state.newAlbumUiState.isOpen ||
                    state.newArtisUiState.isOpen ||
                    state.createPlaylistUiState.isOpen ||
                    (state.exploreArtistUiState.isOpen && config.screenWidthDp > 980)  // stopping compact view for compact screen
                ) ViewArtistCompactRootScreen(
                    artistId = id,
                    onArtistDetailScreenOpen = { artistId ->
                        onEvent(RootDrawerUiEvent.OnExploreArtistOpen(artistId))
                    },
                    navigate = { screen ->
                        when (screen) {
                            is ViewArtistOtherScreen.AddSongToPlaylist -> onEvent(
                                RootDrawerUiEvent.AddSongToPlaylist(
                                    id = screen.id
                                )
                            )

                            is ViewArtistOtherScreen.ViewArtist -> {

                            }
                        }

                    },
                    navigateBack = {
                        navController.popBackStack()
                    }
                )
                else ViewArtistExpandedRootScreen(
                    artistId = id,
                    navigateToArtistDetail = { artistId ->
                        onEvent(RootDrawerUiEvent.OnExploreArtistOpen(artistId))
                    },
                    navigate = { screen ->
                        when (screen) {
                            is ViewArtistOtherScreen.AddSongToPlaylist -> onEvent(
                                RootDrawerUiEvent.AddSongToPlaylist(
                                    id = screen.id
                                )
                            )

                            is ViewArtistOtherScreen.ViewArtist -> {

                            }
                        }

                    },
                    navigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        this@RootDrawerExpanded.AnimatedVisibility(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth(.3f)
                .fillMaxHeight(.3f)
                .align(Alignment.BottomEnd)
                .padding(MaterialTheme.dimens.medium1),
            visible = state.player.isData &&
                    state.player.queue.isNotEmpty() &&
                    config.screenWidthDp > 980 &&
                    !state.player.isPlayerExtended,
            enter = fadeIn() + expandIn(expandFrom = Alignment.Center) +
                    slideInHorizontally(tween(400)) { it },
            exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.CenterEnd) +
                    slideOutHorizontally(tween(400)) { it }
        ) {
            if (state.player.queue.isNotEmpty()) SmallExpandedPlayer(
                header = state.header,
                song = state.player.queue[state.player.info.currentPlayingIndex].copy(
                    colors = state.player.queue[state.player.info.currentPlayingIndex].colors.ifEmpty {
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.surfaceContainer
                        )
                    }
                ),
                hasNext = state.player.info.hasNext,
                hasPrev = state.player.info.hasPrev,
                onEvent = onPlayerEvent
            )
        }
    }
}