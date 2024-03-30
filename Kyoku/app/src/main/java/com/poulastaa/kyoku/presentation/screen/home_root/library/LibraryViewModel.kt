package com.poulastaa.kyoku.presentation.screen.home_root.library

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poulastaa.kyoku.connectivity.NetworkObserver
import com.poulastaa.kyoku.data.model.screens.auth.UiEvent
import com.poulastaa.kyoku.data.model.screens.common.ItemsType
import com.poulastaa.kyoku.data.model.screens.common.UiAlbumPrev
import com.poulastaa.kyoku.data.model.screens.common.UiPlaylistPrev
import com.poulastaa.kyoku.data.model.screens.library.LibraryUiEvent
import com.poulastaa.kyoku.data.model.screens.library.LibraryUiState
import com.poulastaa.kyoku.data.model.screens.library.PinnedDataType
import com.poulastaa.kyoku.data.repository.DatabaseRepositoryImpl
import com.poulastaa.kyoku.domain.repository.DataStoreOperation
import com.poulastaa.kyoku.domain.repository.ServiceRepository
import com.poulastaa.kyoku.navigation.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Random
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
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

    var state by mutableStateOf(LibraryUiState())
        private set

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(800)
            state = state.copy(
                isInternetError = network.value != NetworkObserver.STATUS.AVAILABLE,
                isLoading = false
            )
        }

        if (!state.data.all.isFavourite &&
            state.data.all.playlist.isEmpty() &&
            state.data.all.artist.isEmpty()
        ) {
            // read sort type
            viewModelScope.launch(Dispatchers.IO) {
                ds.readLibraryDataSortType().collect {
                    state = state.copy(
                        isGrid = it
                    )
                }
            }

            // read All Data
            viewModelScope.launch(Dispatchers.IO) {
                val allPlaylist = async {
                    db.readPlaylistPreview().collect {
                        state = state.copy(
                            data = state.data.copy(
                                all = state.data.all.copy(
                                    playlist = it.groupBy { result -> result.name }.map { entry ->
                                        UiPlaylistPrev(
                                            id = entry.value[0].id,
                                            name = entry.key,
                                            listOfUrl = entry.value.map { url ->
                                                url.coverImage
                                            }.shuffled(Random()).take(4)
                                        )
                                    }
                                )
                            )
                        )
                    }
                }

                val allAlbum = async {
                    db.readAllAlbum().collect {
                        state = state.copy(
                            data = state.data.copy(
                                all = state.data.all.copy(
                                    album = it.groupBy { album ->
                                        album.id
                                    }.map { entry ->
                                        UiAlbumPrev(
                                            id = entry.key,
                                            name = entry.value[0].name,
                                            coverImage = entry.value[0].coverImage
                                        )
                                    }
                                )
                            )
                        )
                    }
                }

                val allArtist = async {
                    db.readAllArtist().collect {
                        state = state.copy(
                            data = state.data.copy(
                                all = state.data.all.copy(
                                    artist = it
                                )
                            )
                        )
                    }
                }

                val allFavourite = async {
                    state = state.copy(
                        data = state.data.copy(
                            all = state.data.all.copy(
                                isFavourite = db.readFavouritePrev() > 0
                            )
                        )
                    )
                }

                allPlaylist.await()
                allArtist.await()
                allAlbum.await()
                allFavourite.await()
            }

            // read Pinned Data
            viewModelScope.launch(Dispatchers.IO) {
                val playlist = async {
                    db.readPinnedPlaylist().collect {
                        state = state.copy(
                            data = state.data.copy(
                                pinned = state.data.pinned.copy(
                                    playlist = it.groupBy { result -> result.name }
                                        .map { map ->
                                            UiPlaylistPrev(
                                                id = map.value[0].id,
                                                name = map.key,
                                                listOfUrl = map.value.map { playlist ->
                                                    playlist.coverImage
                                                }.shuffled(Random()).take(4)
                                            )
                                        }
                                )
                            )
                        )
                    }
                }

                val album = async {
                    db.readPinnedAlbum().collect {
                        state = state.copy(
                            data = state.data.copy(
                                pinned = state.data.pinned.copy(
                                    album = it.groupBy { album ->
                                        album.name
                                    }.map { entry ->
                                        UiAlbumPrev(
                                            id = entry.value[0].id,
                                            name = entry.key,
                                            coverImage = entry.value[0].coverImage
                                        )
                                    }
                                )
                            )
                        )
                    }
                }

                val isFavourite = async {
                    ds.readFavouritePinnedState().collect {
                        state = state.copy(
                            data = state.data.copy(
                                pinned = state.data.pinned.copy(
                                    isFavourite = it
                                )
                            )
                        )
                    }
                }

                val artist = async {
                    db.readPinnedArtist().collect {
                        state = state.copy(
                            data = state.data.copy(
                                pinned = state.data.pinned.copy(
                                    artist = it
                                )
                            )
                        )
                    }
                }

                playlist.await()
                album.await()
                isFavourite.await()
                artist.await()
            }
        }
    }


    fun onEvent(event: LibraryUiEvent) {
        when (event) {
            is LibraryUiEvent.EmitToast -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _uiEvent.send(UiEvent.ShowToast(event.message))
                }
            }

            LibraryUiEvent.SomethingWentWrong -> {
                onEvent(LibraryUiEvent.EmitToast("Opp's Something went wrong."))
            }

            LibraryUiEvent.HideBottomSheet -> {
                if (state.isBottomSheetOpen) {
                    state = state.copy(
                        isBottomSheetOpen = false
                    )
                }
            }

            is LibraryUiEvent.FilterChipClick -> {
                when (event) {
                    LibraryUiEvent.FilterChipClick.PlaylistType -> {
                        state = state.copy(
                            filterChip = state.filterChip.copy(
                                isPlaylist = !state.filterChip.isPlaylist
                            )
                        )
                    }

                    LibraryUiEvent.FilterChipClick.ArtistType -> {
                        state = state.copy(
                            filterChip = state.filterChip.copy(
                                isArtist = !state.filterChip.isArtist
                            )
                        )
                    }

                    LibraryUiEvent.FilterChipClick.AlbumType -> {
                        state = state.copy(
                            filterChip = state.filterChip.copy(
                                isAlbum = !state.filterChip.isAlbum
                            )
                        )
                    }
                }
            }

            is LibraryUiEvent.ItemClick -> {
                when (event) {
                    LibraryUiEvent.ItemClick.SortTypeClick -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            ds.storeLibraryDataSortType(
                                sortType = !state.isGrid
                            )
                        }
                    }

                    LibraryUiEvent.ItemClick.AddAlbumClick -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            _uiEvent.send(UiEvent.Navigate(Screens.AddAlbum.route))
                        }
                    }

                    LibraryUiEvent.ItemClick.AddArtistClick -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            _uiEvent.send(UiEvent.Navigate(Screens.AddArtist.route))
                        }
                    }

                    LibraryUiEvent.ItemClick.CreatePlaylistClick -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            _uiEvent.send(UiEvent.Navigate(Screens.CreatePlaylist.route))
                        }
                    }


                    LibraryUiEvent.ItemClick.FavouriteLongClick -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            async {
                                state = state.copy(
                                    pinnedData = state.pinnedData.copy(
                                        name = PinnedDataType.FAVOURITE.title,
                                        type = PinnedDataType.FAVOURITE,
                                        isPinned = ds.readFavouritePinnedState().first()
                                    )
                                )
                            }.await()

                            state = state.copy(
                                isBottomSheetOpen = true
                            )
                        }
                    }

                    LibraryUiEvent.ItemClick.FavouriteClick -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            _uiEvent.send(
                                UiEvent.NavigateWithData(
                                    type = ItemsType.FAVOURITE,
                                    route = Screens.SongView.route
                                )
                            )
                        }
                    }

                    is LibraryUiEvent.ItemClick.PlaylistLongClick -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            async {
                                state = state.copy(
                                    pinnedData = state.pinnedData.copy(
                                        name = event.name,
                                        type = PinnedDataType.PLAYLIST,
                                        isPinned = db.checkIfPlaylistIdPinned(name = event.name)
                                    )
                                )
                            }.await()

                            state = state.copy(
                                isBottomSheetOpen = true
                            )
                        }
                    }

                    is LibraryUiEvent.ItemClick.PlaylistClick -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            _uiEvent.send(
                                UiEvent.NavigateWithData(
                                    route = Screens.SongView.route,
                                    type = ItemsType.PLAYLIST,
                                    id = event.id
                                )
                            )
                        }
                    }

                    is LibraryUiEvent.ItemClick.AlbumLongClick -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            async {
                                state = state.copy(
                                    pinnedData = state.pinnedData.copy(
                                        name = event.name,
                                        type = PinnedDataType.ALBUM,
                                        isPinned = db.checkIfAlbumPinned(name = event.name)
                                    )
                                )
                            }.await()

                            state = state.copy(
                                isBottomSheetOpen = true
                            )
                        }
                    }

                    is LibraryUiEvent.ItemClick.AlbumClick -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            _uiEvent.send(
                                UiEvent.NavigateWithData(
                                    route = Screens.SongView.route,
                                    type = ItemsType.ALBUM,
                                    name = event.name
                                )
                            )
                        }
                    }


                    is LibraryUiEvent.ItemClick.ArtistLongClick -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            async {
                                state = state.copy(
                                    pinnedData = state.pinnedData.copy(
                                        name = event.name,
                                        type = PinnedDataType.ARTIST,
                                        isPinned = db.checkIfArtistPinned(name = event.name)
                                    )
                                )
                            }.await()

                            state = state.copy(
                                isBottomSheetOpen = true
                            )
                        }
                    }

                    is LibraryUiEvent.ItemClick.ArtistClick -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            _uiEvent.send(
                                UiEvent.NavigateWithData(
                                    route = Screens.SongView.route,
                                    type = ItemsType.ARTIST,
                                    id = event.id,
                                    name = event.name
                                )
                            )
                        }
                    }

                    is LibraryUiEvent.BottomSheetItemClick -> {
                        when (event) {
                            is LibraryUiEvent.BottomSheetItemClick.AddClick -> {
                                viewModelScope.launch(Dispatchers.IO) {
                                    state = state.copy(
                                        isBottomSheetOpen = false
                                    )

                                    val result = db.addToPinnedTable(
                                        type = event.type,
                                        name = event.name,
                                        ds = ds
                                    )

                                    if (!result)
                                        onEvent(LibraryUiEvent.SomethingWentWrong)
                                }
                            }

                            LibraryUiEvent.BottomSheetItemClick.RemoveClick -> {
                                viewModelScope.launch(Dispatchers.IO) {
                                    if (
                                        state.pinnedData.name.isNotEmpty() &&
                                        state.pinnedData.type != PinnedDataType.NON
                                    ) {
                                        val result = db.removeFromPinnedTable(
                                            type = state.pinnedData.type,
                                            name = state.pinnedData.name,
                                            ds = ds
                                        )

                                        if (!result) onEvent(LibraryUiEvent.SomethingWentWrong)
                                    } else {
                                        onEvent(LibraryUiEvent.SomethingWentWrong)
                                    }

                                    state = state.copy(
                                        isBottomSheetOpen = false
                                    )
                                }
                            }

                            LibraryUiEvent.BottomSheetItemClick.DeleteClick -> {
                                viewModelScope.launch(Dispatchers.IO) {
                                    if (state.pinnedData.name.isEmpty()) {
                                        onEvent(LibraryUiEvent.SomethingWentWrong)

                                        state = state.copy(
                                            isBottomSheetOpen = false
                                        )

                                        return@launch
                                    }

                                    if (!state.isDialogOpen) state =
                                        state.copy(isDialogOpen = true) // open dialog
                                }
                            }
                        }
                    }

                    is LibraryUiEvent.DeleteDialogClick -> {
                        when (event) {
                            LibraryUiEvent.DeleteDialogClick.DeleteYes -> {
                                viewModelScope.launch(Dispatchers.IO) {
                                    state = state.copy(
                                        isDialogOpen = false,
                                        isBottomSheetOpen = false
                                    )

                                    if (state.pinnedData.name.isEmpty()) {
                                        onEvent(LibraryUiEvent.SomethingWentWrong)

                                        return@launch
                                    }

                                    val result = db.deletePlaylistArtistAlbumFavouriteEntry(
                                        type = state.pinnedData.type,
                                        name = state.pinnedData.name,
                                        ds = ds
                                    )

                                    if (state.pinnedData.type == PinnedDataType.FAVOURITE) {
                                        state = state.copy(
                                            data = state.data.copy(
                                                pinned = state.data.pinned.copy(
                                                    isFavourite = false
                                                )
                                            )
                                        )

                                        db.removeFromPinnedTable(PinnedDataType.FAVOURITE, "", ds)
                                    }

                                    // todo make api call
                                    // todo if internet is not available store in internal database

                                    if (!result) onEvent(LibraryUiEvent.SomethingWentWrong)
                                }
                            }

                            LibraryUiEvent.DeleteDialogClick.DeleteNo -> {
                                state = state.copy(
                                    isDialogOpen = false,
                                    isBottomSheetOpen = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}