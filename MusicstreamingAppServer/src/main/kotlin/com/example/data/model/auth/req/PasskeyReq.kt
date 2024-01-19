package com.example.data.model.auth.req

import kotlinx.serialization.Serializable
import java.security.SecureRandom
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class PasskeyReq(
    val challenge: String = generateFidoChallenge(),
    val rp: Rp = Rp(),
    val user: User = User(),
    val pubKeyCredParams: List<PubKeyCredParams> = listOf(
        PubKeyCredParams()
    ),
    val timeout: Long = 1800000,
    val attestation: String = "none",
    val excludeCredentials: List<ExcludeCredentials> = emptyList(),
    val authenticatorSelection: AuthenticatorSelection = AuthenticatorSelection()
)

private fun generateFidoChallenge(): String {
    val secureRandom = SecureRandom()
    val challengeBytes = ByteArray(32)
    secureRandom.nextBytes(challengeBytes)
    return challengeBytes.b64Encode()
}

@Serializable
data class Rp(
    val name: String = "Kyoku",
    val id: String = "com.example.kyoku"
)

@Serializable
data class User(
    val id: String = "1",
    val name: String = "poulastaadas2@gmail.com",
    val displayName: String = "Anshu"
)

@Serializable
data class PubKeyCredParams(
    val type: String = "public-key",
    val alg: Int = -7
)

@Serializable
data class ExcludeCredentials(
    val id: String,
    val type: String
)

@Serializable
data class AuthenticatorSelection(
    val authenticatorAttachment: String = "platform",
    val requireResidentKey: Boolean = false,
    val residentKey: String = "required",
    val userVerification: String = "required"
)


@OptIn(ExperimentalEncodingApi::class)
fun ByteArray.b64Encode(): String {
    return Base64.UrlSafe.encode(this)
}