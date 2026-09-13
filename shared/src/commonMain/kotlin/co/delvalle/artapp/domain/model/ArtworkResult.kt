package co.delvalle.artapp.domain.model

sealed interface ArtworkResult<out T> {
    data class Success<out T>(val data: T) : ArtworkResult<T>
    data class Error(val error: ArtworkError) : ArtworkResult<Nothing>
}

sealed interface ArtworkError {
    data object NotFound : ArtworkError
    data object ServerError : ArtworkError
    data object Network : ArtworkError
    data class Unknown(val message: String?) : ArtworkError
}
