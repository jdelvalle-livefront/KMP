package co.delvalle.artapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.delvalle.artapp.data.ArtworkRepository
import co.delvalle.artapp.domain.model.ArtworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: ArtworkRepository,
    private val artworkId: Long,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            _uiState.value = when (val result = repository.getArtworkDetail(artworkId)) {
                is ArtworkResult.Success -> DetailUiState.Content(result.data)
                is ArtworkResult.Error -> DetailUiState.Error(result.error)
            }
        }
    }
}
