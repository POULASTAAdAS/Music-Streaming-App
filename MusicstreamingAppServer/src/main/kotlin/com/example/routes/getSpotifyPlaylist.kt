package com.example.routes

import com.example.data.model.EndPoints
import com.example.data.model.api.stat.HandleSpotifyPlaylistStatus
import com.example.data.model.api.res.SpotifyPlaylistResponse
import com.example.data.model.SpotifySong
import com.example.domain.repository.song_db.SongRepository
import com.example.util.getAlbum
import com.example.util.removeAlbum
import com.example.util.songDownloaderApi.makeApiCallOnNotFoundSpotifySongs
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.serialization.json.*


fun Route.getSpotifyPlaylist(
    songRepository: SongRepository,
) {
    authenticate("jwt-auth", "google-auth") {
        route(EndPoints.GetSpotifyPlaylistSong.route) {
            post {
                val playlistId = call.parameters["playlistId"]

                if (playlistId == null) {
                    call.respondRedirect(EndPoints.UnAuthorised.route)

                    return@post
                }

                val playlist = getPlaylist(playlistId)

                if (playlist == null) {
                    call.respond(
                        message = SpotifyPlaylistResponse(),
                        status = HttpStatusCode.InternalServerError
                    )

                    return@post
                }

                getSongNameAndAlbum(playlist, songRepository) {
                    call.respond(
                        message = it,
                        status = HttpStatusCode.OK
                    )
                }
            }
        }
    }
}

suspend fun getSongNameAndAlbum(
    jsonObject: String,
    songRepository: SongRepository,
    postResponse: suspend (song: SpotifyPlaylistResponse) -> Unit
) {
    val list = ArrayList<SpotifySong>()

    try {
        val jsonElement = Json.parseToJsonElement(jsonObject)

        val itemsArray = jsonElement.jsonObject["items"]?.jsonArray

        itemsArray?.forEach { item ->
            val trackJson = item?.jsonObject?.get("track") // some items don't exist this check is important

            if (trackJson != null && trackJson is JsonObject) {

                val spotifySong = SpotifySong()

                item.jsonObject["track"]?.jsonObject?.get("name")
                    ?.jsonPrimitive?.contentOrNull?.let { name ->
                        if (name.isNotBlank())
                            spotifySong.title = name.removeAlbum()
                    }

                item.jsonObject["track"]?.jsonObject?.get("album")
                    ?.jsonObject?.get("name")?.jsonPrimitive?.contentOrNull?.let {
                        if (it.isNotBlank())
                            spotifySong.album = it.getAlbum()
                    }

                if (spotifySong.album != null || spotifySong.title != null)
                    list.add(spotifySong)
            }
        }
    } catch (e: Exception) {
        println("error: $e")
    }

    val result = songRepository.handleSpotifyPlaylist(list)

    when (result.status) {
        HandleSpotifyPlaylistStatus.SUCCESS -> {
            postResponse(
                result.spotifyPlaylistResponse
            )

            result.spotifySongDownloaderApiReq.makeApiCallOnNotFoundSpotifySongs()
        }

        HandleSpotifyPlaylistStatus.FAILURE -> Unit
    }
}


private suspend fun getPlaylist(playlistId: String): String? {
    val accessToken = getSpotifyPlaylistAccessToken() ?: return null

    val client = HttpClient()

    val result = client.get("https://api.spotify.com/v1/playlists/$playlistId/tracks") {
        header("Authorization", "Bearer $accessToken")
    }

    client.close()
    return result.bodyAsText()
}


@OptIn(InternalAPI::class)
private suspend fun getSpotifyPlaylistAccessToken(
    clientId: String = System.getenv("spotifyClientId"),
    clientSecret: String = System.getenv("spotifyClientSecret"),
): String? {
    val tokenEndpoint = "https://accounts.spotify.com/api/token"
    val formData = Parameters.build {
        append("grant_type", "client_credentials")
    }

    val client = HttpClient()
    val response: HttpResponse = client.post(tokenEndpoint) {
        body = FormDataContent(formData)
        headers.append("Authorization", "Basic " + "$clientId:$clientSecret".encodeBase64())
    }

    val responseBody = response.bodyAsText()
    val accessToken = Json.parseToJsonElement(responseBody).jsonObject["access_token"]?.jsonPrimitive?.content

    client.close()

    return accessToken
}

private fun String.encodeBase64(): String {
    return java.util.Base64.getEncoder().encodeToString(this.toByteArray())
}
