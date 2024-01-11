package com.example.data.repository.user_db

import com.example.data.model.auth.res.EmailLoginResponse
import com.example.data.model.auth.stat.*
import com.example.data.model.database_table.EmailAuthUserTable
import com.example.domain.dao.EmailAuthUser
import com.example.domain.repository.user_db.EmailAuthUserRepository
import com.example.plugins.dbQuery
import com.example.util.constructProfileUrl
import java.io.File

class EmailAuthUserRepositoryImpl : EmailAuthUserRepository {
    private suspend fun findUser(email: String) = dbQuery {
        EmailAuthUser.find {
            EmailAuthUserTable.email eq email
        }.firstOrNull()
    }

    override suspend fun createUser(
        userName: String,
        email: String,
        password: String,
        refreshToken: String
    ): UserCreationStatus = try {
        val user = findUser(email)

        if (user == null) {
            dbQuery {
                EmailAuthUser.new {
                    this.userName = userName
                    this.email = email
                    this.password = password
                    this.refreshToken = refreshToken
                }
            }.let {
                UserCreationStatus.CREATED
            }
        } else {
            UserCreationStatus.CONFLICT
        }
    } catch (e: Exception) {
        UserCreationStatus.SOMETHING_WENT_WRONG
    }

    override suspend fun updateVerificationStatus(
        email: String
    ): UpdateEmailVerificationStatus {
        return try {
            val user = findUser(email) ?: return UpdateEmailVerificationStatus.USER_NOT_FOUND

            if (!user.emailVerified) {
                dbQuery {
                    user.emailVerified = true // update query
                }

                UpdateEmailVerificationStatus.VERIFIED
            } else UpdateEmailVerificationStatus.VERIFIED
        } catch (e: Exception) {
            UpdateEmailVerificationStatus.SOMETHING_WENT_WRONG
        }
    }

    // this will be hit repeated time while signing up
    override suspend fun checkEmailVerification(email: String): EmailVerificationStatus {
        return try {
            val user = findUser(email) ?: return EmailVerificationStatus.USER_NOT_FOUND

            return if (user.emailVerified) EmailVerificationStatus.VERIFIED
            else EmailVerificationStatus.UN_VERIFIED
        } catch (e: Exception) {
            EmailVerificationStatus.SOMETHING_WENT_WRONG
        }
    }


    override suspend fun loginUser(
        email: String,
        password: String,
        accessToken: String,
        refreshToken: String
    ): EmailLoginResponse {
        try {
            val user = findUser(email)
                ?: return EmailLoginResponse(
                    status = EmailLoginStatus.USER_DOES_NOT_EXISTS
                )

            if (user.password != password) {
                return EmailLoginResponse(
                    status = EmailLoginStatus.PASSWORD_DOES_NOT_MATCH
                )
            }

            if (!user.emailVerified) {
                return EmailLoginResponse(
                    status = EmailLoginStatus.EMAIL_NOT_VERIFIED
                )
            }

            return EmailLoginResponse(
                status = EmailLoginStatus.USER_PASS_MATCHED,
                userName = user.userName,
                accessToken = accessToken,
                refreshToken = refreshToken,
                profilePic = constructProfileUrl(),
                data = emptyList()
            )
        } catch (e: Exception) {
            return EmailLoginResponse(
                status = EmailLoginStatus.SOMETHING_WENT_WRONG
            )
        }
    }

    override suspend fun checkIfUSerExistsThenSendForgotPasswordMail(email: String): SendVerificationMailStatus {
        return try {
            findUser(email) ?: SendVerificationMailStatus.USER_NOT_FOUND
            SendVerificationMailStatus.USER_EXISTS
        } catch (e: Exception) {
            SendVerificationMailStatus.SOMETHING_WENT_WRONG
        }
    }

    override suspend fun resetPassword(email: String, password: String): PasswordResetStatus {
        return try {
            val user = findUser(email) ?: return PasswordResetStatus.USER_NOT_FOUND

            if (user.password == password) return PasswordResetStatus.SAME_AS_OLD_PASSWORD

            dbQuery {
                user.password = password // update query
            }

            PasswordResetStatus.SUCCESSFUL
        } catch (e: Exception) {
            PasswordResetStatus.SOMETHING_WENT_WRONG
        }
    }

    override suspend fun getUserProfilePic(email: String): File? {
        // todo check is email verified
        return try {
            dbQuery {
                val user = EmailAuthUser.find {
                    EmailAuthUserTable.email eq email
                }.first()

                File(user.profilePic)
            }
        } catch (e: Exception) {
            null
        }
    }
}