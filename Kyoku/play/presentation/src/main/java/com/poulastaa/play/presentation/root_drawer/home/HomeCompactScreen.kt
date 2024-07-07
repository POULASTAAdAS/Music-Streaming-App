package com.poulastaa.play.presentation.root_drawer.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.poulastaa.play.domain.HomeToDrawerEvent
import com.poulastaa.play.presentation.root_drawer.home.components.HomeAppbar

@Composable
fun HomeCompactScreen(
    profileUrl: String,
    viewModel: HomeViewModel = hiltViewModel(),
    onEvent: (HomeToDrawerEvent) -> Unit,
) {
    HomeScreen(
        profileUrl = profileUrl,
        state = viewModel.state,
        onProfileClick = {
            onEvent(HomeToDrawerEvent.PROFILE_CLICK)
        },
        onSearchClick = {
            onEvent(HomeToDrawerEvent.SEARCH_CLICK)
        },
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    state: HomeUiState,
    profileUrl: String,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit,
    onEvent: (HomeUiEvent) -> Unit,
) {
    val appBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            HomeAppbar(
                scrollBehavior = appBarScrollBehavior,
                title = state.heading,
                profileUrl = profileUrl,
                onProfileClick = onProfileClick,
                onSearchClick = onSearchClick
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .padding(it)
        ) {

        }
    }
}