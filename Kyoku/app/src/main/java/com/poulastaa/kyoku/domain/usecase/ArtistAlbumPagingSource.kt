package com.poulastaa.kyoku.domain.usecase

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.poulastaa.kyoku.data.model.api.service.artist.ArtistAlbum
import com.poulastaa.kyoku.data.model.api.service.artist.ArtistPageReq
import com.poulastaa.kyoku.domain.repository.ServiceRepository
import javax.inject.Inject

class ArtistAlbumPagingSource @Inject constructor(
    private val api: ServiceRepository
) : PagingSource<Int, ArtistAlbum>() {
    private lateinit var name: String

    fun load(name: String) {
        this.name = name
    }

    override fun getRefreshKey(state: PagingState<Int, ArtistAlbum>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArtistAlbum> {
        val page = params.key ?: 1

        val response = api.getArtistAlbumAsPage(
            req = ArtistPageReq(
                page = page,
                pageSize = 20,
                name = this.name
            )
        )

        return try {
            LoadResult.Page(
                data = response,
                prevKey = if (page == 1) null else page.minus(1),
                nextKey = if (response.isEmpty()) null
                else page.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}