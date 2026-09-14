@file:OptIn(ExperimentalCoroutinesApi::class)

package co.delvalle.artapp.ui.detail

import co.delvalle.artapp.data.FakeArtworkRepository
import co.delvalle.artapp.domain.model.ArtworkDetail
import co.delvalle.artapp.domain.model.ArtworkError
import co.delvalle.artapp.domain.model.ArtworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private val SAMPLE_DETAIL = ArtworkDetail(
    id = 93014,
    title = "View of Schroon Mountain, Essex County, New York, After a Storm",
    artistDisplay = "Thomas Cole",
    dateDisplay = "1838",
    imageUrl = "https://openaccess-cdn.clevelandart.org/1917.1335/1917.1335_web.jpg",
    mediumDisplay = "Oil on canvas",
    placeOfOrigin = "America",
    creditLine = "Hinman B. Hurlbut Collection",
    description = "<p>...</p>",
)

class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `starts in Loading and moves to Content on success`() = runTest(testDispatcher) {
        val repository = FakeArtworkRepository(artworkDetailResult = ArtworkResult.Success(SAMPLE_DETAIL))
        val viewModel = DetailViewModel(repository, artworkId = SAMPLE_DETAIL.id)

        assertEquals(DetailUiState.Loading, viewModel.uiState.value)

        advanceUntilIdle()

        assertEquals(DetailUiState.Content(SAMPLE_DETAIL), viewModel.uiState.value)
    }

    @Test
    fun `repository error maps to Error`() = runTest(testDispatcher) {
        val repository = FakeArtworkRepository(artworkDetailResult = ArtworkResult.Error(ArtworkError.NotFound))
        val viewModel = DetailViewModel(repository, artworkId = 999_999)

        advanceUntilIdle()

        assertEquals(DetailUiState.Error(ArtworkError.NotFound), viewModel.uiState.value)
    }

    @Test
    fun `retry after an error can recover to Content`() = runTest(testDispatcher) {
        val repository = FakeArtworkRepository(artworkDetailResult = ArtworkResult.Error(ArtworkError.Network))
        val viewModel = DetailViewModel(repository, artworkId = SAMPLE_DETAIL.id)
        advanceUntilIdle()
        assertEquals(DetailUiState.Error(ArtworkError.Network), viewModel.uiState.value)

        repository.artworkDetailResult = ArtworkResult.Success(SAMPLE_DETAIL)
        viewModel.retry()
        advanceUntilIdle()

        assertEquals(DetailUiState.Content(SAMPLE_DETAIL), viewModel.uiState.value)
        assertEquals(2, repository.getArtworkDetailCallCount)
    }
}
