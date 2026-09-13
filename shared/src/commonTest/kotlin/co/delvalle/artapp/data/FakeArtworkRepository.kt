package co.delvalle.artapp.data

import co.delvalle.artapp.domain.model.Artwork
import co.delvalle.artapp.domain.model.ArtworkDetail
import co.delvalle.artapp.domain.model.ArtworkError
import co.delvalle.artapp.domain.model.ArtworkResult

class FakeArtworkRepository(
    var artworksResult: ArtworkResult<List<Artwork>> = ArtworkResult.Error(ArtworkError.Network),
    var artworkDetailResult: ArtworkResult<ArtworkDetail> = ArtworkResult.Error(ArtworkError.Network),
) : ArtworkRepository {

    var getArtworksCallCount = 0
        private set
    var getArtworkDetailCallCount = 0
        private set

    override suspend fun getArtworks(page: Int, limit: Int): ArtworkResult<List<Artwork>> {
        getArtworksCallCount++
        return artworksResult
    }

    override suspend fun getArtworkDetail(id: Long): ArtworkResult<ArtworkDetail> {
        getArtworkDetailCallCount++
        return artworkDetailResult
    }
}
