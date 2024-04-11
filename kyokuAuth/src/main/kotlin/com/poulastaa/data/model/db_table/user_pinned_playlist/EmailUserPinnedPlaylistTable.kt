package com.poulastaa.data.model.db_table.user_pinned_playlist

import com.poulastaa.data.model.db_table.PlaylistTable
import com.poulastaa.data.model.db_table.user.EmailAuthUserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object EmailUserPinnedPlaylistTable : Table() {
    val userId = long("userId").references(EmailAuthUserTable.id, onDelete = ReferenceOption.CASCADE)
    val playlistId = long("playlistId").references(PlaylistTable.id , onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(userId, playlistId)
}