package co.delvalle.artapp.data.remote.dto

import co.delvalle.artapp.domain.model.Artwork
import co.delvalle.artapp.domain.model.ArtworkDetail

private const val UNTITLED = "Untitled"

fun ArtworkDto.toArtwork(): Artwork = Artwork(
    id = id,
    title = title ?: UNTITLED,
    artistDisplay = creators.firstOrNull()?.description,
    dateDisplay = creationDate,
    imageUrl = images?.web?.url,
)

fun ArtworkDto.toArtworkDetail(): ArtworkDetail = ArtworkDetail(
    id = id,
    title = title ?: UNTITLED,
    artistDisplay = creators.firstOrNull()?.description,
    dateDisplay = creationDate,
    imageUrl = images?.web?.url,
    mediumDisplay = technique,
    placeOfOrigin = culture.takeIf { it.isNotEmpty() }?.joinToString(", "),
    creditLine = creditline,
    description = description,
)
