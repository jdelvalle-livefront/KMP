package co.delvalle.artapp.ui.list

import co.delvalle.artapp.domain.model.Artwork
import co.delvalle.artapp.domain.model.ArtworkError

sealed interface ListUiState {
    data object Loading : ListUiState
    data class Content(val artworks: List<Artwork>) : ListUiState
    data object Empty : ListUiState
    data class Error(val error: ArtworkError) : ListUiState
}
