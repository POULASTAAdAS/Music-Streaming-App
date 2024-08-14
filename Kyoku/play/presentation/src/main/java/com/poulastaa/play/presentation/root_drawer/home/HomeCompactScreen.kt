package com.poulastaa.play.presentation.root_drawer.home

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.poulastaa.core.presentation.ui.ObserveAsEvent
import com.poulastaa.play.domain.TopBarToDrawerEvent
import com.poulastaa.play.presentation.OtherScreens
import com.poulastaa.play.presentation.root_drawer.home.components.HomeScreen

@Composable
fun HomeCompactScreen(
    profileUrl: String,
    viewModel: HomeViewModel = hiltViewModel(),
    navigate: (OtherScreens) -> Unit,
    onEvent: (TopBarToDrawerEvent) -> Unit,
) {
    val context = LocalContext.current

    ObserveAsEvent(flow = viewModel.uiEvent) {
        when (it) {
            is HomeUiAction.EmitToast -> Toast.makeText(
                context,
                it.message.asString(context),
                Toast.LENGTH_LONG
            ).show()

            is HomeUiAction.Navigate -> navigate(it.screen)
        }
    }

    HomeScreen(
        profileUrl = profileUrl,
        state = viewModel.state,
        onProfileClick = {
            onEvent(TopBarToDrawerEvent.PROFILE_CLICK)
        },
        onSearchClick = {
            onEvent(TopBarToDrawerEvent.SEARCH_CLICK)
        },
        onEvent = viewModel::onEvent
    )
}