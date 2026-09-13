@file:OptIn(ExperimentalCoroutinesApi::class)

package co.delvalle.artapp.ui.list

import co.delvalle.artapp.data.FakeArtworkRepository
import co.delvalle.artapp.domain.model.Artwork
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

private val SAMPLE_ARTWORK = Artwork(
    id = 1,
    title = "Nighthawks",
    artistDisplay = "Edward Hopper",
    dateDisplay = "1942",
    imageUrl = "https://openaccess-cdn.clevelandart.org/1234.5/1234.5_web.jpg",
)

class ListViewModelTest {

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
        val repository = FakeArtworkRepository(artworksResult = ArtworkResult.Success(listOf(SAMPLE_ARTWORK)))
        val viewModel = ListViewModel(repository)

        assertEquals(ListUiState.Loading, viewModel.uiState.value)

        advanceUntilIdle()

        assertEquals(ListUiState.Content(listOf(SAMPLE_ARTWORK)), viewModel.uiState.value)
    }

    @Test
    fun `empty artwork list maps to Empty`() = runTest(testDispatcher) {
        val repository = FakeArtworkRepository(artworksResult = ArtworkResult.Success(emptyList()))
        val viewModel = ListViewModel(repository)

        advanceUntilIdle()

        assertEquals(ListUiState.Empty, viewModel.uiState.value)
    }

    @Test
    fun `repository error maps to Error`() = runTest(testDispatcher) {
        val repository = FakeArtworkRepository(artworksResult = ArtworkResult.Error(ArtworkError.ServerError))
        val viewModel = ListViewModel(repository)

        advanceUntilIdle()

        assertEquals(ListUiState.Error(ArtworkError.ServerError), viewModel.uiState.value)
    }

    @Test
    fun `retry after an error can recover to Content`() = runTest(testDispatcher) {
        val repository = FakeArtworkRepository(artworksResult = ArtworkResult.Error(ArtworkError.Network))
        val viewModel = ListViewModel(repository)
        advanceUntilIdle()
        assertEquals(ListUiState.Error(ArtworkError.Network), viewModel.uiState.value)

        repository.artworksResult = ArtworkResult.Success(listOf(SAMPLE_ARTWORK))
        viewModel.retry()
        advanceUntilIdle()

        assertEquals(ListUiState.Content(listOf(SAMPLE_ARTWORK)), viewModel.uiState.value)
        assertEquals(2, repository.getArtworksCallCount)
    }
}
