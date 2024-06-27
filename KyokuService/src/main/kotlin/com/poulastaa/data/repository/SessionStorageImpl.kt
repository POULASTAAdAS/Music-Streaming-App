package com.poulastaa.data.repository

import com.poulastaa.data.dao.session.SessionStorageDao
import com.poulastaa.domain.table.session.SessionStorageTable
import com.poulastaa.plugins.dbQuery
import io.ktor.server.sessions.*

class SessionStorageImpl : SessionStorage {
    override suspend fun write(id: String, value: String) {
        val session = dbQuery {
            SessionStorageDao.find {
                SessionStorageTable.sessionId eq id
            }.singleOrNull()
        }

        if (session != null) dbQuery {
            session.value = value
        }
        else {
            dbQuery {
                SessionStorageDao.new {
                    this.sessionId = id
                    this.value = value
                }
            }
        }
    }


    override suspend fun read(id: String): String {
        return dbQuery {
            SessionStorageDao.find {
                SessionStorageTable.sessionId eq id
            }.singleOrNull()?.value ?: throw NoSuchElementException("Session $id not found")
        }
    }

    override suspend fun invalidate(id: String) {
        dbQuery {
            SessionStorageDao.find {
                SessionStorageTable.sessionId eq id
            }.singleOrNull()?.delete()
        }
    }
}