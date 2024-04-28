package com.poulastaa.kyoku.data.model.database.table.prev

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PlayingQueueTable")
data class PlayingQueueTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val songId: Long = 0,
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val coverImage: String = "",
    val masterPlaylistUrl: String = "",
    val totalTime: String = "",
    val year: String = "",
    val colorOne: String = "",
    val colorTwo: String = ""
)