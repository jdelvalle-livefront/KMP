package co.delvalle.artapp.data

import co.delvalle.artapp.data.remote.ArtworkApi
import co.delvalle.artapp.data.remote.createHttpClient
import co.delvalle.artapp.domain.model.ArtworkError
import co.delvalle.artapp.domain.model.ArtworkResult
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

private val JSON_HEADERS = headersOf(HttpHeaders.ContentType, "application/json")

private val REAL_DETAIL_RESPONSE = """
{
  "data": {
    "id": 93014,
    "title": "View of Schroon Mountain, Essex County, New York, After a Storm",
    "creation_date": "1838",
    "culture": ["America"],
    "technique": "oil on canvas",
    "description": "Championing the American wilderness, Cole declared, \"We are still in Eden,\" in his <em>Essay on American Scenery, </em>published two years before he painted this view of the Adirondacks.",
    "images": {
      "web": {
        "url": "https://openaccess-cdn.clevelandart.org/1917.1335/1917.1335_web.jpg",
        "width": "1263",
        "height": "775"
      }
    },
    "creditline": "Hinman B. Hurlbut Collection",
    "creators": [
      { "id": 2659, "description": "Thomas Cole (American, born England,1801–1848)", "role": "artist" }
    ]
  }
}
""".trimIndent()

class ArtworkRepositoryTest {

    @Test
    fun `getArtworkDetail maps a real response to domain with the image web url`() = runBlocking {
        val engine = MockEngine { respond(ByteReadChannel(REAL_DETAIL_RESPONSE), HttpStatusCode.OK, JSON_HEADERS) }
        val repository = DefaultArtworkRepository(ArtworkApi(createHttpClient(engine)))

        val result = repository.getArtworkDetail(93014)

        val success = assertIs<ArtworkResult.Success<*>>(result)
        val detail = assertIs<co.delvalle.artapp.domain.model.ArtworkDetail>(success.data)
        assertEquals("https://openaccess-cdn.clevelandart.org/1917.1335/1917.1335_web.jpg", detail.imageUrl)
        assertEquals("Thomas Cole (American, born England,1801–1848)", detail.artistDisplay)
        assertEquals("America", detail.placeOfOrigin)
    }

    @Test
    fun `getArtworkDetail maps a 404 response to ArtworkError NotFound`() = runBlocking {
        val engine = MockEngine { respondError(HttpStatusCode.NotFound) }
        val repository = DefaultArtworkRepository(ArtworkApi(createHttpClient(engine)))

        val result = repository.getArtworkDetail(id = 999_999)

        val error = assertIs<ArtworkResult.Error>(result)
        assertEquals(ArtworkError.NotFound, error.error)
    }

    @Test
    fun `getArtworks maps a 500 response to ArtworkError ServerError`() = runBlocking {
        val engine = MockEngine { respondError(HttpStatusCode.InternalServerError) }
        val repository = DefaultArtworkRepository(ArtworkApi(createHttpClient(engine)))

        val result = repository.getArtworks(page = 1, limit = 24)

        val error = assertIs<ArtworkResult.Error>(result)
        assertEquals(ArtworkError.ServerError, error.error)
    }
}
