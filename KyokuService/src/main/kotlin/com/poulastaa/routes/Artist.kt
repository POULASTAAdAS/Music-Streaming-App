package com.poulastaa.routes

import com.poulastaa.data.model.artist.ArtistMostPopularSongReq
import com.poulastaa.data.model.artist.ArtistMostPopularSongRes
import com.poulastaa.data.model.common.EndPoints
import com.poulastaa.domain.repository.UserServiceRepository
import com.poulastaa.utils.Constants
import com.poulastaa.utils.getUserType
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.artist(
    service: UserServiceRepository
){
    authenticate(configurations = Constants.SECURITY_LIST) {
        route(EndPoints.Artist.route) {
            post {
                val req = call.receiveNullable<ArtistMostPopularSongReq>() ?: return@post call.respond(
                    message = ArtistMostPopularSongRes(),
                    status = HttpStatusCode.OK
                )

                getUserType() ?: return@post call.respond(
                    message = ArtistMostPopularSongRes(),
                    status = HttpStatusCode.OK
                )

                val response = service.getMostPopularSongOfArtist(req)

                call.respond(
                    message = response,
                    status = HttpStatusCode.OK
                )
            }
        }
    }
}