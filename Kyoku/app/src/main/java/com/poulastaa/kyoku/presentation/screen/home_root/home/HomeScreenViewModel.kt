package com.poulastaa.kyoku.presentation.screen.home_root.home

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poulastaa.kyoku.connectivity.NetworkObserver
import com.poulastaa.kyoku.data.model.SignInStatus
import com.poulastaa.kyoku.data.model.api.service.home.HomeReq
import com.poulastaa.kyoku.data.model.api.service.home.HomeResponseStatus
import com.poulastaa.kyoku.data.model.api.service.home.HomeType
import com.poulastaa.kyoku.data.model.screens.auth.UiEvent
import com.poulastaa.kyoku.data.model.screens.common.ItemsType
import com.poulastaa.kyoku.data.model.screens.common.UiPlaylistPrev
import com.poulastaa.kyoku.data.model.screens.home.HomeLongClickType
import com.poulastaa.kyoku.data.model.screens.home.HomeUiArtistPrev
import com.poulastaa.kyoku.data.model.screens.home.HomeUiEvent
import com.poulastaa.kyoku.data.model.screens.home.HomeUiState
import com.poulastaa.kyoku.data.model.screens.home.SongType
import com.poulastaa.kyoku.data.repository.DatabaseRepositoryImpl
import com.poulastaa.kyoku.domain.repository.DataStoreOperation
import com.poulastaa.kyoku.domain.repository.ServiceRepository
import com.poulastaa.kyoku.navigation.Screens
import com.poulastaa.kyoku.utils.getHomeReqTimeType
import com.poulastaa.kyoku.utils.toHomeAlbumUiPrev
import com.poulastaa.kyoku.utils.toHomeUiSongPrev
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val connectivity: NetworkObserver,
    private val ds: DataStoreOperation,
    private val db: DatabaseRepositoryImpl,
    private val api: ServiceRepository
) : ViewModel() {
    private val network = mutableStateOf(NetworkObserver.STATUS.UNAVAILABLE)

    init {
        viewModelScope.launch {
            connectivity.observe().collect {
                network.value = it
                state = state.copy(
                    isInternetAvailable = it == NetworkObserver.STATUS.AVAILABLE,
                    isInternetError = false
                )
                if (!state.isInternetAvailable)
                    state = state.copy(
                        isInternetError = true
                    )
            }
        }
    }

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var state by mutableStateOf(HomeUiState())
        private set

    private suspend fun isTillNewUser() = db.checkIfNewUser()
    private suspend fun isFirstOpen() = db.isFirstOpen()

    fun loadStartupData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val signInState = ds.readSignedInState().first()

            if (signInState == SignInStatus.HOME.name  // NEW USER
                && isFirstOpen()
            ) {
                // make api call
                val response = api.homeReq(
                    req = HomeReq(
                        type = HomeType.NEW_USER_REQ,
                        time = getHomeReqTimeType(),
                        isOldEnough = false
                    )
                )

                db.setValues(
                    context,
                    async {
                        ds.readTokenOrCookie().first()
                    }.await()
                )

                // store response and read response
                when (response.status) {
                    HomeResponseStatus.SUCCESS -> {
                        db.insertIntoFevArtistMixPrev(list = response.fevArtistsMixPreview)
                        db.insertIntoAlbumPrev(list = response.albumPreview.listOfPreviewAlbum)
                        db.insertResponseArtistPrev(list = response.artistsPreview)
                        db.insertDailyMixPrev(response.dailyMixPreview)

                        // load from db
                        delay(3000)
                        loadFromDb()
                    }

                    HomeResponseStatus.FAILURE -> {
                        onEvent(HomeUiEvent.EmitToast("Opp's Something went wrong."))
                    }
                }
            } else {
                state = if (isTillNewUser())
                    state.copy(
                        dataType = HomeType.NEW_USER_REQ
                    )
                else state.copy(
                    dataType = HomeType.ALREADY_USER_REQ
                )
                delay(800)
                loadFromDb()
            }
        }
    }

    private fun loadFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val fevArtistMixPrev = async {
                state = state.copy(
                    data = state.data.copy(
                        fevArtistMixPrevUrls = db.readFevArtistMixPrev()
                    )
                )
            }

            val dailyMixPrev = async {
                state = state.copy(
                    data = state.data.copy(
                        dailyMixPrevUrls = db.readDailyMixPrevUrls()
                    )
                )
            }


            val albumPrev = async {
                db.readAllAlbumPrev().collect {
                    state = state.copy(
                        data = state.data.copy(
                            albumPrev = it.toHomeAlbumUiPrev()
                        )
                    )
                }
            }

            val artistPrev = async {
                db.readAllArtistPrev().collect {
                    state = state.copy(
                        data = state.data.copy( // todo check taking to long to map
                            artistPrev = it.groupBy { result ->
                                result.artistId
                            }.map { entry ->
                                HomeUiArtistPrev(
                                    id = entry.key,
                                    name = entry.value[0].name,
                                    artistCover = entry.value[0].artisUrl,
                                    lisOfPrevSong = entry.value.map { song ->
                                        song.toHomeUiSongPrev()
                                    }
                                )
                            }
                        )
                    )
                }
            }


            val playlist = async {
                db.readPlaylistPreview().collect {
                    state = state.copy(
                        data = state.data.copy(
                            playlist = it.groupBy { result -> result.playlistId }
                                .map { entry ->
                                    UiPlaylistPrev(
                                        id = entry.key,
                                        name = entry.value[0].name,
                                        listOfUrl = entry.value.map { url ->
                                            url.coverImage
                                        }.take(4)
                                    )
                                }
                        )
                    )
                }
            }

            val historyPrev = async {
                db.redRecentlyPlayed().collect {
                    state = state.copy(
                        data = state.data.copy(
                            historyPrev = it
                        )
                    )
                }
            }

            val savedAlbumPrev = async {
                db.radSavedAlbumPrev().collect {
                    state = state.copy(
                        data = state.data.copy(
                            savedAlbumPrev = it
                        )
                    )
                }
            }

            val favourites = async {
                db.readFavouritePrev()
            }

            fevArtistMixPrev.await()
            albumPrev.await()
            artistPrev.await()
            dailyMixPrev.await()
            playlist.await()
            historyPrev.await()
            savedAlbumPrev.await()

            favourites.await().let {
                state = state.copy(
                    data = state.data.copy(
                        favourites = it > 0
                    )
                )
            }
        }.let {
            state = state.copy(
                isLoading = false
            )
        }
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.EmitToast -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _uiEvent.send(UiEvent.ShowToast(event.message))
                }
            }

            HomeUiEvent.SomethingWentWrong -> {
                viewModelScope.launch(Dispatchers.IO) {

                }
            }

            is HomeUiEvent.ItemClick -> {
                if (!state.isLoading)
                    when (event.type) {
                        ItemsType.ALBUM_PREV -> {
                            event
                        }

                        ItemsType.FAVOURITE -> {
                            event
                        }

                        ItemsType.SONG -> {
//                            viewModelScope.launch(Dispatchers.IO) { // todo send more data to identify
//                                _uiEvent.send(UiEvent.Navigate(Screens.Player.route))
//                            }

                            return
                        }

                        ItemsType.HISTORY -> {
                            event
                        }

                        ItemsType.ERR -> {
                            viewModelScope.launch(Dispatchers.IO) {
                                _uiEvent.send(
                                    UiEvent.NavigateWithData(
                                        route = Screens.AllFromArtist.route,
                                        name = event.name,
                                        isApiCall = true
                                    )
                                )
                            }
                            return
                        }

                        else -> event
                    }.let {
                        Log.d("item", it.toString())

                        viewModelScope.launch(Dispatchers.IO) {
                            _uiEvent.send(
                                UiEvent.NavigateWithData(
                                    route = Screens.SongView.route,
                                    itemsType = it.type,
                                    name = it.name,
                                    id = it.id,
                                    isApiCall = it.isApiCall
                                )
                            )
                        }
                    }
            }

            is HomeUiEvent.ItemLongClick -> {
                state = state.copy(
                    isBottomSheetOpen = true,
                    isBottomSheetLoading = true
                )

                when (event.type) {
                    HomeLongClickType.ALBUM_PREV -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            val albumDef = async {
                                state.data.albumPrev.firstOrNull {
                                    it.id == event.id
                                }
                            }

                            val isAlreadySavedDef = async {
                                db.checkIfAlbumAlreadyInLibrary(event.id)
                            }

                            val album = albumDef.await()
                            val isAlreadySaved = isAlreadySavedDef.await()

                            if (album == null) {
                                onEvent(HomeUiEvent.SomethingWentWrong)

                                state = state.copy(
                                    isBottomSheetOpen = false
                                )

                                return@launch
                            }

                            state = state.copy(
                                isBottomSheetLoading = false,
                                bottomSheetData = state.bottomSheetData.copy(
                                    id = album.id,
                                    name = album.name,
                                    urls = listOf(album.coverImage),
                                    type = HomeLongClickType.ALBUM_PREV,
                                    isAlreadySaved = isAlreadySaved
                                )
                            )
                        }
                    }

                    HomeLongClickType.ARTIST_MIX -> {
                        val urls = state.data.fevArtistMixPrevUrls

                        if (urls.isEmpty()) {
                            onEvent(HomeUiEvent.SomethingWentWrong)

                            state = state.copy(
                                isBottomSheetOpen = false
                            )

                            return
                        }

                        state = state.copy(
                            isBottomSheetLoading = false,
                            bottomSheetData = state.bottomSheetData.copy(
                                name = "Artist Mix",
                                urls = urls,
                                type = HomeLongClickType.ARTIST_MIX
                            )
                        )
                    }

                    HomeLongClickType.DAILY_MIX -> {
                        val urls = state.data.dailyMixPrevUrls

                        if (urls.isEmpty()) {
                            onEvent(HomeUiEvent.SomethingWentWrong)

                            state = state.copy(
                                isBottomSheetOpen = false
                            )

                            return
                        }

                        state = state.copy(
                            isBottomSheetLoading = false,
                            bottomSheetData = state.bottomSheetData.copy(
                                name = "Daily Mix",
                                urls = urls,
                                type = HomeLongClickType.DAILY_MIX
                            )
                        )
                    }

                    HomeLongClickType.HISTORY_SONG -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            val songDef = async {
                                state.data.historyPrev.firstOrNull {
                                    it.id == event.id
                                }
                            }

                            val isOnFavouriteDef = async {
                                db.checkIfSongAlreadyInFavourite(event.id)
                            }

                            val song = songDef.await()
                            val isOnFavourite = isOnFavouriteDef.await()

                            if (song == null) {
                                onEvent(HomeUiEvent.SomethingWentWrong)

                                state = state.copy(
                                    isBottomSheetOpen = false
                                )

                                return@launch
                            }

                            state = state.copy(
                                isBottomSheetLoading = false,
                                bottomSheetData = state.bottomSheetData.copy(
                                    id = song.id,
                                    name = song.title,
                                    urls = listOf(song.coverImage),
                                    type = HomeLongClickType.HISTORY_SONG,
                                    isAlreadySaved = isOnFavourite
                                )
                            )
                        }
                    }

                    HomeLongClickType.ARTIST_SONG -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            val songDef = async {
                                state.data.artistPrev.firstNotNullOfOrNull {
                                    it.lisOfPrevSong.firstOrNull { song ->
                                        song.id == event.id
                                    }
                                }
                            }

                            val isOnFavouriteDef = async {
                                db.checkIfSongAlreadyInFavourite(event.id)
                            }

                            val song = songDef.await()
                            val isOnFavourite = isOnFavouriteDef.await()

                            if (song == null) {
                                onEvent(HomeUiEvent.SomethingWentWrong)

                                state = state.copy(
                                    isBottomSheetOpen = false
                                )

                                return@launch
                            }


                            state = state.copy(
                                isBottomSheetLoading = false,
                                bottomSheetData = state.bottomSheetData.copy(
                                    id = song.id,
                                    name = song.title,
                                    urls = listOf(song.coverImage),
                                    type = HomeLongClickType.ARTIST_SONG,
                                    isAlreadySaved = isOnFavourite
                                )
                            )
                        }
                    }
                }
            }

            is HomeUiEvent.BottomSheetItemClick -> {
                state = state.copy(
                    isBottomSheetOpen = false,
                    isBottomSheetLoading = true
                )

                when (event) {
                    HomeUiEvent.BottomSheetItemClick.CancelClicked -> Unit

                    is HomeUiEvent.BottomSheetItemClick.PlaySong -> {
                        Log.d("data", "PlaySong: ${event.id} , ${event.type}")
                    }

                    is HomeUiEvent.BottomSheetItemClick.ViewArtist -> {
                        Log.d("data", "ViewArtist: ${event.id} , ${event.type}")
                    }

                    is HomeUiEvent.BottomSheetItemClick.AddToFavourite -> {
                        if (!state.isInternetAvailable) {
                            onEvent(HomeUiEvent.EmitToast("Please check your Internet connection"))

                            return
                        }

                        viewModelScope.launch(Dispatchers.IO) {
                            val responseSong = api.addSongToFavourite(event.id)

                            if (responseSong.id == -1L) {
                                onEvent(HomeUiEvent.SomethingWentWrong)

                                return@launch
                            }
                            db.insertIntoFavourite(list = listOf(responseSong))

                            onEvent(HomeUiEvent.EmitToast("${responseSong.title} added to favourite"))
                        }
                    }

                    is HomeUiEvent.BottomSheetItemClick.RemoveFromFavourite -> {
                        if (!state.isInternetAvailable) {
                            onEvent(HomeUiEvent.EmitToast("Please check your Internet connection"))

                            return
                        }

                        viewModelScope.launch(Dispatchers.IO) {
                            val responseSong = api.removeFromFavourite(event.id)

                            if (!responseSong) {
                                onEvent(HomeUiEvent.SomethingWentWrong)

                                return@launch
                            }

                            db.removeFromFavourite(event.id)

                            onEvent(HomeUiEvent.EmitToast("${event.title} removed from favourite"))
                        }
                    }

                    is HomeUiEvent.BottomSheetItemClick.RemoveFromListenHistory -> {
                        Log.d("data", "RemoveFromListenHistory: ${event.id}")
                    }

                    is HomeUiEvent.BottomSheetItemClick.HideSong -> {
                        Log.d("data", "HideSong: ${event.id}")
                    }

                    is HomeUiEvent.BottomSheetItemClick.PlayAlbum -> {
                        Log.d("data", "PlayAlbum: ${event.id}")
                    }

                    HomeUiEvent.BottomSheetItemClick.PlayArtistMix -> {
                        Log.d("data", "PlayArtistMix")
                    }

                    HomeUiEvent.BottomSheetItemClick.PlayDailyMix -> {
                        Log.d("data", "PlayDailyMix")
                    }

                    is HomeUiEvent.BottomSheetItemClick.AddToLibraryAlbum -> {
                        Log.d("data", "AddToLibraryAlbum: ${event.id}")
                    }

                    is HomeUiEvent.BottomSheetItemClick.AddToPlaylist -> {
                        when (event.type) {
                            HomeLongClickType.ALBUM_PREV -> {
                                viewModelScope.launch(Dispatchers.IO) {
                                    val album = state.data.albumPrev.firstOrNull {
                                        it.id == event.id
                                    } ?: return@launch onEvent(HomeUiEvent.SomethingWentWrong)

                                    _uiEvent.send(
                                        UiEvent.NavigateWithData(
                                            route = Screens.CreatePlaylist.route,
                                            id = album.id,
                                            name = album.name,
                                            longClickType = HomeLongClickType.ALBUM_PREV.name
                                        )
                                    )
                                }
                            }

                            HomeLongClickType.ARTIST_MIX -> {
                                val date =
                                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd:MM:yy"))


                                viewModelScope.launch(Dispatchers.IO) {
                                    _uiEvent.send(
                                        UiEvent.NavigateWithData(
                                            route = Screens.CreatePlaylist.route,
                                            name = "Artist Mix [$date]",
                                            longClickType = HomeLongClickType.ARTIST_MIX.name
                                        )
                                    )
                                }
                            }

                            HomeLongClickType.DAILY_MIX -> {
                                val date =
                                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd:MM:yy"))

                                viewModelScope.launch(Dispatchers.IO) {
                                    _uiEvent.send(
                                        UiEvent.NavigateWithData(
                                            route = Screens.CreatePlaylist.route,
                                            name = "Daily Mix [$date]",
                                            longClickType = HomeLongClickType.DAILY_MIX.name
                                        )
                                    )
                                }
                            }

                            HomeLongClickType.HISTORY_SONG -> {
                                viewModelScope.launch(Dispatchers.IO) {
                                    _uiEvent.send(
                                        UiEvent.NavigateWithData(
                                            route = Screens.EditPlaylist.route,
                                            songType = SongType.HISTORY_SONG,
                                            id = event.id
                                        )
                                    )
                                }
                            }

                            HomeLongClickType.ARTIST_SONG -> {
                                viewModelScope.launch(Dispatchers.IO) {
                                    _uiEvent.send(
                                        UiEvent.NavigateWithData(
                                            route = Screens.EditPlaylist.route,
                                            songType = SongType.ARTIST_SONG,
                                            id = event.id
                                        )
                                    )
                                }
                            }
                        }
                    }

                    is HomeUiEvent.BottomSheetItemClick.DownloadAlbum -> {
                        Log.d("data", "DownloadAlbum: ${event.id}")
                    }

                    HomeUiEvent.BottomSheetItemClick.DownloadArtistMix -> {
                        Log.d("data", "DownloadArtistMix")
                    }

                    HomeUiEvent.BottomSheetItemClick.DownloadDailyMix -> {
                        Log.d("data", "DownloadDailyMix")
                    }
                }
            }
        }
    }
}



















