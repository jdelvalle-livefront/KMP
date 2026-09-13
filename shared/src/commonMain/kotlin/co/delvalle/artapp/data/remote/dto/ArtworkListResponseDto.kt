package co.delvalle.artapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ArtworkListResponseDto(
    val data: List<ArtworkDto>,
)
