package com.poulastaa.routes.auth.common


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.GoogleUserSession
import com.poulastaa.data.model.Payload
import com.poulastaa.data.model.auth.req.GoogleAuthReq
import com.poulastaa.data.model.auth.stat.UserCreationStatus
import com.poulastaa.domain.repository.UserServiceRepository
import com.poulastaa.utils.Constants.ISSUER
import com.poulastaa.utils.toPayload
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.handleGoogleLogin(
    googleAuthReq: GoogleAuthReq,
    userService: UserServiceRepository,
) {
    val result = googleAuthReq.verifyTokenId()

    if (result == null) {
        call.respondRedirect(EndPoints.UnAuthorised.route)

        return
    }

    try {
        val payload = result.toPayload()

        val userCreationResponse = userService.createUser(
            userName = payload.userName,
            sub = payload.sub,
            email = payload.email,
            pictureUrl = payload.pictureUrl
        )

        when (userCreationResponse.status) {
            UserCreationStatus.CREATED -> {
                payload.setSession(call)

                call.respond(
                    message = userCreationResponse,
                    status = HttpStatusCode.OK
                )
            }

            UserCreationStatus.CONFLICT -> {
                payload.setSession(call)

                call.respond(
                    message = userCreationResponse,
                    status = HttpStatusCode.OK
                )
            }

            UserCreationStatus.SOMETHING_WENT_WRONG -> {
                call.respond(
                    message = userCreationResponse,
                    status = HttpStatusCode.InternalServerError
                )
            }

            UserCreationStatus.EMAIL_NOT_VALID -> Unit // this will not occur
        }
    } catch (e: Exception) {
        e.printStackTrace()
        call.respondRedirect(EndPoints.UnAuthorised.route)
    }
}

private fun Payload.setSession(call: ApplicationCall) {
    call.sessions.set(
        GoogleUserSession(
            email = this.email,
            userName = this.userName
        )
    )
}

private fun GoogleAuthReq.verifyTokenId(): GoogleIdToken? = try {
    GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
        .setAudience(listOf(System.getenv("clientId")))
        .setIssuer(ISSUER)
        .build()
        .verify(this.tokenId)
} catch (e: Exception) {
    null
}
