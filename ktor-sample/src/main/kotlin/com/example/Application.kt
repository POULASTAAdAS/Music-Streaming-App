package com.example


import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

fun main() {
    runBlocking {
        val client = HttpClient()
        client.download("http://0.0.0.0:8080/song", File("Tum Hi Ho.mp3"))
    }
}

suspend fun HttpClient.download(url: String, outFile: File, chunkSize: Int = 10) {
    val length = head(url).headers[HttpHeaders.ContentLength]?.toLong() as Long
    val lastByte = length - 1

    var start = outFile.length()
    val output = withContext(Dispatchers.IO) {
        FileOutputStream(outFile, true)
    }

    while (true) {
        delay(3000)
        val end = min(start + chunkSize - 1, lastByte)
        val data = get(url) {
            header("Range", "bytes=${start}-${end}")
        }.body<ByteArray>()
        withContext(Dispatchers.IO) {
            output.write(data)
        }
        if (end >= lastByte) break
        start += chunkSize
    }
}