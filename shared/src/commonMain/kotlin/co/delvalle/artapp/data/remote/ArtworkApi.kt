package co.delvalle.artapp.data.remote

import co.delvalle.artapp.data.remote.dto.ArtworkDetailResponseDto
import co.delvalle.artapp.data.remote.dto.ArtworkListResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

private const val BASE_URL = "https://openaccess-api.clevelandart.org/api"
private const val LIST_FIELDS = "id,title,creation_date,creators,images"
private const val DETAIL_FIELDS = "id,title,creation_date,creators,images,technique,description,creditline,culture"

class ArtworkApi(private val httpClient: HttpClient) {

    suspend fun getArtworks(page: Int, limit: Int = 24): ArtworkListResponseDto =
        httpClient.get("$BASE_URL/artworks/") {
            parameter("skip", (page - 1).coerceAtLeast(0) * limit)
            parameter("limit", limit)
            parameter("has_image", 1)
            parameter("fields", LIST_FIELDS)
        }.body()

    suspend fun getArtworkDetail(id: Long): ArtworkDetailResponseDto =
        httpClient.get("$BASE_URL/artworks/$id") {
            parameter("fields", DETAIL_FIELDS)
        }.body()
}
