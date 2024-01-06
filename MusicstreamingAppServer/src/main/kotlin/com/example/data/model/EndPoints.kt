package com.example.data.model

sealed class EndPoints(val route: String) {
    data object Auth : EndPoints(route = "/api/auth")
    data object VerifyEmail : EndPoints(route = "/api/auth/verifyEmail")
    data object EmailVerificationCheck : EndPoints(route = "/api/auth/emailVerificationCheck")
    data object ForgotPassword : EndPoints(route = "/api/auth/forgotPassword")
    data object ResetPassword : EndPoints(route = "/api/auth/resetPassword")

    data object PhoneSignUp : EndPoints(route = "/api/auth/phoneSignUp")
}