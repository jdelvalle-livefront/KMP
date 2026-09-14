package co.delvalle.artapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ArtworkDetailResponseDto(
    val data: ArtworkDto,
)
