package com.poulastaa.kyoku.data.model.screens.auth.email.signup

data class EmailSignUpState(
    val email: String = "",
    val userName: String = "",
    val password: String = "",
    val conformPassword: String = "",

    val emailSupportingText: String = "",
    val userNameSupportingText: String = "",
    val passwordSupportingText: String = "",
    val conformPasswordSupportingText: String = "",

    val isEmailError: Boolean = false,
    val isUserNameError: Boolean = false,
    val isPasswordError: Boolean = false,
    val isConformPasswordError: Boolean = false,

    val isResendVerificationMailPromptVisible: Boolean = false,
    val sendVerificationMailTimer: Int = 0,
    val isResendMailEnabled: Boolean = false,

    val isPasswordVisible: Boolean = true,

    val isLoading: Boolean = false,

    val isInternetAvailable: Boolean = false
)