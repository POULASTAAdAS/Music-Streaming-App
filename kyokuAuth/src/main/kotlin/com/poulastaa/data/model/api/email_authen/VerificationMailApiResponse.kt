package com.poulastaa.data.model.api.email_authen

import kotlinx.serialization.Serializable

@Serializable
data class VerificationMailApiResponse(
    val email: String,
    val user: String,
    val domain: String,
    val status: String,
    val reason: String,
    val disposable: Boolean
)
