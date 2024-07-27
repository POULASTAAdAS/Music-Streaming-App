package com.poulastaa.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PopularSongMixPrevEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val coverImage: String,
)