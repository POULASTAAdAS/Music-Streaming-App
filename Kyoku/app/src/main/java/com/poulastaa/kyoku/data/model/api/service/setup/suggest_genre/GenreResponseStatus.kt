package com.poulastaa.kyoku.data.model.api.service.setup.suggest_genre

import kotlinx.serialization.Serializable

@Serializable
enum class GenreResponseStatus {
    SUCCESS,
    FAILURE
}