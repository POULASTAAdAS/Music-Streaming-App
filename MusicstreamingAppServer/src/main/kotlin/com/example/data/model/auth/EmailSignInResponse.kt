package com.example.data.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class EmailSignInResponse(
    val status: UserCreationStatus,
    val userName: String = "",
    val token: String = "",
    val profilePic: String = ""
)
