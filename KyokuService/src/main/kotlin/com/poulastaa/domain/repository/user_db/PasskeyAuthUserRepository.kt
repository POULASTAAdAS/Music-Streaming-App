package com.poulastaa.domain.repository.user_db

import com.poulastaa.data.model.setup.set_b_date.SetBDateResponseStatus

interface PasskeyAuthUserRepository {
    suspend fun updateBDate(date: Long, email: String): SetBDateResponseStatus
}