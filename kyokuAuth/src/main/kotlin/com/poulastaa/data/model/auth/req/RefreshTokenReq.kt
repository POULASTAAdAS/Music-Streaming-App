package com.poulastaa.data.model.auth.req

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenReq(
    val oldRefreshToken: String
)
