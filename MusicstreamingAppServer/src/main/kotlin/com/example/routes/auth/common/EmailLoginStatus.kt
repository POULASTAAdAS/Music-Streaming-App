package com.example.routes.auth.common

enum class EmailLoginStatus {
    USER_PASS_MATCHED,
    PASSWORD_DOES_NOT_MATCH,
    SOMETHING_WENT_WRONG,
    EMAIL_NOT_VERIFIED,
    USER_DOES_NOT_EXISTS
}