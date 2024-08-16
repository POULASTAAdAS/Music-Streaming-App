package com.poulastaa.core.domain

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun storeSignInState(state: ScreenEnum)
    suspend fun readSignInState(): ScreenEnum

    suspend fun storeTokenOrCookie(data: String)
    fun readTokenOrCookie(): Flow<String>

    suspend fun storeRefreshToken(data: String)
    suspend fun readRefreshToken(): String

    suspend fun storeLocalUser(user: User)
    suspend fun readLocalUser(): User

    suspend fun storeSaveScreen(data: String)
    fun readSaveScreen(): Flow<String>

    suspend fun storeLibraryViewType(isGrid: Boolean)
    suspend fun readLibraryViewType(): Boolean

    suspend fun isFevPinned(): Boolean
    suspend fun updateFevPinState(state: Boolean)

    suspend fun logOut()
}