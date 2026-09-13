package co.delvalle.artapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.delvalle.artapp.data.ArtworkRepository
import co.delvalle.artapp.domain.model.ArtworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListViewModel(private val repository: ArtworkRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ListUiState>(ListUiState.Loading)
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        viewModelScope.launch {
            _uiState.value = ListUiState.Loading
            _uiState.value = when (val result = repository.getArtworks()) {
                is ArtworkResult.Success ->
                    if (result.data.isEmpty()) ListUiState.Empty else ListUiState.Content(result.data)
                is ArtworkResult.Error -> ListUiState.Error(result.error)
            }
        }
    }
}
