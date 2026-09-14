package co.delvalle.artapp.data.remote

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private val JSON_HEADERS = headersOf(HttpHeaders.ContentType, "application/json")

// Captured on 2026-09-13 from GET /api/artworks/?limit=2&has_image=1&fields=id,title,creation_date,creators,images
private val REAL_LIST_RESPONSE = """
{
  "info": {
    "total": 41514,
    "parameters": { "skip": 0, "limit": 2, "has_image": "1" }
  },
  "data": [
    {
      "id": 94979,
      "accession_number": "1915.534",
      "title": "Nathaniel Hurd",
      "creation_date": "c. 1765",
      "creators": [
        {
          "id": 2409,
          "description": "John Singleton Copley (American, born The Thirteen Colonies, 1738–1815)",
          "role": "artist"
        }
      ],
      "images": {
        "web": {
          "url": "https://openaccess-cdn.clevelandart.org/1915.534/1915.534_web.jpg",
          "width": "748",
          "height": "893"
        }
      }
    },
    {
      "id": 124245,
      "accession_number": "1945.24",
      "title": "La Vie",
      "creation_date": "1903",
      "creators": [
        { "id": 2160, "description": "Pablo Picasso (Spanish, 1881–1973)", "role": "artist" }
      ],
      "images": {}
    }
  ]
}
""".trimIndent()

// Captured on 2026-09-13 from GET /api/artworks/93014?fields=id,title,creation_date,creators,images,technique,description,creditline,culture
private val REAL_DETAIL_RESPONSE = """
{
  "data": {
    "id": 93014,
    "accession_number": "1917.1335",
    "title": "View of Schroon Mountain, Essex County, New York, After a Storm",
    "creation_date": "1838",
    "culture": ["America"],
    "technique": "oil on canvas",
    "description": "Championing the American wilderness, Cole declared, \"We are still in Eden,\" in his <em>Essay on American Scenery, </em>published two years before he painted this view of the Adirondacks.<br><br>Cole included two Indigenous men in the painting's right foreground foliage.",
    "images": {
      "web": {
        "url": "https://openaccess-cdn.clevelandart.org/1917.1335/1917.1335_web.jpg",
        "width": "1263",
        "height": "775"
      }
    },
    "creditline": "Hinman B. Hurlbut Collection",
    "creators": [
      {
        "id": 2659,
        "description": "Thomas Cole (American, born England,1801–1848)",
        "role": "artist"
      }
    ],
    "has_conservation_images": false
  }
}
""".trimIndent()

private val LIST_RESPONSE_WITH_MISSING_FIELDS = """
{
  "info": { "total": 1 },
  "data": [
    { "id": 1, "title": "Untitled Work" }
  ]
}
""".trimIndent()

class ArtworkApiTest {

    @Test
    fun `getArtworks parses a real response and ignores unknown top-level keys`() = runBlocking {
        val engine = MockEngine { respond(ByteReadChannel(REAL_LIST_RESPONSE), HttpStatusCode.OK, JSON_HEADERS) }
        val api = ArtworkApi(createHttpClient(engine))

        val response = api.getArtworks(page = 1, limit = 2)

        assertEquals(2, response.data.size)
        assertEquals("John Singleton Copley (American, born The Thirteen Colonies, 1738–1815)", response.data[0].creators.single().description)
        assertEquals("https://openaccess-cdn.clevelandart.org/1915.534/1915.534_web.jpg", response.data[0].images?.web?.url)
        assertNull(response.data[1].images?.web)
    }

    @Test
    fun `getArtworkDetail parses a real response with raw HTML description`() = runBlocking {
        val engine = MockEngine { respond(ByteReadChannel(REAL_DETAIL_RESPONSE), HttpStatusCode.OK, JSON_HEADERS) }
        val api = ArtworkApi(createHttpClient(engine))

        val response = api.getArtworkDetail(93014)

        assertEquals("Thomas Cole (American, born England,1801–1848)", response.data.creators.single().description)
        assertEquals("oil on canvas", response.data.technique)
        assertEquals(true, response.data.description?.contains("<em>"))
    }

    @Test
    fun `getArtworks tolerates missing creators and images`() = runBlocking {
        val engine = MockEngine { respond(ByteReadChannel(LIST_RESPONSE_WITH_MISSING_FIELDS), HttpStatusCode.OK, JSON_HEADERS) }
        val api = ArtworkApi(createHttpClient(engine))

        val response = api.getArtworks(page = 1, limit = 1)

        val artwork = response.data.single()
        assertEquals(emptyList(), artwork.creators)
        assertNull(artwork.images)
    }
}
