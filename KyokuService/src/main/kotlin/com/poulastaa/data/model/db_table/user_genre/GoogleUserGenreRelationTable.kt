package com.poulastaa.data.model.db_table.user_genre

import com.poulastaa.data.model.db_table.GenreTable
import com.poulastaa.data.model.db_table.user.GoogleAuthUserTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object GoogleUserGenreRelationTable: LongIdTable() {
    val genreId = integer("genreId").references(GenreTable.id)
    val userId = long("userId").references(GoogleAuthUserTable.id, onDelete = ReferenceOption.CASCADE)
}