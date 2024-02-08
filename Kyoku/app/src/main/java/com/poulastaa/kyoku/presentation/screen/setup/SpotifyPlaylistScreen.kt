package com.poulastaa.kyoku.presentation.screen.setup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SpotifyPlaylistScreen(
    viewModel: SetUpViewModel = hiltViewModel()
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "SpotifyPlaylistScreen", fontSize = 50.sp, modifier = Modifier.clickable {
            viewModel.logOut()
        })

        Text(text = "Read", fontSize = 50.sp, modifier = Modifier.clickable {
            viewModel.showData()
        })
    }
}