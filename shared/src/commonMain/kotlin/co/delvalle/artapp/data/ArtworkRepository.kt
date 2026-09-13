package co.delvalle.artapp.data

import co.delvalle.artapp.data.remote.ArtworkApi
import co.delvalle.artapp.data.remote.dto.toArtwork
import co.delvalle.artapp.data.remote.dto.toArtworkDetail
import co.delvalle.artapp.domain.model.Artwork
import co.delvalle.artapp.domain.model.ArtworkDetail
import co.delvalle.artapp.domain.model.ArtworkError
import co.delvalle.artapp.domain.model.ArtworkResult
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException

interface ArtworkRepository {
    suspend fun getArtworks(page: Int = 1, limit: Int = 24): ArtworkResult<List<Artwork>>
    suspend fun getArtworkDetail(id: Long): ArtworkResult<ArtworkDetail>
}

class DefaultArtworkRepository(private val api: ArtworkApi) : ArtworkRepository {

    override suspend fun getArtworks(page: Int, limit: Int): ArtworkResult<List<Artwork>> = safeCall {
        api.getArtworks(page, limit).data.map { it.toArtwork() }
    }

    override suspend fun getArtworkDetail(id: Long): ArtworkResult<ArtworkDetail> = safeCall {
        api.getArtworkDetail(id).data.toArtworkDetail()
    }

    private suspend fun <T> safeCall(block: suspend () -> T): ArtworkResult<T> = try {
        ArtworkResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        ArtworkResult.Error(e.toArtworkError())
    }
}

private fun Throwable.toArtworkError(): ArtworkError = when (this) {
    is ClientRequestException ->
        if (response.status == HttpStatusCode.NotFound) ArtworkError.NotFound else ArtworkError.Unknown(message)
    is ServerResponseException -> ArtworkError.ServerError
    else -> ArtworkError.Network
}
