package com.example.util

object Constants {
    const val MASTER_PLAYLIST_ROOT_DIR = "E:/songdb/master/"
    const val COVER_IMAGE_ROOT_DIR = "E:/songdb/coverImage/"
    const val PROFILE_PIC_ROOT_DIR = "E:/songdb/userProfilePic"

    const val BASE_URL = "https://e387-103-192-116-166.ngrok-free.app"

    const val ISSUER = "https://accounts.google.com"

    const val AUTH_TYPE_GOOGLE = "AUTH_TYPE_GOOGLE"
    const val AUTH_TYPE_EMAIL_SIGN_UP = "AUTH_TYPE_EMAIL_SIGN_UP"
    const val AUTH_TYPE_EMAIL_LOG_IN = "AUTH_TYPE_EMAIL_LOG_IN"

    const val SMS_EMAIL_GOOGLE_SMTP_HOST = "smtp.gmail.com"
    const val SMS_EMAIL_PORT = "587"

    const val ACCESS_TOKEN_CLAIM_KEY = "emailAccess"
    const val REFRESH_TOKEN_CLAIM_KEY = "emailRefresh"
    const val VERIFICATION_MAIL_TOKEN_CLAIM_KEY = "emailVerify"
    const val FORGOT_PASSWORD_MAIL_TOKEN_CLAIM_KEY = "forgotPassword"

    const val ACCESS_TOKEN_DEFAULT_TIME = 240000L // todo increase
    const val REFRESH_TOKEN_DEFAULT_TIME = 5184000000L // 60 days
    const val VERIFICATION_MAIL_TOKEN_TIME = 240000L // 4 minute
    const val FORGOT_PASSWORD_MAIL_TOKEN_TIME = 240000L // 4 minute
}