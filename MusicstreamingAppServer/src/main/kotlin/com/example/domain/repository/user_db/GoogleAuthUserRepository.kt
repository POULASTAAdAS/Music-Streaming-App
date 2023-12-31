package com.example.domain.repository.user_db

import com.example.data.model.auth.GoogleSignInResponse

interface GoogleAuthUserRepository {
    suspend fun createUser(
        userName: String,
        email: String,
        sub: String,
        pictureUrl: String
    ): GoogleSignInResponse
}