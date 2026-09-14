package co.delvalle.artapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArtworkDto(
    val id: Long,
    val title: String? = null,
    @SerialName("creation_date") val creationDate: String? = null,
    val creators: List<CreatorDto> = emptyList(),
    val images: ImagesDto? = null,
    val technique: String? = null,
    val description: String? = null,
    val creditline: String? = null,
    val culture: List<String> = emptyList(),
)

@Serializable
data class CreatorDto(
    val description: String? = null,
)

@Serializable
data class ImagesDto(
    val web: ImageAssetDto? = null,
)

@Serializable
data class ImageAssetDto(
    val url: String? = null,
)
