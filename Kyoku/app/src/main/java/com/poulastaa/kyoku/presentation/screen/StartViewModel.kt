package com.poulastaa.kyoku.presentation.screen

import androidx.lifecycle.ViewModel
import com.poulastaa.kyoku.domain.repository.DataStoreOperation
import com.poulastaa.kyoku.navigation.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val dataStore: DataStoreOperation,
) : ViewModel() {
    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination get() = _startDestination.asStateFlow()

    private val _keepSplashOn = MutableStateFlow(true)
    val keepSplashOn get() = _keepSplashOn.asStateFlow()

    init { // read sign in state
        _startDestination.value = Screens.SuggestGenre.route

//        viewModelScope.launch {
//            dataStore.readSignedInState().collect {
//                when (it) {
//                    SignInStatus.AUTH.name -> _startDestination.value = Screens.Auth.route
//                    SignInStatus.NEW_USER.name ->_startDestination.value = Screens.GetSpotifyPlaylist.route
//                    SignInStatus.B_DATE_SET.name -> _startDestination.value = Screens.SetBirthDate.route
//                    SignInStatus.OLD_USER.name  -> _startDestination.value = Screens.Home.route
//                    SignInStatus.GENRE_SET.name  -> _startDestination.value = Screens.SuggestGenre.route
//                    SignInStatus.ARTIST_SET.name  -> _startDestination.value = Screens.SelectArtist.route
//                }

            _keepSplashOn.value = false
//        }
//        }
    }
}