package com.poulastaa.data.model.db_table

import com.poulastaa.utils.Constants.DEFAULT_PROFILE_PIC
import com.poulastaa.utils.Constants.PROFILE_PIC_ROOT_DIR
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

object GoogleAuthUserTable : LongIdTable() {
    val userName: Column<String> = text("username")
    val email: Column<String> = varchar("email", 320).uniqueIndex()
    val sub: Column<String> = varchar("sub", 30).uniqueIndex()
    val profilePic: Column<String> = varchar("profilePic", 200)
        .default(DEFAULT_PROFILE_PIC)
    val bDate: Column<Long?> = long("bDate").nullable()
}