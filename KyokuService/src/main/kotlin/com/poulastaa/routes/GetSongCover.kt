package com.poulastaa.routes

import com.poulastaa.data.model.EndPoints
import com.poulastaa.domain.repository.UserServiceRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getSongCover(
    service: UserServiceRepository
) {
    authenticate("jwt-auth", "google-auth", "passkey-auth") {
        route(EndPoints.CoverImage.route) {
            get {
                val coverImage = call.parameters["coverImage"] ?: return@get

                val file = service.getSongCover(coverImage)

                if (file != null) {
                    call.respondFile(file)

                    return@get
                }

                call.respond(
                    message = "no cover photo found",
                    status = HttpStatusCode.OK
                )
            }
        }
    }
}