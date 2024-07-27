package com.poulastaa.play.presentation.root_drawer.library

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.poulastaa.core.domain.DataStoreRepository
import com.poulastaa.core.domain.library.LibraryRepository
import com.poulastaa.play.presentation.root_drawer.library.model.LibraryViewType
import com.poulastaa.play.presentation.root_drawer.toUiPlaylist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val ds: DataStoreRepository,
    private val repo: LibraryRepository,
) : ViewModel() {
    var state by mutableStateOf(LibraryUiState())
        private set

    init {
        readAuthHeader()
        readLibraryViewType()
        populate()
    }

    private val _uiEvent = Channel<LibraryUiAction>()
    val uiEvent = _uiEvent.receiveAsFlow()


    fun onEvent(event: LibraryUiEvent) {
        when (event) {
            LibraryUiEvent.OnSearchClick -> {

            }

            is LibraryUiEvent.ToggleFilterType -> {
                state = state.copy(
                    filterType = event.type
                )
            }

            LibraryUiEvent.ToggleView -> {
                state = state.copy(
                    viewType = if (LibraryViewType.LIST == state.viewType) LibraryViewType.GRID else LibraryViewType.LIST
                )
            }
        }
    }


    private fun readAuthHeader() {
        viewModelScope.launch {
            ds.readTokenOrCookie().collectLatest {
                state = state.copy(
                    header = it
                )
            }
        }
    }

    private fun readLibraryViewType() {
        viewModelScope.launch {
            val type = if (ds.readLibraryViewType()) LibraryViewType.GRID else LibraryViewType.LIST

            withContext(Dispatchers.Main) {
                state = state.copy(
                    viewType = type,
                    viewTypeReading = false
                )
            }
        }
    }

    private fun populate() {
        readPlaylist()
        readArtist()
    }

    private fun readPlaylist() {
        viewModelScope.launch {
            repo.getPlaylist().collectLatest { list ->
                state = state.copy(
                    data = state.data.copy(
                        playlist = list.map {
                            it.toUiPlaylist()
                        }
                    )
                )
            }
        }
    }

    private fun readArtist() {
        viewModelScope.launch {
            repo.getArtist().collectLatest {
                state = state.copy(
                    data = state.data.copy(
                        artist = it.map { artist ->
                            artist.toUiArtist()
                        }
                    ),
                    isDataLoading = false
                )
            }
        }
    }
}