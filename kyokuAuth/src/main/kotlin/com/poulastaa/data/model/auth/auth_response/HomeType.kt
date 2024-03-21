package com.poulastaa.data.model.auth.auth_response

import kotlinx.serialization.Serializable

@Serializable
enum class HomeType {
    NEW_USER_REQ, // signIn
    ALREADY_USER_REQ, // signUp
    DAILY_REFRESH_REQ
}