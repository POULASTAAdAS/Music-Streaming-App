package com.example.routes.auth

import com.example.data.model.EndPoints
import com.example.data.model.SendForgotPasswordMail
import com.example.domain.repository.user.EmailAuthUserRepository
import com.example.routes.auth.common.SendVerificationMailStatus
import com.example.routes.auth.common.generateJWTTokenWithClaimMailId
import com.example.util.Constants.BASE_URL
import com.example.util.sendMail
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.forgotPassword(
    emailAuthUser: EmailAuthUserRepository
) {
    route(EndPoints.ForgotPassword.route) {
        get {
            val email = call.parameters["email"]

            if (email == null) {
                call.respond(
                    message = "invalid request",
                    status = HttpStatusCode.Forbidden
                )

                return@get
            }

            val result = emailAuthUser.sendForgotPasswordMail(email = email)

            when (result.status) {
                SendVerificationMailStatus.USER_EXISTS -> {
                    val content = (
                            (
                                    "<html>"
                                            + "<body>"
                                            + "<h1>Do not share this mail</h1>"
                                            + "<p>Click the following link to reset your password:</p>"
                                            + "<a href=\"${BASE_URL + EndPoints.ResetPassword.route}?token=" +
                                            email.trim().generateJWTTokenWithClaimMailId(call.application.environment)
                                    ) + "\">Reset Password</a>"
                                    + "</body>"
                                    + "</html>"
                            )

                    if (
                        sendMail(
                            to = email,
                            subject = "Reset password mail",
                            content = content
                        )
                    ) call.respond(
                        message = result,
                        status = HttpStatusCode.OK
                    ) else call.respond(
                        message =
                        SendForgotPasswordMail(
                            status = SendVerificationMailStatus.SOMETHING_WENT_WRONG
                        ),
                        status = HttpStatusCode.Forbidden
                    )
                }

                SendVerificationMailStatus.USER_NOT_FOUND -> {
                    call.respond(
                        message = result,
                        status = HttpStatusCode.Forbidden
                    )
                }

                SendVerificationMailStatus.SOMETHING_WENT_WRONG -> {
                    call.respond(
                        message = result,
                        status = HttpStatusCode.Forbidden
                    )
                }
            }
        }
    }
}