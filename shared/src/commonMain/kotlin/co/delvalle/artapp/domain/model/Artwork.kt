package co.delvalle.artapp.domain.model

data class Artwork(
    val id: Long,
    val title: String,
    val artistDisplay: String?,
    val dateDisplay: String?,
    val imageUrl: String?,
)

data class ArtworkDetail(
    val id: Long,
    val title: String,
    val artistDisplay: String?,
    val dateDisplay: String?,
    val imageUrl: String?,
    val mediumDisplay: String?,
    val placeOfOrigin: String?,
    val creditLine: String?,
    val description: String?,
)
