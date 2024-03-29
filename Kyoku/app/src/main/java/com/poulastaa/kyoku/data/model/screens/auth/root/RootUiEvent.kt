package com.poulastaa.kyoku.data.model.screens.auth.root

import android.app.Activity

sealed class RootUiEvent {
    data class OnPasskeyEmailEnter(val email: String) : RootUiEvent()
    data class OnAutoFillPasskeyEmail(val email: String) : RootUiEvent()

    data class OnPasskeyAuthClick(val activity: Activity) : RootUiEvent()
    data object OnGoogleAuthClick : RootUiEvent()
    data object OnEmailAuthClick : RootUiEvent()

    data class SendGoogleAuthApiRequest(
        val token: String,
        val activity: Activity
    ) : RootUiEvent()

    data object NoGoogleAccountFound : RootUiEvent()
    data object OnAuthCanceled : RootUiEvent()

    data object SomeErrorOccurredOnAuth : RootUiEvent()
}