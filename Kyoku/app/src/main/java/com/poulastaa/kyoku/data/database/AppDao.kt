package com.poulastaa.kyoku.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.poulastaa.kyoku.data.model.database.PlaylistWithSongs
import com.poulastaa.kyoku.data.model.database.table.PlaylistRelationTable
import com.poulastaa.kyoku.data.model.database.table.PlaylistTable
import com.poulastaa.kyoku.data.model.database.table.SongTable
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Transaction
    @Query(
        "SELECT songtable.id , songtable.coverimage, songtable.title, songtable.artist," +
                " PlaylistTable.name " + // , PlaylistTable.isExpanded
                "FROM songtable JOIN PlaylistRelationTable ON songtable.id = PlaylistRelationTable.songId " +
                "JOIN PlaylistTable ON PlaylistRelationTable.playlistId = PlaylistTable.id"
    )
    fun getAllPlaylist(): Flow<List<PlaylistWithSongs>>

    @Transaction
    @Query("select * from songtable")
    fun getAllSong(): Flow<List<SongTable>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSong(song: SongTable): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSongIntoPlaylist(playlist: PlaylistTable): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDataIntoPlaylistRelationTable(data: PlaylistRelationTable)
}