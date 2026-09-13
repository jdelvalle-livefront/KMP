package co.delvalle.artapp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal expect fun platformHttpClientEngine(): HttpClientEngine

private val artworkJson = Json {
    ignoreUnknownKeys = true
}

fun createHttpClient(engine: HttpClientEngine = platformHttpClientEngine()): HttpClient =
    HttpClient(engine) {
        expectSuccess = true
        install(ContentNegotiation) {
            json(artworkJson)
        }
    }
