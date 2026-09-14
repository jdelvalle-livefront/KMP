package co.delvalle.artapp.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.delvalle.artapp.ui.components.ErrorState
import co.delvalle.artapp.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(viewModel: ListViewModel, onArtworkClick: (Long) -> Unit, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Cleveland Museum of Art") }) },
    ) { contentPadding ->
        when (val state = uiState) {
            is ListUiState.Loading -> LoadingIndicator(Modifier.fillMaxSize().padding(contentPadding))

            is ListUiState.Empty -> Text(
                text = "No artworks to show.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxSize().padding(contentPadding).padding(24.dp),
            )

            is ListUiState.Error -> ErrorState(
                error = state.error,
                onRetry = viewModel::retry,
                modifier = Modifier.fillMaxSize().padding(contentPadding),
            )

            is ListUiState.Content -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(contentPadding),
                verticalArrangement = Arrangement.Top,
            ) {
                items(state.artworks, key = { it.id }) { artwork ->
                    ArtworkListItem(artwork = artwork, onClick = { onArtworkClick(artwork.id) })
                }
            }
        }
    }
}
