package com.poulastaa.play.presentation.view_artist

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import com.poulastaa.core.presentation.designsystem.AppThem
import com.poulastaa.core.presentation.designsystem.components.CompactErrorScreen
import com.poulastaa.core.presentation.designsystem.dimens
import com.poulastaa.core.presentation.ui.model.ArtistUiSong
import com.poulastaa.core.presentation.ui.model.UiArtist
import com.poulastaa.play.domain.DataLoadingState
import com.poulastaa.play.presentation.ArtistSongDetailsCard
import com.poulastaa.play.presentation.root_drawer.library.components.ImageGrid
import com.poulastaa.play.presentation.view_artist.components.ExploreArtistButton
import com.poulastaa.play.presentation.view_artist.components.ViewArtistCompactLoading
import com.poulastaa.play.presentation.view_artist.components.ViewArtistNameRow
import com.poulastaa.play.presentation.view_artist.components.ViewArtistTopBar
import kotlinx.coroutines.delay

@Composable
fun ViewArtistCompactRootScreen(
    modifier: Modifier = Modifier,
    artistId: Long,
    viewModel: ViewArtistViewModel = hiltViewModel(),
    navigateToArtistDetail: (artistId: Long) -> Unit,
    navigateBack: () -> Unit
) {
    LaunchedEffect(key1 = artistId) {
        viewModel.loadData(artistId)
    }

    ViewArtistScreen(
        modifier = modifier,
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        navigateToArtistDetail = navigateToArtistDetail,
        navigateBack = navigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ViewArtistScreen(
    modifier: Modifier = Modifier,
    state: ViewArtistUiState,
    onEvent: (ViewArtistUiEvent) -> Unit,
    navigateToArtistDetail: (artistId: Long) -> Unit,
    navigateBack: () -> Unit
) {
    val scroll = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            ViewArtistTopBar(scrollBehavior = scroll) {
                navigateBack()
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = state.loadingState,
            label = "view artist animation transition"
        ) {
            when (it) {
                DataLoadingState.LOADING -> ViewArtistCompactLoading(
                    modifier = Modifier.padding(innerPadding)
                )

                DataLoadingState.LOADED -> Content(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(innerPadding),
                    scrollBehavior = scroll,
                    state = state,
                    navigateToArtistDetail = navigateToArtistDetail,
                    onEvent = onEvent
                )

                DataLoadingState.ERROR -> CompactErrorScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior,
    state: ViewArtistUiState,
    navigateToArtistDetail: (artistId: Long) -> Unit,
    onEvent: (ViewArtistUiEvent) -> Unit
) {
    LazyColumn(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        contentPadding = PaddingValues(MaterialTheme.dimens.medium1)
    ) {
        item {
            Card(
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.dimens.large2),
                shape = MaterialTheme.shapes.small
            ) {
                ImageGrid(
                    header = state.header,
                    urls = listOf(state.data.artist.coverImageUrl)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))
        }

        item {
            ViewArtistNameRow(
                popularity = state.data.popularity,
                name = state.data.artist.name,
                isArtistFollowed = state.data.isArtistFollowed,
                onFollowArtistToggle = {
                    onEvent(ViewArtistUiEvent.FollowArtistToggleClick)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))
        }

        item {
            ExploreArtistButton(
                modifier = Modifier.fillMaxWidth(.5f),
                name = state.data.artist.name
            ) {
                navigateToArtistDetail(state.data.artist.id)
            }
        }

        item {
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))
        }

        items(state.data.listOfSong) { song ->
            ArtistSongDetailsCard(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraSmall)
                    .clickable {
                        onEvent(ViewArtistUiEvent.OnSongClick(song.id))
                    },
                header = state.header,
                song = song,
                onThreeDotCLick = {

                }
            )
        }

        item {
            ExploreArtistButton(
                name = state.data.artist.name
            ) {
                navigateToArtistDetail(state.data.artist.id)
            }
        }

        item {
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium1))
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    AppThem {
        var loadingState by remember {
            mutableStateOf(DataLoadingState.LOADED)
        }

        LaunchedEffect(key1 = Unit) {
            delay(500)
            loadingState = DataLoadingState.LOADED
        }

        ViewArtistScreen(
            state = ViewArtistUiState(
                data = UiArtistData(
                    artist = UiArtist(
                        name = "That Cool Artist"
                    ),
                    listOfSong = (1..10).map {
                        ArtistUiSong(
                            title = "That Cool Song: $it",
                            popularity = it.toLong()
                        )
                    },
                ),
                loadingState = DataLoadingState.LOADED
            ),
            onEvent = {},
            navigateToArtistDetail = {},
            navigateBack = {}
        )
    }
}