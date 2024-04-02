package com.poulastaa.routes

import com.poulastaa.data.model.common.EndPoints
import com.poulastaa.data.model.home.AlbumPreview
import com.poulastaa.domain.repository.UserServiceRepository
import com.poulastaa.utils.Constants
import com.poulastaa.utils.getUserType
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.album(
    service: UserServiceRepository
) {
    authenticate(configurations = Constants.SECURITY_LIST) {
        route(EndPoints.Album.route) {
            post {
                val req = call.parameters["id"] ?: return@post call.respond(
                    message = AlbumPreview(),
                    status = HttpStatusCode.OK
                )

                getUserType() ?: return@post call.respond(
                    message = AlbumPreview(),
                    status = HttpStatusCode.OK
                )

                val response = service.getAlbum(req.toLong())

                call.respond(
                    message = response,
                    status = HttpStatusCode.OK
                )
            }
        }
    }
}