package com.poulastaa.data.model.db_table.user_listen_history

import com.poulastaa.data.model.db_table.song.SongTable
import com.poulastaa.data.model.db_table.user.EmailAuthUserTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object EmailUserListenHistoryTable : Table() {
    val userId = long("userId").references(EmailAuthUserTable.id)
    val songId = long("songId").references(SongTable.id)
    val date = datetime("date").defaultExpression(CurrentDateTime)

    val repeat = integer("repeat").default(0)

    override val primaryKey = PrimaryKey(userId, songId, date)
}