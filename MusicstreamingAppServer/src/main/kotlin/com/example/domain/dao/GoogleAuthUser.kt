package com.example.domain.dao

import com.example.data.model.database_table.GoogleAuthUserTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class GoogleAuthUser(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<GoogleAuthUser>(GoogleAuthUserTable)

    var userName by GoogleAuthUserTable.userName
    var email by GoogleAuthUserTable.email
    var sub by GoogleAuthUserTable.sub
    var profilePicUrl by GoogleAuthUserTable.profilePic
}