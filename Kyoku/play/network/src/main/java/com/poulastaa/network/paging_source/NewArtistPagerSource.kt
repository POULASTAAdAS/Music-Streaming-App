package com.poulastaa.network.paging_source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.poulastaa.core.data.network.get
import com.poulastaa.core.domain.EndPoints
import com.poulastaa.core.domain.model.Artist
import com.poulastaa.core.domain.model.ArtistPagingType
import com.poulastaa.core.domain.utils.DataError
import com.poulastaa.core.domain.utils.NoInternetException
import com.poulastaa.core.domain.utils.OtherRemoteException
import com.poulastaa.core.domain.utils.Result
import com.poulastaa.core.domain.utils.map
import com.poulastaa.network.mapper.toArtist
import com.poulastaa.network.mapper.toArtistPagingTypeDto
import com.poulastaa.network.model.ArtistPagingTypeDto
import com.poulastaa.network.model.PagingArtistResDto
import okhttp3.OkHttpClient
import javax.inject.Inject

class NewArtistPagerSource @Inject constructor(
    private val client: OkHttpClient,
    private val gson: Gson,
) : PagingSource<Int, Artist>() {
    private var query: String = ""
    private var type: ArtistPagingTypeDto = ArtistPagingTypeDto.ALL

    fun init(
        query: String,
        type: ArtistPagingType,
    ) {
        this.query = query
        this.type = type.toArtistPagingTypeDto()
    }

    override fun getRefreshKey(state: PagingState<Int, Artist>): Int? =
        state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Artist> {
        val page = params.key ?: 1

        val result = client.get<PagingArtistResDto>(
            route = EndPoints.GetArtistPaging.route,
            params = listOf(
                "page" to page.toString(),
                "size" to 15.toString(),
                "query" to query,
                "type" to type.name,
            ),
            gson = gson
        ).map {
            it.list.map { dto -> dto.toArtist() }
        }

        return when (result) {
            is Result.Error -> {
                when (result.error) {
                    DataError.Network.NO_INTERNET -> LoadResult.Error(NoInternetException)

                    else -> LoadResult.Error(OtherRemoteException)
                }
            }

            is Result.Success -> {
                LoadResult.Page(
                    data = result.data,
                    prevKey = if (page == 1) null else page.minus(1),
                    nextKey = if (result.data.isEmpty()) null else page.plus(1)
                )
            }
        }
    }
}