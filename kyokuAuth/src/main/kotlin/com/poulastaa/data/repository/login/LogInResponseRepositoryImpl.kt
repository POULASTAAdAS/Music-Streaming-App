package com.poulastaa.data.repository.login

import com.poulastaa.data.model.UserType
import com.poulastaa.data.model.auth.auth_response.*
import com.poulastaa.data.model.db_table.AlbumTable
import com.poulastaa.data.model.db_table.ArtistTable
import com.poulastaa.data.model.db_table.DbResponseArtistPreview
import com.poulastaa.data.model.db_table.DbResponseArtistPreview.Companion.toListOfSongPreview
import com.poulastaa.data.model.db_table.PlaylistTable
import com.poulastaa.data.model.db_table.song.SongAlbumArtistRelationTable
import com.poulastaa.data.model.db_table.song.SongArtistRelationTable
import com.poulastaa.data.model.db_table.song.SongTable
import com.poulastaa.data.model.db_table.user_album.EmailUserAlbumRelation
import com.poulastaa.data.model.db_table.user_album.GoogleUserAlbumRelation
import com.poulastaa.data.model.db_table.user_album.PasskeyUserAlbumRelation
import com.poulastaa.data.model.db_table.user_artist.EmailUserArtistRelationTable
import com.poulastaa.data.model.db_table.user_artist.GoogleUserArtistRelationTable
import com.poulastaa.data.model.db_table.user_artist.PasskeyUserArtistRelationTable
import com.poulastaa.data.model.db_table.user_fev.EmailUserFavouriteTable
import com.poulastaa.data.model.db_table.user_fev.GoogleUserFavouriteTable
import com.poulastaa.data.model.db_table.user_fev.PasskeyUserFavouriteTable
import com.poulastaa.data.model.db_table.user_listen_history.EmailUserListenHistoryTable
import com.poulastaa.data.model.db_table.user_listen_history.GoogleUserListenHistoryTable
import com.poulastaa.data.model.db_table.user_listen_history.PasskeyUserListenHistoryTable
import com.poulastaa.data.model.db_table.user_playlist.EmailUserPlaylistTable
import com.poulastaa.data.model.db_table.user_playlist.GoogleUserPlaylistTable
import com.poulastaa.data.model.db_table.user_playlist.PasskeyUserPlaylistTable
import com.poulastaa.domain.dao.Album
import com.poulastaa.domain.dao.song.Song
import com.poulastaa.domain.dao.song.SongArtistRelation
import com.poulastaa.domain.dao.user_artist.EmailUserArtistRelation
import com.poulastaa.domain.dao.user_artist.GoogleUserArtistRelation
import com.poulastaa.domain.dao.user_artist.PasskeyUserArtistRelation
import com.poulastaa.domain.repository.login.LogInResponseRepository
import com.poulastaa.plugins.dbQuery
import com.poulastaa.utils.constructCoverPhotoUrl
import com.poulastaa.utils.toPlaylistResult
import com.poulastaa.utils.toResponseSong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Random

class LogInResponseRepositoryImpl : LogInResponseRepository {
    override suspend fun getFevArtistMix(
        userId: Long,
        userType: UserType
    ): List<FevArtistsMixPreview> = when (userType) {
        UserType.GOOGLE_USER -> {
            dbQuery {
                GoogleUserArtistRelation.find {
                    GoogleUserArtistRelationTable.userId eq userId
                }.map {
                    it.artistId
                }.getListOfFevArtistMixPreview()
            }
        }

        UserType.EMAIL_USER -> {
            dbQuery {
                EmailUserArtistRelation.find {
                    EmailUserArtistRelationTable.userId eq userId
                }.map {
                    it.artistId
                }.getListOfFevArtistMixPreview()
            }
        }

        UserType.PASSKEY_USER -> {
            dbQuery {
                PasskeyUserArtistRelation.find {
                    PasskeyUserArtistRelationTable.userId eq userId
                }.map {
                    it.artistId
                }.getListOfFevArtistMixPreview()
            }
        }
    }

    override suspend fun getAlbumPrev(userId: Long, userType: UserType): ResponseAlbumPreview {
        // get users fev albums max 2
        val userFevAlbumIdList = getFevAlbumIdList(userType, userId)

        // get most popular albums remove duplicate limit 7
        // make a single list take 5
        val albumIdList = (userFevAlbumIdList + userFevAlbumIdList.getMostPopularAlbumsIdList()).take(5)

        return ResponseAlbumPreview(
            listOfPreviewAlbum = albumIdList.getAlbumOnAlbumIdList()
        )
    }

    override suspend fun getArtistPrev(userId: Long, userType: UserType): List<ResponseArtistsPreview> =
        withContext(Dispatchers.IO) {
            async { getFevArtistIdList(userType, userId) }.await()
                .getResponseArtistPreviewOnArtistIdList()
        }

    override suspend fun getDailyMixPrev(userId: Long, userType: UserType): DailyMixPreview {
        val historySongIdList = try { // get artistId from history
            getHistorySongIdList(userType, userId)
        } catch (e: Exception) {
            return DailyMixPreview()
        }
        val songsByTheArtistUnSorted = getPreviewSongsByTheArtists(historySongIdList)

        return DailyMixPreview(
            listOfSongs = songsByTheArtistUnSorted.flatMap {
                it.value.take(10)
            }.distinctBy {
                it.id
            }.shuffled(Random()).take(4)
        )
    }

    // last played
    override suspend fun getHistoryPrev(userId: Long, userType: UserType): List<SongPreview> = dbQuery {
        when (userType) {
            UserType.GOOGLE_USER -> {
                GoogleUserListenHistoryTable
                    .slice(
                        GoogleUserListenHistoryTable.songId
                    ).select {
                        GoogleUserListenHistoryTable.userId eq userId
                    }
                    .orderBy(GoogleUserListenHistoryTable.date, SortOrder.DESC)
                    .limit(7)
                    .map {
                        it[GoogleUserListenHistoryTable.songId]
                    }
            }

            UserType.EMAIL_USER -> {
                EmailUserListenHistoryTable
                    .slice(
                        EmailUserListenHistoryTable.songId
                    ).select {
                        EmailUserListenHistoryTable.userId eq userId
                    }
                    .orderBy(EmailUserListenHistoryTable.date, SortOrder.DESC)
                    .limit(7)
                    .map {
                        it[EmailUserListenHistoryTable.songId]
                    }
            }

            UserType.PASSKEY_USER -> {
                PasskeyUserListenHistoryTable
                    .slice(
                        PasskeyUserListenHistoryTable.songId
                    ).select {
                        PasskeyUserListenHistoryTable.userId eq userId
                    }
                    .orderBy(PasskeyUserListenHistoryTable.date, SortOrder.DESC)
                    .limit(7)
                    .map {
                        it[PasskeyUserListenHistoryTable.songId]
                    }
            }
        }.let {
            Song.find {
                SongTable.id inList it
            }.map {
                SongPreview(
                    id = it.id.value.toString(),
                    title = it.title,
                    coverImage = it.coverImage.constructCoverPhotoUrl(),
                    artist = it.artist,
                    album = it.album
                )
            }
        }
    }

    override suspend fun getAlbums(userId: Long, userType: UserType): List<ResponseAlbum> = dbQuery {
        val albumIdList = when (userType) {
            UserType.GOOGLE_USER -> {
                GoogleUserAlbumRelation
                    .slice(
                        GoogleUserAlbumRelation.albumId
                    ).select {
                        GoogleUserAlbumRelation.userId eq userId
                    }.orderBy(GoogleUserAlbumRelation.points, SortOrder.DESC)
                    .map {
                        it[GoogleUserAlbumRelation.albumId]
                    }
            }

            UserType.EMAIL_USER -> {
                EmailUserAlbumRelation
                    .slice(
                        EmailUserAlbumRelation.albumId
                    ).select {
                        EmailUserAlbumRelation.userId eq userId
                    }.orderBy(EmailUserAlbumRelation.points, SortOrder.DESC)
                    .map {
                        it[EmailUserAlbumRelation.albumId]
                    }
            }

            UserType.PASSKEY_USER -> {
                PasskeyUserAlbumRelation
                    .slice(
                        PasskeyUserAlbumRelation.albumId
                    ).select {
                        PasskeyUserAlbumRelation.userId eq userId
                    }.orderBy(PasskeyUserAlbumRelation.points, SortOrder.DESC)
                    .map {
                        it[PasskeyUserAlbumRelation.albumId]
                    }
            }
        }

        SongTable
            .join(
                otherTable = SongAlbumArtistRelationTable,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    SongAlbumArtistRelationTable.songId as Column<*> eq SongTable.id
                }
            )
            .join(
                otherTable = when (userType) {
                    UserType.GOOGLE_USER -> GoogleUserAlbumRelation
                    UserType.EMAIL_USER -> EmailUserAlbumRelation
                    UserType.PASSKEY_USER -> PasskeyUserAlbumRelation
                },
                joinType = JoinType.INNER,
                additionalConstraint = {
                    SongAlbumArtistRelationTable.albumId eq when (userType) {
                        UserType.GOOGLE_USER -> GoogleUserAlbumRelation.albumId
                        UserType.EMAIL_USER -> EmailUserAlbumRelation.albumId
                        UserType.PASSKEY_USER -> PasskeyUserAlbumRelation.albumId
                    }
                }
            ).slice(
                SongTable.id,
                SongTable.title,
                SongTable.album,
                SongTable.artist,
                SongTable.coverImage,
                SongTable.masterPlaylistPath,
                SongTable.totalTime,
                SongTable.genre,
                SongTable.publisher,
                SongTable.composer,
                SongTable.album_artist,
                SongTable.description,
                SongTable.track,
                SongTable.date
            ).select {
                when (userType) {
                    UserType.GOOGLE_USER -> GoogleUserAlbumRelation.albumId inList albumIdList
                    UserType.EMAIL_USER -> EmailUserAlbumRelation.albumId inList albumIdList
                    UserType.PASSKEY_USER -> PasskeyUserAlbumRelation.albumId inList albumIdList
                }
            }.map {
                it.toResponseSong()
            }.groupBy {
                it.album
            }.map {
                ResponseAlbum(
                    name = it.key,
                    listOfSongs = it.value
                )
            }
    }

    override suspend fun getPlaylists(userId: Long, userType: UserType): List<ResponsePlaylist> = dbQuery {
        SongTable
            .join(
                otherTable = when (userType) {
                    UserType.GOOGLE_USER -> GoogleUserPlaylistTable
                    UserType.EMAIL_USER -> EmailUserPlaylistTable
                    UserType.PASSKEY_USER -> PasskeyUserPlaylistTable
                },
                joinType = JoinType.INNER,
                additionalConstraint = {
                    SongTable.id eq when (userType) {
                        UserType.GOOGLE_USER -> GoogleUserPlaylistTable.songId
                        UserType.EMAIL_USER -> EmailUserPlaylistTable.songId
                        UserType.PASSKEY_USER -> PasskeyUserPlaylistTable.songId
                    } as Column<*>
                }
            ).join(
                otherTable = PlaylistTable,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    when (userType) {
                        UserType.GOOGLE_USER -> GoogleUserPlaylistTable.playlistId
                        UserType.EMAIL_USER -> EmailUserPlaylistTable.playlistId
                        UserType.PASSKEY_USER -> PasskeyUserPlaylistTable.playlistId
                    } as Column<*> eq PlaylistTable.id
                }
            )
            .slice(
                SongTable.id,
                SongTable.title,
                SongTable.album,
                SongTable.artist,
                SongTable.coverImage,
                SongTable.masterPlaylistPath,
                SongTable.totalTime,
                SongTable.genre,
                SongTable.publisher,
                SongTable.composer,
                SongTable.album_artist,
                SongTable.description,
                SongTable.track,
                SongTable.date,

                PlaylistTable.id,
                PlaylistTable.name
            )
            .select {
                when (userType) {
                    UserType.GOOGLE_USER -> GoogleUserPlaylistTable.userId
                    UserType.EMAIL_USER -> EmailUserPlaylistTable.userId
                    UserType.PASSKEY_USER -> PasskeyUserPlaylistTable.userId
                } eq userId
            }.orderBy(PlaylistTable.points, SortOrder.DESC)
            .map {
                it.toPlaylistResult()
            }.groupBy {
                it.playlistId
            }.map {
                ResponsePlaylist(
                    id = it.key,
                    name = it.value[0].playlistName,
                    listOfSongs = it.value.map { song ->
                        song.toResponseSong()
                    }
                )
            }
    }

    override suspend fun getFavourites(userId: Long, userType: UserType): Favourites = Favourites(
        listOfSongs = dbQuery {
            SongTable
                .join(
                    otherTable = when (userType) {
                        UserType.GOOGLE_USER -> GoogleUserFavouriteTable
                        UserType.EMAIL_USER -> EmailUserFavouriteTable
                        UserType.PASSKEY_USER -> PasskeyUserFavouriteTable
                    },
                    joinType = JoinType.INNER,
                    additionalConstraint = {
                        when (userType) {
                            UserType.GOOGLE_USER -> GoogleUserFavouriteTable.songId
                            UserType.EMAIL_USER -> EmailUserFavouriteTable.songId
                            UserType.PASSKEY_USER -> PasskeyUserFavouriteTable.songId
                        } as Column<*> eq SongTable.id
                    }
                )
                .slice(
                    SongTable.id,
                    SongTable.title,
                    SongTable.album,
                    SongTable.artist,
                    SongTable.coverImage,
                    SongTable.masterPlaylistPath,
                    SongTable.totalTime,
                    SongTable.genre,
                    SongTable.publisher,
                    SongTable.composer,
                    SongTable.album_artist,
                    SongTable.description,
                    SongTable.track,
                    SongTable.date,
                )
                .select {
                    when (userType) {
                        UserType.GOOGLE_USER -> GoogleUserFavouriteTable.userId
                        UserType.EMAIL_USER -> EmailUserFavouriteTable.userId
                        UserType.PASSKEY_USER -> PasskeyUserFavouriteTable.userId
                    } eq userId
                }.orderBy(
                    when (userType) {
                        UserType.GOOGLE_USER -> GoogleUserFavouriteTable.date
                        UserType.EMAIL_USER -> EmailUserFavouriteTable.date
                        UserType.PASSKEY_USER -> PasskeyUserFavouriteTable.date
                    }, SortOrder.DESC
                ).map {
                    it.toResponseSong()
                }
        }
    )

    override suspend fun isOldEnough(userId: Long, userType: UserType): Boolean = dbQuery {
        when (userType) {
            UserType.GOOGLE_USER -> {
                GoogleUserListenHistoryTable.select {
                    GoogleUserListenHistoryTable.userId eq userId
                }.count() > 14
            }

            UserType.EMAIL_USER -> {
                EmailUserListenHistoryTable.select {
                    EmailUserListenHistoryTable.userId eq userId
                }.count() > 14
            }

            UserType.PASSKEY_USER -> {
                PasskeyUserListenHistoryTable.select {
                    PasskeyUserListenHistoryTable.userId eq userId
                }.count() > 14
            }
        }
    }

    // getFevArtistMixPrev
    private suspend fun List<Int>.getListOfFevArtistMixPreview() = dbQuery {
        Song.find {
            SongTable.id inList SongArtistRelation.find {
                SongArtistRelationTable.artistId inList this@getListOfFevArtistMixPreview
            }.map {
                it.songId
            }
        }.orderBy(SongTable.points to SortOrder.DESC)
            .limit(4).map {
                FevArtistsMixPreview(
                    coverImage = it.coverImage.constructCoverPhotoUrl(),
                    artist = it.artist
                )
            }
    }

    // getAlbumPrev
    private suspend fun getFevAlbumIdList(userType: UserType, userId: Long) = dbQuery {
        when (userType) {
            UserType.GOOGLE_USER -> {
                GoogleUserAlbumRelation.slice(GoogleUserAlbumRelation.albumId)
                    .select {
                        GoogleUserAlbumRelation.userId eq userId
                    }.orderBy(GoogleUserAlbumRelation.points, SortOrder.DESC)
                    .limit(2)
                    .map {
                        it[GoogleUserAlbumRelation.albumId]
                    }
            }

            UserType.EMAIL_USER -> {
                EmailUserAlbumRelation.slice(EmailUserAlbumRelation.albumId)
                    .select {
                        EmailUserAlbumRelation.userId eq userId
                    }.orderBy(EmailUserAlbumRelation.points, SortOrder.DESC)
                    .limit(2)
                    .map {
                        it[EmailUserAlbumRelation.albumId]
                    }
            }

            UserType.PASSKEY_USER -> {
                PasskeyUserAlbumRelation.slice(PasskeyUserAlbumRelation.albumId)
                    .select {
                        PasskeyUserAlbumRelation.userId eq userId
                    }.orderBy(PasskeyUserAlbumRelation.points, SortOrder.DESC)
                    .limit(2)
                    .map {
                        it[PasskeyUserAlbumRelation.albumId]
                    }
            }
        }
    }

    private suspend fun List<Long>.getMostPopularAlbumsIdList() = dbQuery {
        Album.all()
            .orderBy(AlbumTable.points to SortOrder.DESC)
            .filterNot { album ->
                this.any {
                    album.id.value == it
                }
            }.map { it.id.value }
            .take(7)
            .shuffled(Random())
    }

    private suspend fun List<Long>.getAlbumOnAlbumIdList() = dbQuery {
        SongTable
            .join(
                otherTable = SongAlbumArtistRelationTable,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    SongTable.id eq SongAlbumArtistRelationTable.songId as Column<*>
                }
            ).join(
                otherTable = AlbumTable,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    SongAlbumArtistRelationTable.albumId as Column<*> eq AlbumTable.id
                }
            ).slice(
                SongTable.id,
                SongTable.title,
                SongTable.coverImage,
                SongTable.artist,
                SongTable.album
            ).select {
                AlbumTable.id inList this@getAlbumOnAlbumIdList
            }.orderBy(AlbumTable.points, SortOrder.ASC)
            .map {
                SongPreview(
                    id = it[SongTable.id].value.toString(),
                    title = it[SongTable.title],
                    coverImage = it[SongTable.coverImage].constructCoverPhotoUrl(),
                    artist = it[SongTable.artist],
                    album = it[SongTable.album]
                )
            }.groupBy {
                it.album
            }.map {
                AlbumPreview(
                    name = it.key,
                    listOfSongs = it.value.take(4)
                )
            }
    }

    // getArtistPrev
    private suspend fun getHistoryArtistIdList(
        userType: UserType,
        usedId: Long
    ) = dbQuery {
        when (userType) {
            UserType.GOOGLE_USER -> {
                GoogleUserListenHistoryTable
                    .slice(
                        GoogleUserListenHistoryTable.songId,
                        GoogleUserListenHistoryTable.repeat
                    )
                    .select {
                        GoogleUserListenHistoryTable.userId eq usedId and (
                                GoogleUserListenHistoryTable.date greaterEq (
                                        LocalDateTime.now().minus(1, ChronoUnit.DAYS)
                                        )
                                )
                    }.map {
                        Pair(
                            first = it[GoogleUserListenHistoryTable.songId],
                            second = it[GoogleUserListenHistoryTable.repeat]
                        )
                    }
            }

            UserType.EMAIL_USER -> {
                EmailUserListenHistoryTable
                    .slice(
                        EmailUserListenHistoryTable.songId,
                        EmailUserListenHistoryTable.repeat
                    )
                    .select {
                        EmailUserListenHistoryTable.userId eq usedId and (
                                EmailUserListenHistoryTable.date greaterEq (
                                        LocalDateTime.now().minus(1, ChronoUnit.DAYS)
                                        )
                                )
                    }.map {
                        Pair(
                            first = it[EmailUserListenHistoryTable.songId],
                            second = it[EmailUserListenHistoryTable.repeat]
                        )
                    }
            }

            UserType.PASSKEY_USER -> {
                PasskeyUserListenHistoryTable
                    .slice(
                        PasskeyUserListenHistoryTable.songId,
                        PasskeyUserListenHistoryTable.repeat
                    )
                    .select {
                        PasskeyUserListenHistoryTable.userId eq usedId and (
                                PasskeyUserListenHistoryTable.date greaterEq (
                                        LocalDateTime.now().minus(1, ChronoUnit.DAYS)
                                        )
                                )
                    }.map {
                        Pair(
                            first = it[PasskeyUserListenHistoryTable.songId],
                            second = it[PasskeyUserListenHistoryTable.repeat]
                        )
                    }
            }
        }.groupBy(
            keySelector = { it.first },
            valueTransform = { it.second }
        ).map { (key, values) ->
            key to (values.sum() + values.size)
        }.sortedByDescending {
            it.second
        }.take(2)
            .map { it.first }
            .let {
                SongArtistRelation.find {
                    SongArtistRelationTable.songId inList it
                }.map {
                    it.artistId
                }
            }
    }

    private suspend fun getFevArtistIdList(
        userType: UserType,
        usedId: Long
    ) = dbQuery {
        when (userType) {
            UserType.GOOGLE_USER -> {
                GoogleUserArtistRelation.find {
                    GoogleUserArtistRelationTable.userId eq usedId
                }.map {
                    it.artistId
                }
            }

            UserType.EMAIL_USER -> {
                EmailUserArtistRelation.find {
                    EmailUserArtistRelationTable.userId eq usedId
                }.map {
                    it.artistId
                }
            }

            UserType.PASSKEY_USER -> {
                PasskeyUserArtistRelation.find {
                    PasskeyUserArtistRelationTable.userId eq usedId
                }.map {
                    it.artistId
                }
            }
        }
    }

    private suspend fun List<Int>.getResponseArtistPreviewOnArtistIdList() = dbQuery {
        SongTable
            .join(
                otherTable = ArtistTable,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    SongTable.artist eq ArtistTable.name
                }
            ).slice(
                SongTable.id,
                SongTable.title,
                SongTable.coverImage,
                SongTable.artist,
                SongTable.album,
                SongTable.points,
                ArtistTable.id,
                ArtistTable.profilePicUrl
            ).select {
                ArtistTable.id inList this@getResponseArtistPreviewOnArtistIdList
            }.mapToResponseArtistsPreview()
    }

    private fun Query.mapToResponseArtistsPreview() = this.map {
        DbResponseArtistPreview(
            songId = it[SongTable.id].value.toString(),
            songTitle = it[SongTable.title],
            songCover = it[SongTable.coverImage].constructCoverPhotoUrl(),
            artist = it[SongTable.artist],
            album = it[SongTable.album],
            artistId = it[ArtistTable.id].value,
            artistImage = it[ArtistTable.profilePicUrl]
        )
    }.groupBy {
        it.artist
    }.map {
        ResponseArtistsPreview(
            artist = ResponseArtist(
                id = it.value[0].artistId,
                name = it.key,
                imageUrl = ResponseArtist.getArtistImageUrl(it.value[0].artistImage)
            ),
            listOfSongs = it.value.toListOfSongPreview().take(5)
        )
    }

    // getDailyMixPrev
    private suspend fun getHistorySongIdList(userType: UserType, userId: Long) = dbQuery {
        when (userType) {
            UserType.GOOGLE_USER -> {
                GoogleUserListenHistoryTable
                    .slice(
                        EmailUserListenHistoryTable.songId
                    ).select {
                        GoogleUserListenHistoryTable.userId eq userId and (
                                GoogleUserListenHistoryTable.date greaterEq (
                                        LocalDateTime.now().minus(3, ChronoUnit.DAYS)
                                        )
                                )
                    }
                    .withDistinct()
                    .orderBy(org.jetbrains.exposed.sql.Random())
                    .limit(8)
                    .map {
                        it[GoogleUserListenHistoryTable.songId]
                    }
            }

            UserType.EMAIL_USER -> {
                EmailUserListenHistoryTable
                    .slice(
                        EmailUserListenHistoryTable.songId
                    ).select {
                        EmailUserListenHistoryTable.userId eq userId and (
                                EmailUserListenHistoryTable.date greaterEq (
                                        LocalDateTime.now().minus(3, ChronoUnit.DAYS)
                                        )
                                )
                    }
                    .withDistinct()
                    .orderBy(org.jetbrains.exposed.sql.Random())
                    .limit(8)
                    .map {
                        it[EmailUserListenHistoryTable.songId]
                    }
            }

            UserType.PASSKEY_USER -> {
                PasskeyUserListenHistoryTable
                    .slice(
                        PasskeyUserListenHistoryTable.songId
                    ).select {
                        PasskeyUserListenHistoryTable.userId eq userId and (
                                PasskeyUserListenHistoryTable.date greaterEq (
                                        LocalDateTime.now().minus(3, ChronoUnit.DAYS)
                                        )
                                )
                    }
                    .withDistinct()
                    .orderBy(org.jetbrains.exposed.sql.Random())
                    .limit(8)
                    .map {
                        it[PasskeyUserListenHistoryTable.songId]
                    }
            }
        }
    }

    private suspend fun getPreviewSongsByTheArtists(songIdList: List<Long>) = dbQuery {
        val sar1 = SongArtistRelationTable.alias("sar1")
        val sar2 = SongArtistRelationTable.alias("sar2")

        SongTable
            .join(
                otherTable = sar1,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    SongTable.id eq sar1[SongArtistRelationTable.songId] as Column<*>
                }
            ).join(
                otherTable = ArtistTable,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    ArtistTable.id eq sar1[SongArtistRelationTable.artistId] as Column<*>
                }
            ).join(
                otherTable = sar2,
                joinType = JoinType.INNER,
                additionalConstraint = {
                    ArtistTable.id eq sar2[SongArtistRelationTable.artistId] as Column<*>
                }
            ).slice(
                SongTable.id,
                SongTable.title,
                SongTable.coverImage,
                SongTable.artist,
                SongTable.album,
                SongTable.points
            ).select {
                sar2[SongArtistRelationTable.songId] inList songIdList
            }.orderBy(SongTable.points, SortOrder.DESC)
            .map {
                SongPreview(
                    it[SongTable.id].toString(),
                    it[SongTable.title],
                    it[SongTable.coverImage].constructCoverPhotoUrl(),
                    it[SongTable.artist],
                    it[SongTable.album]
                )
            }.groupBy {
                it.artist
            }
    }
}