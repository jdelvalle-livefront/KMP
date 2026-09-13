package co.delvalle.artapp.data.remote.dto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private const val IMAGE_URL = "https://openaccess-cdn.clevelandart.org/1915.534/1915.534_web.jpg"

class ArtworkMappersTest {

    @Test
    fun `toArtwork with empty images object maps to null imageUrl`() {
        val dto = ArtworkDto(id = 1, title = "Nathaniel Hurd", images = ImagesDto(web = null))

        val artwork = dto.toArtwork()

        assertNull(artwork.imageUrl)
    }

    @Test
    fun `toArtwork with images web url maps imageUrl`() {
        val dto = ArtworkDto(id = 1, title = "Nathaniel Hurd", images = ImagesDto(web = ImageAssetDto(url = IMAGE_URL)))

        val artwork = dto.toArtwork()

        assertEquals(IMAGE_URL, artwork.imageUrl)
    }

    @Test
    fun `toArtwork with null title falls back to placeholder`() {
        val dto = ArtworkDto(id = 1, title = null)

        val artwork = dto.toArtwork()

        assertEquals("Untitled", artwork.title)
    }

    @Test
    fun `toArtwork with empty creators maps to null artistDisplay`() {
        val dto = ArtworkDto(id = 1, creators = emptyList())

        val artwork = dto.toArtwork()

        assertNull(artwork.artistDisplay)
    }

    @Test
    fun `toArtwork uses the first creator's description`() {
        val dto = ArtworkDto(
            id = 1,
            creators = listOf(
                CreatorDto(description = "John Singleton Copley (American, born The Thirteen Colonies, 1738–1815)"),
                CreatorDto(description = "Should be ignored"),
            ),
        )

        val artwork = dto.toArtwork()

        assertEquals("John Singleton Copley (American, born The Thirteen Colonies, 1738–1815)", artwork.artistDisplay)
    }

    @Test
    fun `toArtworkDetail with empty images object maps to null imageUrl`() {
        val dto = ArtworkDto(id = 1, title = "La Vie", images = ImagesDto(web = null))

        val detail = dto.toArtworkDetail()

        assertNull(detail.imageUrl)
    }

    @Test
    fun `toArtworkDetail with images web url maps imageUrl`() {
        val dto = ArtworkDto(id = 1, title = "La Vie", images = ImagesDto(web = ImageAssetDto(url = IMAGE_URL)))

        val detail = dto.toArtworkDetail()

        assertEquals(IMAGE_URL, detail.imageUrl)
    }

    @Test
    fun `toArtworkDetail maps optional fields as null when absent`() {
        val dto = ArtworkDto(id = 1, title = "La Vie")

        val detail = dto.toArtworkDetail()

        assertNull(detail.mediumDisplay)
        assertNull(detail.placeOfOrigin)
        assertNull(detail.creditLine)
        assertNull(detail.description)
    }

    @Test
    fun `toArtworkDetail joins multiple culture entries`() {
        val dto = ArtworkDto(id = 1, culture = listOf("America", "Northern Italy, late 16th century"))

        val detail = dto.toArtworkDetail()

        assertEquals("America, Northern Italy, late 16th century", detail.placeOfOrigin)
    }

    @Test
    fun `toArtworkDetail uses the first creator's description`() {
        val dto = ArtworkDto(id = 1, creators = listOf(CreatorDto(description = "Pablo Picasso (Spanish, 1881–1973)")))

        val detail = dto.toArtworkDetail()

        assertEquals("Pablo Picasso (Spanish, 1881–1973)", detail.artistDisplay)
    }
}
