package com.poulastaa.kyoku.utils

import com.poulastaa.kyoku.data.model.BDateFroMaterHelper
import com.poulastaa.kyoku.data.model.BDateFroMaterHelperStatus
import com.poulastaa.kyoku.data.model.api.service.setup.SetBDateReq
import java.net.CookieManager
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Base64
import kotlin.random.Random

fun Char.isUserName(): Boolean =
    if (this == '_') true
    else this.isLetterOrDigit()

fun String.b64Decode(): ByteArray = Base64.getUrlDecoder().decode(this)

fun CookieManager.extractTokenOrCookie(): String =
    this.cookieStore.cookies[0].toString()

fun String.validateSpotifyLink(): Boolean {
    return (this.startsWith("https://open.spotify.com/playlist/") && this.contains("?si="))
}

fun String.toSpotifyPlaylistId(): String =
    this.removePrefix("https://open.spotify.com/playlist/").split("?si=")[0]

fun generatePlaylistName(): String = "Playlist #${Random.nextLong(1, 10_000)}"

fun Long.toDate(): BDateFroMaterHelper {
    val bDate = BDateFroMaterHelper(
        date = "",
        status = BDateFroMaterHelperStatus.TO_OLD
    )
    val currentYear = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(System.currentTimeMillis()),
        ZoneId.systemDefault()
    )

    val date = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(this),
        ZoneId.systemDefault()
    )

    val st = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    return if (date.year < 1960) {
        bDate.copy(
            date = st,
            status = BDateFroMaterHelperStatus.TO_OLD
        )
    } else if (date.year > currentYear.year + 1) {
        bDate.copy(
            date = st,
            status = BDateFroMaterHelperStatus.FROM_FUTURE
        )
    } else if (date.year > (currentYear.year - 7)) {
        bDate.copy(
            status = BDateFroMaterHelperStatus.TO_YOUNG,
            date = st
        )
    } else {
        bDate.copy(
            date = st,
            status = BDateFroMaterHelperStatus.OK
        )
    }
}

fun Long.toSetBDateReq(email: String) = SetBDateReq(
    date = this,
    email = email
)