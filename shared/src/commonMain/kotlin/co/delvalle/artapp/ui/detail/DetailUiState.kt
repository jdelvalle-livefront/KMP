package co.delvalle.artapp.ui.detail

import co.delvalle.artapp.domain.model.ArtworkDetail
import co.delvalle.artapp.domain.model.ArtworkError

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Content(val artwork: ArtworkDetail) : DetailUiState
    data class Error(val error: ArtworkError) : DetailUiState
}
