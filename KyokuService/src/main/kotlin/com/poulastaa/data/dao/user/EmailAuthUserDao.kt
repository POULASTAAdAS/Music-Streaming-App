package com.poulastaa.data.dao.user

import com.poulastaa.domain.table.user.EmailAuthUserTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class EmailAuthUserDao(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<EmailAuthUserDao>(EmailAuthUserTable)

    var userName by EmailAuthUserTable.userName
    var email by EmailAuthUserTable.email
    var password by EmailAuthUserTable.password
    var emailVerified by EmailAuthUserTable.emailVerified
    var emailVerificationDone by EmailAuthUserTable.emailVerificationDone
    var profilePic by EmailAuthUserTable.profilePic
    var refreshToken by EmailAuthUserTable.refreshToken
    var bDate by EmailAuthUserTable.bDate
    var countryId by EmailAuthUserTable.countryId
}