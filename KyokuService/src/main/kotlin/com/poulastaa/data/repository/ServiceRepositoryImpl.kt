package com.poulastaa.data.repository

import com.poulastaa.data.mappers.getUserType
import com.poulastaa.data.mappers.getYear
import com.poulastaa.data.mappers.toPlaylistDto
import com.poulastaa.data.model.*
import com.poulastaa.data.model.home.HomeDto
import com.poulastaa.domain.model.ReqUserPayload
import com.poulastaa.domain.model.route_model.req.home.HomeReq
import com.poulastaa.domain.model.route_model.req.playlist.SavePlaylistReq
import com.poulastaa.domain.model.route_model.req.playlist.UpdatePlaylistReq
import com.poulastaa.domain.repository.*
import com.poulastaa.domain.table.relation.UserGenreRelationTable
import com.poulastaa.plugins.query
import kotlinx.coroutines.*
import org.jetbrains.exposed.sql.select

class ServiceRepositoryImpl(
    private val jwt: JWTRepository,
    private val setupRepo: SetupRepository,
    private val kyokuRepo: DatabaseRepository,
    private val userRepo: UserRepository,
    private val homeRepo: HomeRepository,
) : ServiceRepository {
    override suspend fun getSpotifyPlaylist(
        userPayload: ReqUserPayload,
        spotifyPayload: List<SpotifySongTitle>,
    ): PlaylistDto {
        val user = userRepo.getUserOnPayload(userPayload) ?: return PlaylistDto()

        val result = setupRepo.getSpotifyPlaylist(
            spotifyPayload = spotifyPayload
        )

        CoroutineScope(Dispatchers.IO).launch {
            val songIdList = result.listOfSong.map { it.resultSong.id }
            val artistIdList = result.listOfSong.map {
                it.artistList.map { artist -> artist.id }
            }.flatten()

            kyokuRepo.updateSongPointByOne(songIdList)
            kyokuRepo.updateArtistPointByOne(artistIdList)
            userRepo.createUserPlaylist(
                userId = user.id,
                userType = userPayload.userType,
                playlistId = result.id,
                songIdList = songIdList
            )
        }

        return result.toPlaylistDto()
    }

    override suspend fun updateBDate(
        userPayload: ReqUserPayload,
        date: Long,
    ): Boolean {
        val user = userRepo.getUserOnPayload(userPayload) ?: return false

        return userRepo.updateBDate(
            userId = user.id,
            bDate = date,
            userType = userPayload.userType
        )
    }

    override suspend fun getGenre(
        userPayload: ReqUserPayload,
        genreIds: List<Int>,
    ): SuggestGenreDto {
        userRepo.getUserOnPayload(userPayload) ?: return SuggestGenreDto()

        return SuggestGenreDto(
            listOgGenre = setupRepo.getGenre(genreIds)
        )
    }

    override suspend fun storeGenre(
        userPayload: ReqUserPayload,
        genreIds: List<Int>,
    ): Boolean {
        val user = userRepo.getUserOnPayload(userPayload) ?: return false

        userRepo.storeGenre(
            userId = user.id,
            userType = user.userType,
            idList = genreIds
        )

        kyokuRepo.updateGenrePointByOne(genreIds)

        return true
    }

    override suspend fun getArtist(
        userPayload: ReqUserPayload,
        artistIds: List<Long>,
    ): SuggestArtistDao {
        userRepo.getUserOnPayload(userPayload) ?: return SuggestArtistDao()

        return SuggestArtistDao(
            listOfArtist = setupRepo.getArtist(artistIds)
        )
    }

    override suspend fun storeArtist(
        userPayload: ReqUserPayload,
        artistIds: List<Long>,
    ): Boolean {
        val user = userRepo.getUserOnPayload(userPayload) ?: return false

        userRepo.storeArtist(
            userId = user.id,
            userType = userPayload.userType,
            idList = artistIds
        )

        kyokuRepo.updateArtistPointByOne(artistIds)

        return true
    }

    override suspend fun homeReq(
        userPayload: ReqUserPayload,
        req: HomeReq,
    ): HomeDto = coroutineScope {
        val user = userRepo.getUserOnPayload(userPayload) ?: return@coroutineScope HomeDto()

        val popularSongMixDef = async { homeRepo.getPopularSongMixPrev(user.countryId) }
        val popularSongFromYourTimeDef = async {
            homeRepo.getPopularSongFromUserTimePrev(user.bDate.getYear(), user.countryId)
        }
        val favouriteArtistMixDef = async {
            homeRepo.getFavouriteArtistMixPrev(
                userId = user.id,
                userType = user.userType,
                countryId = user.countryId
            )
        }
        val dayTimeSongDef = async {
            homeRepo.getDayTypeSongPrev(
                dayType = req.dayType,
                countryId = user.countryId
            )
        }
        val poplarAlbumDef = async { homeRepo.getPopularAlbumPrev(user.countryId) }
        val poplarArtistDef = async {
            homeRepo.getPopularArtist(
                userId = user.id,
                userType = user.userType,
                countryId = user.countryId
            )
        }

        val popularArtist = poplarArtistDef.await()

        val poplarArtistSongDef = async {
            homeRepo.getPopularArtistSongPrev(
                userId = user.id,
                userType = user.userType,
                excludeArtist = popularArtist.map {
                    it.id
                },
                countryId = user.countryId
            )
        }

        HomeDto(
            status = ResponseStatus.SUCCESS,
            popularSongMixPrev = popularSongMixDef.await(),
            popularSongFromYourTimePrev = popularSongFromYourTimeDef.await(),
            favouriteArtistMixPrev = favouriteArtistMixDef.await(),
            dayTypeSong = dayTimeSongDef.await(),
            popularAlbum = poplarAlbumDef.await(),
            popularArtist = popularArtist,
            popularArtistSong = poplarArtistSongDef.await()
        )
    }

    override suspend fun getLoginData(
        userType: String,
        token: String,
    ): LogInDto {
        val email = jwt.verifyJWTToken(
            token = token,
            claim = "getLoginDataKey"
        ) ?: return LogInDto()

        val uType = userType.getUserType() ?: return LogInDto()
        val pair = userRepo.getUserData(uType, email)

        val user = userRepo.getUserOnEmail(
            email = email,
            userType = uType
        ) ?: return LogInDto()

        return coroutineScope {
            val popularSongMixDef = async { homeRepo.getPopularSongMixPrev(user.countryId) }
            val popularSongFromYourTimeDef = async {
                homeRepo.getPopularSongFromUserTimePrev(user.bDate.getYear(), user.countryId)
            }
            val favouriteArtistMixDef = async {
                homeRepo.getFavouriteArtistMixPrev(
                    userId = user.id,
                    userType = user.userType,
                    countryId = user.countryId
                )
            }
            val poplarAlbumDef = async { homeRepo.getPopularAlbumPrev(user.countryId) }
            val poplarArtistDef = async {
                homeRepo.getPopularArtist(
                    userId = user.id,
                    userType = user.userType,
                    countryId = user.countryId
                )
            }

            val popularArtist = poplarArtistDef.await()
            val poplarArtistSongDef = async {
                homeRepo.getPopularArtistSongPrev(
                    userId = user.id,
                    userType = user.userType,
                    excludeArtist = popularArtist.map {
                        it.id
                    },
                    countryId = user.countryId
                )
            }

            val savedGenreDef = async {
                query {
                    UserGenreRelationTable.select {
                        UserGenreRelationTable.userId eq user.id
                    }.empty()
                }
            }

            val userAuthStatus = when {
                pair.first.savedArtist.isEmpty() -> UserAuthStatus.USER_FOUND_SET_ARTIST
                savedGenreDef.await() -> UserAuthStatus.USER_FOUND_SET_GENRE
                else -> UserAuthStatus.USER_FOUND_HOME
            }

            if (userAuthStatus != UserAuthStatus.USER_FOUND_HOME) {
                LogInDto(
                    status = userAuthStatus
                )
            } else
                LogInDto(
                    status = userAuthStatus,
                    popularSongMixPrev = popularSongMixDef.await(),
                    popularSongFromYourTimePrev = popularSongFromYourTimeDef.await(),
                    favouriteArtistMixPrev = favouriteArtistMixDef.await(),
                    popularAlbum = poplarAlbumDef.await(),
                    popularArtist = popularArtist,
                    popularArtistSong = poplarArtistSongDef.await(),


                    savedPlaylist = pair.first.savedPlaylist,
                    savedAlbum = pair.first.savedAlbum,
                    savedArtist = pair.first.savedArtist,
                    favouriteSong = pair.first.favouriteSong
                )
        }
    }

    override suspend fun addToFavourite(
        id: Long,
        userPayload: ReqUserPayload,
    ): SongDto {
        val user = userRepo.getUserOnPayload(userPayload) ?: return SongDto()

        return userRepo.addToFavourite(
            id = id,
            userType = user.userType,
            userId = user.id
        )
    }

    override suspend fun removeFromFavourite(id: Long, userPayload: ReqUserPayload): Boolean {
        val user = userRepo.getUserOnPayload(userPayload) ?: return false

        return userRepo.removeFromFavourite(id, email = user.email, userType = user.userType)
    }

    override suspend fun addArtist(artistId: Long, payload: ReqUserPayload): ArtistDto {
        val user = userRepo.getUserOnPayload(payload) ?: return ArtistDto()

        return userRepo.addArtist(artistId, user.email, user.userType)
    }

    override suspend fun removeArtist(id: Long, userPayload: ReqUserPayload): Boolean {
        val user = userRepo.getUserOnPayload(userPayload) ?: return false

        return userRepo.removeFromFavourite(id, email = user.email, userType = user.userType)
    }

    override suspend fun addAlbum(albumId: Long, payload: ReqUserPayload): AlbumWithSongDto {
        val user = userRepo.getUserOnPayload(payload) ?: return AlbumWithSongDto()

        return userRepo.addAlbum(albumId, email = user.email, userType = user.userType)
    }

    override suspend fun removeAlbum(id: Long, userPayload: ReqUserPayload): Boolean {
        val user = userRepo.getUserOnPayload(userPayload) ?: return false

        return userRepo.removeAlbum(id, email = user.email, userType = user.userType)
    }

    override suspend fun savePlaylist(req: SavePlaylistReq, payload: ReqUserPayload): PlaylistDto {
        val user = userRepo.getUserOnPayload(payload) ?: return PlaylistDto()

        val type = when (req.type) {
            ExploreType.OLD_GEM.name -> ExploreType.OLD_GEM
            ExploreType.POPULAR.name -> ExploreType.POPULAR
            else -> ExploreType.ARTIST_MIX
        }

        return coroutineScope {
            val oldSongDef = req.idList.map { async { kyokuRepo.getSongOnId(it) } }
            val songDef = async {
                when (type) {
                    ExploreType.POPULAR -> homeRepo.getPopularSongMix(user.countryId).toMutableList()
                    ExploreType.OLD_GEM -> homeRepo.getOldGem(user.countryId, user.bDate.getYear()).toMutableList()
                    ExploreType.ARTIST_MIX -> homeRepo.getArtistSongMix(
                        countryId = user.countryId,
                        userId = user.id,
                        userType = user.userType
                    ).toMutableList()
                }
            }

            val oldSong: List<SongDto> = oldSongDef.awaitAll()
            val song = songDef.await()

            oldSong.forEach { old ->
                if (!song.any { it.id == old.id }) song.add(old)
            }

            val playlistId = kyokuRepo.createPlaylist(
                name = req.name,
                userId = user.id,
                userType = user.userType,
                songIdList = song.map { it.id }
            )

            PlaylistDto(
                id = playlistId,
                name = req.name,
                listOfSong = song.toList()
            )
        }
    }

    override suspend fun getSong(songId: Long): SongDto = kyokuRepo.getSongOnId(songId)

    override suspend fun updatePlaylist(
        req: UpdatePlaylistReq,
        payload: ReqUserPayload,
    ): Boolean {
        val user = userRepo.getUserOnPayload(payload) ?: return false

        userRepo.updatePlaylist(
            userId = user.id,
            userType = user.userType,
            songId = req.songId,
            map = req.playlistIdList
        )

        return true
    }
}