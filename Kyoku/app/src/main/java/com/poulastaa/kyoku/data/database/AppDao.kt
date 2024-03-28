package com.poulastaa.kyoku.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.poulastaa.kyoku.data.model.database.AlbumPrevResult
import com.poulastaa.kyoku.data.model.database.ArtistPrevResult
import com.poulastaa.kyoku.data.model.database.PlaylistPrevResult
import com.poulastaa.kyoku.data.model.database.PlaylistWithSongs
import com.poulastaa.kyoku.data.model.database.table.AlbumPrevTable
import com.poulastaa.kyoku.data.model.database.table.AlbumPreviewSongRelationTable
import com.poulastaa.kyoku.data.model.database.table.AlbumTable
import com.poulastaa.kyoku.data.model.database.table.ArtistPrevTable
import com.poulastaa.kyoku.data.model.database.table.ArtistPreviewSongRelation
import com.poulastaa.kyoku.data.model.database.table.DailyMixPrevTable
import com.poulastaa.kyoku.data.model.database.table.FavouriteTable
import com.poulastaa.kyoku.data.model.database.table.FevArtistsMixPreviewTable
import com.poulastaa.kyoku.data.model.database.table.PinnedTable
import com.poulastaa.kyoku.data.model.database.table.PlaylistTable
import com.poulastaa.kyoku.data.model.database.table.RecentlyPlayedPrevTable
import com.poulastaa.kyoku.data.model.database.table.SongAlbumRelationTable
import com.poulastaa.kyoku.data.model.database.table.SongPlaylistRelationTable
import com.poulastaa.kyoku.data.model.database.table.SongPreviewTable
import com.poulastaa.kyoku.data.model.database.table.SongTable
import com.poulastaa.kyoku.data.model.screens.home.HomeUiSavedAlbumPrev
import com.poulastaa.kyoku.data.model.screens.home.HomeUiSongPrev
import com.poulastaa.kyoku.data.model.screens.library.Artist
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSong(song: SongTable): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylist(playlist: PlaylistTable): Long

    @Transaction
    @Query(
        "SELECT songtable.id , songtable.coverimage, songtable.title, songtable.artist," +
                " PlaylistTable.name " +
                "FROM songtable JOIN SongPlaylistRelationTable ON songtable.id = SongPlaylistRelationTable.songId " +
                "JOIN PlaylistTable ON SongPlaylistRelationTable.playlistId = PlaylistTable.id"
    )
    fun getAllPlaylist(): Flow<List<PlaylistWithSongs>>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSongPlaylistRelation(data: SongPlaylistRelationTable)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertIntoSongPrev(data: SongPreviewTable): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertIntoFevArtistMixPrev(data: FevArtistsMixPreviewTable)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertIntoAlbumPrev(data: AlbumPrevTable): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertIntoAlbumPrevSongRelationTable(data: AlbumPreviewSongRelationTable)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertIntoArtist(data: ArtistPrevTable): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertIntoArtistPrevSongRelationTable(data: ArtistPreviewSongRelation)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertIntoDailyMixPrevTable(data: DailyMixPrevTable)

    @Query("select * from AlbumPrevTable limit 1") // fetching all entry is un-necessary
    suspend fun checkIfNewUser(): List<AlbumPrevTable> // could had any other table related to homeResponse

    @Transaction
    @Query("select * from fevartistsmixpreviewtable")
    fun readFevArtistPrev(): Flow<List<FevArtistsMixPreviewTable>>

    @Transaction
    @Query(
        """select SongPreviewTable.id ,SongPreviewTable.title , SongPreviewTable.artist ,  SongPreviewTable.coverImage ,  AlbumPrevTable.name from SongPreviewTable 
            join albumpreviewsongrelationtable on albumpreviewsongrelationtable.songId = SongPreviewTable.id
            join AlbumPrevTable on AlbumPrevTable.id = albumpreviewsongrelationtable.albumId
            where AlbumPrevTable.id in ( 
                select albumId from albumpreviewsongrelationtable
            ) order by AlbumPrevTable.id"""
    )
    fun readAllAlbumPrev(): Flow<List<AlbumPrevResult>>


    @Transaction
    @Query(
        """
        select SongPreviewTable.id ,SongPreviewTable.title ,  SongPreviewTable.coverImage , ArtistPrevTable.name , ArtistPrevTable.imageUrl  from SongPreviewTable
            join ArtistPreviewSongRelation on ArtistPreviewSongRelation.songId = SongPreviewTable.id
            join ArtistPrevTable on ArtistPrevTable.id = ArtistPreviewSongRelation.artistId
            where ArtistPrevTable.id in (
                select artistId from ArtistPrevTable
        ) order by ArtistPrevTable.id
    """
    )
    fun readAllArtistPrev(): Flow<List<ArtistPrevResult>>

    @Transaction
    @Query(
        """
        select PlaylistTable.id , PlaylistTable.name , SongTable.coverImage  from PlaylistTable
        join SongPlaylistRelationTable on SongPlaylistRelationTable.playlistId = PlaylistTable.id
        join SongTable on SongTable.id = SongPlaylistRelationTable.songId
        where PlaylistTable.id in (
            select playlistId from SongPlaylistRelationTable
        ) order by PlaylistTable.points desc
    """
    )
    fun readPreviewPlaylist(): Flow<List<PlaylistPrevResult>>

    @Transaction
    @Query(
        """
        select songpreviewtable.id , songpreviewtable.title , songpreviewtable.artist , songpreviewtable.coverImage from songpreviewtable
        join RecentlyPlayedPrevTable on RecentlyPlayedPrevTable.songId = songpreviewtable.id
        where RecentlyPlayedPrevTable.songId in (
            select songId from RecentlyPlayedPrevTable order by id desc
        )
    """
    )
    fun redRecentlyPlayed(): Flow<List<HomeUiSongPrev>>

    @Query("select count(*) from FavouriteTable")
    suspend fun readFavouritePrev(): Long

    @Transaction
    @Query(
        """
        select SongTable.coverImage  , SongTable.album  from SongTable
        join SongAlbumRelationTable on SongAlbumRelationTable.songId = SongTable.id
        join AlbumTable on AlbumTable.id = SongAlbumRelationTable.albumId
        where SongAlbumRelationTable.albumId in (
            select id from AlbumTable
        ) group by AlbumTable.name order by AlbumTable.points desc limit 2
    """
    )
    fun radSavedAlbumPrev(): Flow<List<HomeUiSavedAlbumPrev>>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIntoFavourite(data: FavouriteTable)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIntoAlbum(data: AlbumTable): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertIntoSongAlbumRelationTable(data: SongAlbumRelationTable)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertIntoRecentlyPlayedPrevTable(data: RecentlyPlayedPrevTable)

    @Transaction
    @Query("select * from ArtistPrevTable")
    fun readAllArtist(): Flow<List<Artist>>

    @Query(
        """
        select id from PinnedTable where PinnedTable.playlistId = (
            select id from PlaylistTable where name = :name
        )
    """
    )
    suspend fun checkIfPlaylistIsPinned(name: String): Long?

    @Query(
        """
        select id from PinnedTable where PinnedTable.artistId = (
            select id from ArtistPrevTable where name = :name
        )
    """
    )
    suspend fun checkIfArtistPinned(name: String): Long?

    @Transaction
    @Query("select id from PlaylistTable where name = :name")
    suspend fun getIdOfPlaylist(name: String): Long?

    @Transaction
    @Query("select id from ArtistPrevTable where name = :name")
    suspend fun getIdOfArtist(name: String): Long?

    @Transaction
    @Query("select id from AlbumTable where name = :name")
    suspend fun getIdOfAlbum(name: String): Long?

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToPinnedTable(data: PinnedTable)

    @Transaction
    @Query("delete from PinnedTable where playlistId = :id")
    suspend fun removePlaylistIdFromPinnedTable(id: Long)

    @Transaction
    @Query("delete from PinnedTable where artistId = :id")
    suspend fun removeArtistIdFromPinnedTable(id: Long)

    @Transaction
    @Query("delete from PinnedTable where albumId = :id")
    suspend fun removeAlbumIdFromPinnedTable(id: Long)

    @Transaction
    @Query(
        """
        select PlaylistTable.id , PlaylistTable.name , SongTable.coverImage  from PlaylistTable
        join SongPlaylistRelationTable on SongPlaylistRelationTable.playlistId = PlaylistTable.id
        join SongTable on SongTable.id = SongPlaylistRelationTable.songId
        where PlaylistTable.id in (
            select playlistId from PinnedTable
        ) order by PlaylistTable.points desc
    """
    )
    fun readPinnedPlaylist(): Flow<List<PlaylistPrevResult>>

    @Transaction
    @Query("delete from PlaylistTable where id = :id")
    fun removePlaylist(id: Long)

    @Transaction
    @Query("delete from AlbumTable where id = :id")
    fun removeAlbum(id: Long)

    @Transaction
    @Query("delete from ArtistPrevTable where id = :id")
    fun removeArtist(id: Long)

    @Transaction
    @Query(
        """
        select ArtistPrevTable.id , ArtistPrevTable.name , ArtistPrevTable.imageUrl 
        from ArtistPrevTable 
        join PinnedTable on ArtistPrevTable.id = PinnedTable.artistId
        where PinnedTable.artistId
    """
    )
    fun readPinnedArtist(): Flow<List<Artist>>
}















