package com.poulastaa.domain.dao.user

import com.poulastaa.data.model.db_table.user.PasskeyAuthUserTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class PasskeyAuthUser(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<PasskeyAuthUser>(PasskeyAuthUserTable)

    var userId by PasskeyAuthUserTable.userId
    var email by PasskeyAuthUserTable.email
    var displayName by PasskeyAuthUserTable.userName
    var profilePic by PasskeyAuthUserTable.profilePic
    var bDate by PasskeyAuthUserTable.bDate
}