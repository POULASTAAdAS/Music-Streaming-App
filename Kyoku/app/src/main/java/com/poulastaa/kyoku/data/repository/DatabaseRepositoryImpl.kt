package com.poulastaa.kyoku.data.repository

import android.content.Context
import com.poulastaa.kyoku.data.database.AppDao
import com.poulastaa.kyoku.data.model.api.service.ResponseSong
import com.poulastaa.kyoku.data.model.api.service.home.AlbumPreview
import com.poulastaa.kyoku.data.model.api.service.home.DailyMixPreview
import com.poulastaa.kyoku.data.model.api.service.home.FevArtistsMixPreview
import com.poulastaa.kyoku.data.model.api.service.home.ResponseAlbum
import com.poulastaa.kyoku.data.model.api.service.home.ResponseArtistsPreview
import com.poulastaa.kyoku.data.model.api.service.home.ResponsePlaylist
import com.poulastaa.kyoku.data.model.api.service.home.SongPreview
import com.poulastaa.kyoku.data.model.database.PlaylistWithSongs
import com.poulastaa.kyoku.data.model.database.table.AlbumPreviewSongRelationTable
import com.poulastaa.kyoku.data.model.database.table.AlbumTable
import com.poulastaa.kyoku.data.model.database.table.ArtistPreviewSongRelation
import com.poulastaa.kyoku.data.model.database.table.DailyMixPrevTable
import com.poulastaa.kyoku.data.model.database.table.FavouriteTable
import com.poulastaa.kyoku.data.model.database.table.PinnedTable
import com.poulastaa.kyoku.data.model.database.table.PlaylistTable
import com.poulastaa.kyoku.data.model.database.table.RecentlyPlayedPrevTable
import com.poulastaa.kyoku.data.model.database.table.SongAlbumRelationTable
import com.poulastaa.kyoku.data.model.database.table.SongPlaylistRelationTable
import com.poulastaa.kyoku.data.model.screens.library.PinnedDataType
import com.poulastaa.kyoku.domain.repository.DataStoreOperation
import com.poulastaa.kyoku.utils.toAlbumTablePrevEntry
import com.poulastaa.kyoku.utils.toArtistTableEntry
import com.poulastaa.kyoku.utils.toFevArtistMixPrevTable
import com.poulastaa.kyoku.utils.toSongPrevTableEntry
import com.poulastaa.kyoku.utils.toSongTable
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ViewModelScoped
class DatabaseRepositoryImpl @Inject constructor(
    private val dao: AppDao
) {
    private var context: Context? = null
    private var header: String? = null

    fun setValues(context: Context, header: String) {
        this.context = context
        this.header = header
    }

    fun insertIntoPlaylistSpotify(
        data: List<ResponseSong>,
        id: Long,
        playlistName: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val playlistId = async {
                dao.insertPlaylist(
                    playlist = PlaylistTable(
                        playlistId = id,
                        name = playlistName
                    )
                )
            }.await()

            data.forEach {
                dao.insertSongPlaylistRelation(
                    data = SongPlaylistRelationTable(
                        playlistId = playlistId,
                        songId = async {
                            dao.insertSong(
                                song = it.toSongTable()
                            )
                        }.await()
                    )
                )
            }
        }
    }

    fun getAllPlaylist(): Flow<List<PlaylistWithSongs>> = dao.getAllPlaylist()

    suspend fun checkIfNewUser() = dao.checkIfNewUser().isEmpty()


    fun insertIntoFevArtistMixPrev(list: List<FevArtistsMixPreview>) {
        CoroutineScope(Dispatchers.IO).launch {
            async {
                list.forEach {
                    dao.insertIntoFevArtistMixPrev(
                        data = it.toFevArtistMixPrevTable()
                    )
                }
            }.await()

//            async {
//                dao.getAllFevArtistMixPrev().forEach {
//                    if (it.coverImage.startsWith(SERVICE_BASE_URL))
//                        dao.updateArtistCoverImage(
//                            coverImage = it.coverImage.encodeImage(
//                                context = context!!,
//                                header = header!!,
//                                isCookie = !header!!.startsWith("B")
//                            ),
//                            id = it.id
//                        )
//                }
//            }.await()
        }
    }

    fun insertIntoAlbumPrev(list: List<AlbumPreview>) {
        CoroutineScope(Dispatchers.IO).launch {
            async {
                list.forEach {
                    val albumId = dao.insertIntoAlbumPrev(data = it.toAlbumTablePrevEntry())

                    it.listOfSongs.forEach { song ->
                        val songId = dao.insertIntoSongPrev(
                            data = song.toSongPrevTableEntry()
                        )

                        dao.insertIntoAlbumPrevSongRelationTable(
                            data = AlbumPreviewSongRelationTable(
                                albumId = albumId,
                                songId = songId
                            )
                        )
                    }
                }
            }.await()

//            async {
//                dao.getAllPrevSong().forEach {
//                    if (it.coverImage.startsWith(SERVICE_BASE_URL))
//                        dao.updatePrevSong(
//                            coverImage = it.coverImage.encodeImage(
//                                context = context!!,
//                                header = header!!,
//                                isCookie = !header!!.startsWith("B")
//                            ),
//                            id = it.id
//                        )
//                }
//            }.await()
        }
    }

    fun insertResponseArtistPrev(list: List<ResponseArtistsPreview>) {
        CoroutineScope(Dispatchers.IO).launch {
            async {
                list.forEach {
                    try {
                        dao.insertIntoArtist(
                            it.artist.toArtistTableEntry()
                        )
                    } catch (e: Exception) {
                        null
                    }?.let { id ->
                        it.listOfSongs.forEach { previewSong ->
                            val songId = dao.insertIntoSongPrev(
                                previewSong.toSongPrevTableEntry()
                            )

                            dao.insertIntoArtistPrevSongRelationTable(
                                data = ArtistPreviewSongRelation(
                                    artistId = id,
                                    songId = songId
                                )
                            )
                        }
                    }
                }
            }.await()

//            val artist = async {
//                dao.getAllFromArtist().forEach {
//                    if (it.coverImage.startsWith(SERVICE_BASE_URL))
//                        dao.updatePrevArtist(
//                            coverImage = it.coverImage.encodeImage(
//                                context = context!!,
//                                header = header!!,
//                                isCookie = !header!!.startsWith("B")
//                            ),
//                            id = it.id
//                        )
//                }
//            }
//
//            val prevSong = async {
//                dao.getAllPrevSong().forEach {
//                    if (it.coverImage.startsWith(SERVICE_BASE_URL))
//                        dao.updatePrevSong(
//                            coverImage = it.coverImage.encodeImage(
//                                context = context!!,
//                                header = header!!,
//                                isCookie = !header!!.startsWith("B")
//                            ),
//                            id = it.id
//                        )
//                }
//            }
//
//            artist.await()
//            prevSong.await()
        }
    }

    fun insertDailyMixPrev(data: DailyMixPreview) {
        CoroutineScope(Dispatchers.IO).launch {
            data.listOfSongs.forEach {
                dao.insertIntoDailyMixPrevTable(
                    data = DailyMixPrevTable(
                        id = dao.insertIntoSongPrev(
                            data = it.toSongPrevTableEntry()
                        )
                    )
                )
            }
        }
    }

    fun readFevArtistMixPrev() = dao.readFevArtistPrev()
    fun readAllAlbumPrev() = dao.readAllAlbumPrev()
    fun readAllArtistPrev() = dao.readAllArtistPrev()
    fun readPlaylistPreview() = dao.readPreviewPlaylist()
    fun redRecentlyPlayed() = dao.redRecentlyPlayed()
    fun radSavedAlbumPrev() = dao.radSavedAlbumPrev()
    suspend fun readFavouritePrev() = dao.readFavouritePrev()


    fun insertIntoPlaylistHome(list: List<ResponsePlaylist>) {
        CoroutineScope(Dispatchers.IO).launch {
            async {
                list.forEach {
                    val playlistId = async {
                        dao.insertPlaylist(
                            playlist = PlaylistTable(
                                playlistId = it.id,
                                name = it.name
                            )
                        )
                    }.await()

                    it.listOfSongs.forEach { song ->
                        val songId = async {
                            dao.insertSong(
                                song = song.toSongTable()
                            )
                        }.await()

                        dao.insertSongPlaylistRelation(
                            data = SongPlaylistRelationTable(
                                playlistId = playlistId,
                                songId = songId
                            )
                        )
                    }
                }
            }.await()

//            async {
//                dao.getAllFromSongTable().forEach {
//                    if (it.coverImage.startsWith(SERVICE_BASE_URL))
//                        dao.updateSong(
//                            coverImage = it.coverImage.encodeImage(
//                                context = context!!,
//                                header = header!!,
//                                isCookie = !header!!.startsWith("B")
//                            ),
//                            id = it.id
//                        )
//                }
//            }.await()
        }
    }

    fun insertIntoFavourite(list: List<ResponseSong>) {
        CoroutineScope(Dispatchers.IO).launch {
            list.forEach {
                dao.insertIntoFavourite(
                    data = FavouriteTable(
                        songId = dao.insertSong(
                            song = it.toSongTable()
                        )
                    )
                )
            }
        }
    }

    fun insertIntoAlbum(list: List<ResponseAlbum>) {
        CoroutineScope(Dispatchers.IO).launch {
            list.forEach {
                val albumId = async {
                    dao.insertIntoAlbum(
                        data = AlbumTable(
                            name = it.name
                        )
                    )
                }.await()

                it.listOfSongs.forEach { song ->
                    val songId = async {
                        dao.insertSong(
                            song = song.toSongTable()
                        )
                    }.await()

                    dao.insertIntoSongAlbumRelationTable(
                        data = SongAlbumRelationTable(
                            songId = songId,
                            albumId = albumId
                        )
                    )
                }
            }
        }
    }


    fun insertIntoRecentlyPlayedPrev(list: List<SongPreview>) {
        CoroutineScope(Dispatchers.IO).launch {
            async {
                list.forEach {
                    val songId = async {
                        dao.insertIntoSongPrev(
                            data = it.toSongPrevTableEntry()
                        )
                    }.await()


                    dao.insertIntoRecentlyPlayedPrevTable(
                        data = RecentlyPlayedPrevTable(
                            songId = songId
                        )
                    )
                }
            }.await()

//            async {
//                dao.getAllPrevSong().forEach {
//                    if (it.coverImage.startsWith(SERVICE_BASE_URL))
//                        dao.updatePrevSong(
//                            coverImage = it.coverImage.encodeImage(
//                                context = context!!,
//                                header = header!!,
//                                isCookie = !header!!.startsWith("B")
//                            ),
//                            id = it.id
//                        )
//                }
//            }.await()
        }
    }

    fun readAllAlbum() = dao.readAllAlbum()
    fun readAllArtist() = dao.readAllArtist()

    suspend fun checkIfPlaylistIdPinned(name: String) =
        dao.checkIfPlaylistIsPinned(name)?.let { true } ?: false

    suspend fun checkIfAlbumPinned(name: String) =
        dao.checkIfAlbumIsPinned(name)?.let { true } ?: false

    suspend fun checkIfArtistPinned(name: String) =
        dao.checkIfArtistPinned(name)?.let { true } ?: false

    suspend fun addToPinnedTable(
        type: PinnedDataType,
        name: String,
        ds: DataStoreOperation
    ) = withContext(Dispatchers.IO) {
        when (type) {
            PinnedDataType.PLAYLIST -> {
                val id = async {
                    dao.getIdOfPlaylist(name)
                }.await() ?: return@withContext false

                dao.addToPinnedTable(
                    data = PinnedTable(
                        playlistId = id
                    )
                )

                true
            }

            PinnedDataType.ARTIST -> {
                val id = async {
                    dao.getIdOfArtist(name)
                }.await() ?: return@withContext false

                dao.addToPinnedTable(
                    data = PinnedTable(
                        artistId = id
                    )
                )

                true
            }

            PinnedDataType.ALBUM -> {
                val id = async {
                    dao.getIdOfAlbum(name)
                }.await() ?: return@withContext false

                dao.addToPinnedTable(
                    data = PinnedTable(
                        albumId = id
                    )
                )

                true
            }

            PinnedDataType.FAVOURITE -> {
                ds.storeFavouritePinnedState(true)

                true
            }
        }
    }

    suspend fun removeFromPinnedTable(
        type: PinnedDataType,
        name: String,
        ds: DataStoreOperation
    ) = withContext(Dispatchers.IO) {
        when (type) {
            PinnedDataType.PLAYLIST -> {
                val playlistId = dao.getIdOfPlaylist(name) ?: return@withContext false

                return@withContext try {
                    dao.removePlaylistIdFromPinnedTable(playlistId).let { true }
                } catch (e: Exception) {
                    false
                }
            }

            PinnedDataType.ARTIST -> {
                val artistId = dao.getIdOfArtist(name) ?: return@withContext false

                return@withContext try {
                    dao.removeArtistIdFromPinnedTable(artistId).let { true }
                } catch (e: Exception) {
                    false
                }
            }

            PinnedDataType.ALBUM -> {
                val albumId = dao.getIdOfAlbum(name) ?: return@withContext false

                return@withContext try {
                    dao.removeAlbumIdFromPinnedTable(albumId).let { true }
                } catch (e: Exception) {
                    false
                }
            }

            PinnedDataType.FAVOURITE -> {
                ds.storeFavouritePinnedState(false)
                true
            }
        }
    }


    suspend fun deletePlaylistArtistAlbumFavouriteEntry(
        type: PinnedDataType,
        name: String,
        ds: DataStoreOperation
    ) = withContext(Dispatchers.IO) {
        when (type) {
            PinnedDataType.PLAYLIST -> {
                val playlistId = dao.getIdOfPlaylist(name) ?: return@withContext false

                return@withContext try {
                    dao.deletePlaylist(playlistId)
                    true
                } catch (e: Exception) {
                    false
                }
            }

            PinnedDataType.ALBUM -> {
                val albumId = dao.getIdOfAlbum(name) ?: return@withContext false

                return@withContext try {
                    dao.deleteAlbum(albumId)
                    true
                } catch (e: Exception) {
                    false
                }
            }

            PinnedDataType.ARTIST -> {
                val artistId = dao.getIdOfArtist(name) ?: return@withContext false

                return@withContext try {
                    dao.deleteArtist(artistId)
                    true
                } catch (e: Exception) {
                    false
                }
            }

            PinnedDataType.FAVOURITE -> {
                dao.deleteFavourites()
                true
            }
        }
    }

    fun readPinnedPlaylist() = dao.readPinnedPlaylist()
    fun readPinnedAlbum() = dao.readPinnedAlbum()
    fun readPinnedArtist() = dao.readPinnedArtist()
}