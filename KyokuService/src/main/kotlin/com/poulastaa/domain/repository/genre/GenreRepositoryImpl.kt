package com.poulastaa.domain.repository.genre

import com.poulastaa.data.model.UserType
import com.poulastaa.data.model.UserTypeHelper
import com.poulastaa.data.model.db_table.CountryGenreRelationTable
import com.poulastaa.data.model.db_table.GenreTable
import com.poulastaa.data.model.db_table.user_genre.EmailUserGenreRelationTable
import com.poulastaa.data.model.db_table.user_genre.GoogleUserGenreRelationTable
import com.poulastaa.data.model.db_table.user_genre.PasskeyUserGenreRelationTable
import com.poulastaa.data.model.setup.genre.GenreResponseStatus
import com.poulastaa.data.model.setup.genre.StoreGenreResponse
import com.poulastaa.data.model.setup.genre.SuggestGenreReq
import com.poulastaa.data.model.setup.genre.SuggestGenreResponse
import com.poulastaa.data.repository.genre.GenreRepository
import com.poulastaa.domain.dao.CountryGenreRelation
import com.poulastaa.domain.dao.Genre
import com.poulastaa.domain.dao.user_genre.EmailUserGenreRelation
import com.poulastaa.domain.dao.user_genre.GoogleUserGenreRelation
import com.poulastaa.domain.dao.user_genre.PasskeyUserGenreRelation
import com.poulastaa.plugins.dbQuery
import org.jetbrains.exposed.sql.and

class GenreRepositoryImpl : GenreRepository {
    private val appName = listOf(
        "Kyokui.Com",
        "Kyoku.Com",
        "Kyoku",
        "Kyoku.To"
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
                it.id.value to it.name
            }.removeDuplicateGenre(
                oldList = req.alreadySendGenreList,
                isSelectRequest = req.isSelectReq
            )
        }

        return genreMap.toSuggestGenreResponse()
    }

    override suspend fun storeGenre(
        helper: UserTypeHelper,
        genreNameList: List<String>
    ): StoreGenreResponse {
        val genreIdList = dbQuery {
            Genre.find {
                GenreTable.name inList genreNameList
            }.map {
                it.id.value
            }
        }

        when (helper.userType) {
            UserType.GOOGLE_USER -> genreIdList.storeGenreForGoogleUser(id = helper.id.toLong())

            UserType.EMAIL_USER -> genreIdList.storeGenreForEmailUser(id = helper.id.toLong())

            UserType.PASSKEY_USER -> genreIdList.storeGenreForPasskeyUser(id = helper.id.toLong())
        }

        return StoreGenreResponse(
            status = GenreResponseStatus.SUCCESS
        )
    }

    private fun List<Pair<Int, String>>.removeDuplicateGenre(
        oldList: List<String>,
        isSelectRequest: Boolean
    ): Map<Int, String> {
        var filteredMap = this.filterNot { (_, u) -> oldList.contains(u.trim()) }

        filteredMap = if (!isSelectRequest) filteredMap.filterNot { (_, v) -> appName.contains(v) } else filteredMap

        return filteredMap.sortedBy { it.first }.take(if (isSelectRequest) 3 else 6).toMap()  // works like paging
    }

    private fun Map<Int, String>.toSuggestGenreResponse(): SuggestGenreResponse = SuggestGenreResponse(
        status = GenreResponseStatus.SUCCESS,
        genreList = this.values.map { it.trim() }.toList()
    )

    private suspend fun Iterable<Int>.storeGenreForEmailUser(id: Long) {
        dbQuery {
            this.forEach {
                val found = EmailUserGenreRelation.find {
                    EmailUserGenreRelationTable.genreId eq it and (EmailUserGenreRelationTable.userId eq id)
                }.firstOrNull()

                if (found == null) EmailUserGenreRelation.new {
                    this.userId = id
                    this.genreId = it
                }
            }
        }
    }

    private suspend fun Iterable<Int>.storeGenreForGoogleUser(id: Long) {
        dbQuery {
            this.forEach {
                val found = GoogleUserGenreRelation.find {
                    GoogleUserGenreRelationTable.genreId eq it and (GoogleUserGenreRelationTable.userId eq id)
                }.firstOrNull()

                if (found == null) GoogleUserGenreRelation.new {
                    this.userId = id
                    this.genreId = it
                }
            }
        }
    }

    private suspend fun Iterable<Int>.storeGenreForPasskeyUser(id: Long) {
        dbQuery {
            this.forEach {
                val found = PasskeyUserGenreRelation.find {
                    PasskeyUserGenreRelationTable.genreId eq it and (PasskeyUserGenreRelationTable.userId eq id)
                }.firstOrNull()

                if (found == null) PasskeyUserGenreRelation.new {
                    this.userId = id
                    this.genreId = it
                }
            }
        }
    }
}