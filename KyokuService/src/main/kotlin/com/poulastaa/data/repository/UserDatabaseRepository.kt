package com.poulastaa.data.repository

import com.poulastaa.data.dao.AlbumDao
import com.poulastaa.data.dao.ArtistDao
import com.poulastaa.data.dao.PlaylistDao
import com.poulastaa.data.dao.SongDao
import com.poulastaa.data.dao.user.EmailAuthUserDao
import com.poulastaa.data.dao.user.GoogleAuthUserDao
import com.poulastaa.data.mappers.toArtistDto
import com.poulastaa.data.mappers.toSongDto
import com.poulastaa.data.mappers.toUserResult
import com.poulastaa.data.model.*
import com.poulastaa.domain.model.PlaylistSongPayload
import com.poulastaa.domain.model.ReqUserPayload
import com.poulastaa.domain.model.UserResult
import com.poulastaa.domain.model.UserType
import com.poulastaa.domain.repository.DatabaseRepository
import com.poulastaa.domain.repository.UserId
import com.poulastaa.domain.repository.UserRepository
import com.poulastaa.domain.table.AlbumTable
import com.poulastaa.domain.table.ArtistTable
import com.poulastaa.domain.table.PlaylistTable
import com.poulastaa.domain.table.SongTable
import com.poulastaa.domain.table.relation.*
import com.poulastaa.domain.table.user.EmailAuthUserTable
import com.poulastaa.domain.table.user.GoogleAuthUserTable
import com.poulastaa.plugins.query
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class UserDatabaseRepository(
    private val database: DatabaseRepository,
) : UserRepository {
    override suspend fun getUserOnPayload(payload: ReqUserPayload): UserResult? = when (payload.userType) {
        UserType.GOOGLE_USER -> {
            query {
                GoogleAuthUserDao.find {
                    GoogleAuthUserTable.id eq payload.id.toLong()
                }.singleOrNull()?.toUserResult()
            }
        }

        UserType.EMAIL_USER -> {
            query {
                EmailAuthUserDao.find {
                    EmailAuthUserTable.email eq payload.id
                }.singleOrNull()?.toUserResult()
            }
        }
    }

    override suspend fun getUserOnEmail(
        email: String,
        userType: UserType,
    ): UserResult? = when (userType) {
        UserType.GOOGLE_USER -> {
            query {
                GoogleAuthUserDao.find {
                    GoogleAuthUserTable.email eq email
                }.singleOrNull()?.toUserResult()
            }
        }

        UserType.EMAIL_USER -> {
            query {
                EmailAuthUserDao.find {
                    EmailAuthUserTable.email eq email
                }.singleOrNull()?.toUserResult()
            }
        }
    }

    override fun createUserPlaylist(
        userId: Long,
        userType: UserType,
        playlistId: Long,
        songIdList: List<Long>,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            query {
                UserPlaylistSongRelationTable.batchInsert(songIdList, ignore = true) { songId ->
                    this[UserPlaylistSongRelationTable.userId] = userId
                    this[UserPlaylistSongRelationTable.playlistId] = playlistId
                    this[UserPlaylistSongRelationTable.songId] = songId
                    this[UserPlaylistSongRelationTable.userType] = userType.name
                }
            }
        }
    }

    override suspend fun updateBDate(
        userId: Long,
        bDate: Long,
        userType: UserType,
    ): Boolean {
        when (userType) {
            UserType.GOOGLE_USER -> {
                query {
                    GoogleAuthUserDao.find {
                        GoogleAuthUserTable.id eq userId
                    }.single().bDate = bDate
                }
            }

            UserType.EMAIL_USER -> {
                query {
                    EmailAuthUserDao.find {
                        EmailAuthUserTable.id eq userId
                    }.single().bDate = bDate
                }
            }
        }

        return true
    }

    override suspend fun storeGenre(
        userId: Long,
        userType: UserType,
        idList: List<Int>,
    ) {
        coroutineScope {
            query {
                UserGenreRelationTable.batchInsert(idList, ignore = true) { genreId ->
                    this[UserGenreRelationTable.userId] = userId
                    this[UserGenreRelationTable.userType] = userType.name
                    this[UserGenreRelationTable.genreId] = genreId
                }
            }
        }
    }

    override suspend fun storeArtist(userId: Long, userType: UserType, idList: List<Long>) {
        coroutineScope {
            query {
                UserArtistRelationTable.batchInsert(idList, ignore = true) { artistId ->
                    this[UserArtistRelationTable.userId] = userId
                    this[UserArtistRelationTable.userType] = userType.name
                    this[UserArtistRelationTable.artistId] = artistId
                }
            }
        }
    }

    override suspend fun getUserData(
        userType: UserType,
        email: String,
    ): Pair<LogInDto, UserId> = coroutineScope {
        val userId = getUserOnEmail(email, userType)?.id ?: return@coroutineScope LogInDto() to -1

        val savedPlaylistDef = async {
            query {
                UserPlaylistSongRelationTable.select {
                    (UserPlaylistSongRelationTable.userId eq userId) and (UserPlaylistSongRelationTable.userType eq userType.name)
                }.map {
                    PlaylistSongPayload(
                        playlistId = it[UserPlaylistSongRelationTable.playlistId],
                        songId = it[UserPlaylistSongRelationTable.songId],
                    )
                }
            }.groupBy { it.playlistId }.map {
                async {
                    query {
                        PlaylistDao.find {
                            PlaylistTable.id eq it.key
                        }.first()
                    } to query {
                        SongDao.find {
                            SongTable.id inList it.value.map { it.songId }
                        }.toList()
                    }
                }
            }.awaitAll().map {
                PlaylistDto(
                    id = it.first.id.value,
                    name = it.first.name,
                    listOfSong = it.second.map { songDao ->
                        async {
                            database.getArtistOnSongId(songDao.id.value) to songDao
                        }
                    }.awaitAll().map { pair ->
                        pair.second.toSongDto(artist = pair.first.joinToString())
                    }
                )
            }
        }
        val savedAlbumDef = async {
            query {
                UserAlbumRelationTable.select {
                    (UserAlbumRelationTable.userId eq userId) and (UserAlbumRelationTable.userType eq userType.name)
                }.map {
                    it[UserAlbumRelationTable.albumId]
                }.let { albumIdList ->
                    query {
                        AlbumDao.find {
                            AlbumTable.id inList albumIdList
                        }.map { album ->
                            async {
                                query {
                                    SongAlbumRelationTable.select {
                                        SongAlbumRelationTable.albumId eq album.id.value
                                    }.single().let {
                                        it[SongAlbumRelationTable.songId]
                                    }.let { songId ->
                                        SongDao.find {
                                            SongTable.id eq songId
                                        }.single().constructCoverImage()
                                    }
                                } to album
                            }
                        }.awaitAll()
                    }
                }.map {
                    AlbumDto(
                        id = it.second.id.value,
                        name = it.second.name,
                        coverImage = it.first
                    )
                }
            }
        }
        val followArtistDef = async {
            query {
                UserArtistRelationTable.select {
                    UserArtistRelationTable.userId eq userId and (UserArtistRelationTable.userType eq userType.name)
                }.map {
                    it[UserArtistRelationTable.artistId]
                }.let {
                    query {
                        ArtistDao.find {
                            ArtistTable.id inList it
                        }
                    }
                }.map {
                    ArtistDto(
                        id = it.id.value,
                        name = it.name,
                        coverImage = it.constructProfilePic()
                    )
                }
            }
        }
        val favouriteSongDef = async {
            query {
                UserFavouriteRelationTable.select {
                    UserFavouriteRelationTable.userId eq userId and (UserFavouriteRelationTable.userType eq userType.name)
                }.map {
                    it[UserFavouriteRelationTable.songId]
                }.let {
                    SongDao.find {
                        SongTable.id inList it
                    }.map { songDao ->
                        async {
                            database.getArtistOnSongId(songDao.id.value) to songDao
                        }
                    }.awaitAll().map {
                        it.second.toSongDto(it.first.joinToString { artist -> artist.name })
                    }
                }
            }
        }

        LogInDto(
            savedPlaylist = savedPlaylistDef.await(),
            savedAlbum = savedAlbumDef.await(),
            savedArtist = followArtistDef.await(),
            favouriteSong = favouriteSongDef.await()
        ) to userId
    }

    override suspend fun addToFavourite(
        id: Long,
        userId: Long,
        userType: UserType,
    ): SongDto = coroutineScope {
        val insert = async {
            if (
                query {
                    SongDao.find {
                        SongTable.id eq id
                    }.singleOrNull() == null
                }
            ) return@async SongDto()

            query {
                UserFavouriteRelationTable.insertIgnore {
                    it[this.songId] = id
                    it[this.userId] = userId
                    it[this.userType] = userType.name
                }
            }
        }

        val song = async {
            val resultRow = query {
                SongArtistRelationTable.select {
                    SongArtistRelationTable.songId eq id
                }
            }

            val artistName = if (query { resultRow.empty() }) ""
            else query {
                resultRow.map {
                    it[SongArtistRelationTable.artistId]
                }.let {
                    ArtistDao.find {
                        ArtistTable.id inList it
                    }.map {
                        it.name
                    }
                }
            }.joinToString()

            query {
                SongDao.find {
                    SongTable.id eq id
                }.singleOrNull()?.toSongDto(artistName)
            } ?: SongDto()
        }

        insert.await()
        song.await()
    }

    override suspend fun removeFromFavourite(
        id: Long,
        email: String,
        userType: UserType,
    ): Boolean {
        val user = getUserOnEmail(email = email, userType = userType) ?: return false

        return query {
            UserFavouriteRelationTable.deleteWhere {
                this.userId eq user.id and
                        (this.userType eq userType.name) and
                        (this.songId eq id)
            }

            true
        }
    }

    override suspend fun addArtist(
        artistId: Long,
        email: String,
        userType: UserType,
    ): ArtistDto {
        val artist = query {
            ArtistDao.find {
                ArtistTable.id eq artistId
            }.singleOrNull()
        } ?: return ArtistDto()

        query {
            UserArtistRelationTable.insertIgnore {
                it[this.artistId] = artistId
                it[this.userType] = userType.name
                it[this.userId] = userId
            }
        }

        return artist.toArtistDto()
    }

    override suspend fun removeArtist(
        id: Long,
        email: String,
        userType: UserType,
    ): Boolean = query {
        UserArtistRelationTable.deleteWhere {
            this.userId eq userId and
                    (this.userType eq userType.name) and
                    (this.artistId eq artistId)
        }

        true
    }
}