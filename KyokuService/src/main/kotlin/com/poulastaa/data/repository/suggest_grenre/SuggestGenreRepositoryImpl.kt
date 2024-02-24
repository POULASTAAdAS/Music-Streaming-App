package com.poulastaa.data.repository.suggest_grenre

import com.poulastaa.data.model.db_table.CountryGenreRelationTable
import com.poulastaa.data.model.db_table.GenreTable
import com.poulastaa.data.model.setup.suggest_genre.SuggestGenreReq
import com.poulastaa.data.model.setup.suggest_genre.SuggestGenreResponse
import com.poulastaa.data.model.setup.suggest_genre.SuggestGenreResponseStatus
import com.poulastaa.domain.dao.CountryGenreRelation
import com.poulastaa.domain.dao.Genre
import com.poulastaa.domain.repository.suggest_genre.SuggestGenreRepository
import com.poulastaa.plugins.dbQuery

class SuggestGenreRepositoryImpl : SuggestGenreRepository {

    private fun List<Pair<Int, String>>.removeDuplicateGenre(
        oldList: List<String>,
        isSelectRequest: Boolean
    ): Map<Int, String> {
        val filteredMap = this.filterNot { (_, u) -> oldList.contains(u.trim()) }
        return filteredMap.sortedBy { it.first }.take(if (isSelectRequest) 3 else 15).toMap()  // works like paging
    }

    private fun List<Pair<Int, String>>.removeDuplicateArtist(): List<String> {
        return this.associateBy({ it.first }, { it.second }).values.toList()
    }


    private fun Map<Int, String>.toSuggestGenreResponse(): SuggestGenreResponse = SuggestGenreResponse(
        status = SuggestGenreResponseStatus.SUCCESS,
        genreList = this.values.map { it.trim() }.toList()
    )

    override suspend fun suggestGenre(
        req: SuggestGenreReq,
        countryId: Int
    ): SuggestGenreResponse {
        val genreIdList = dbQuery {
            CountryGenreRelation.find {
                CountryGenreRelationTable.countryId eq countryId
            }.map { it.genreId }
        }


        val genreMap = dbQuery {
            Genre.find {
                GenreTable.id inList genreIdList
            }.map {
                it.id.value to it.genre
            }.removeDuplicateGenre(
                oldList = req.alreadySendGenreList ?: emptyList(),
                isSelectRequest = req.isSelectReq
            )
        }


//        val artistUrlList = dbQuery {
//            Artist.find {
//                ArtistTable.genre inList genreMap.keys
//            }.map {
//                it.genre to it.profilePicUrl
//            }.removeDuplicateArtist()
//        }


        return genreMap.toSuggestGenreResponse()
    }
}