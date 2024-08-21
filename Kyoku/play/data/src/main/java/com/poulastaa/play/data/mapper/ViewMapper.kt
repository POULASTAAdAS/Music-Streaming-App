package com.poulastaa.play.data.mapper

import com.poulastaa.core.ViewData
import com.poulastaa.core.domain.AlbumData
import com.poulastaa.core.domain.model.PlaylistData
import com.poulastaa.core.domain.model.PlaylistSong
import com.poulastaa.core.domain.model.Song


fun Song.toPlaylistSong() = PlaylistSong(
    id = this.id,
    coverImage = this.coverImage,
    title = this.title,
    artist = this.artistName
)

@JvmName("playlistToViewData")
fun PlaylistData.toViewData() = ViewData(
    id = this.id,
    name = this.name,
    listOfSong = this.listOfSong.map { it.toPlaylistSong() }
)

@JvmName("albumToViewData")
fun AlbumData.toViewData() = ViewData(
    id = this.id,
    name = this.name,
    listOfSong = this.listOfSong.map { it.toPlaylistSong() }
)