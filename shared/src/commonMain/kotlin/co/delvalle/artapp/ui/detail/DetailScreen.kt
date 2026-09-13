package co.delvalle.artapp.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.delvalle.artapp.domain.model.ArtworkDetail
import co.delvalle.artapp.ui.components.ArtworkImage
import co.delvalle.artapp.ui.components.ErrorState
import co.delvalle.artapp.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(viewModel: DetailViewModel, onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Detail") },
                navigationIcon = { TextButton(onClick = onBackClick) { Text("‹ Back") } },
            )
        },
    ) { contentPadding ->
        when (val state = uiState) {
            is DetailUiState.Loading -> LoadingIndicator(Modifier.fillMaxSize().padding(contentPadding))

            is DetailUiState.Error -> ErrorState(
                error = state.error,
                onRetry = viewModel::retry,
                modifier = Modifier.fillMaxSize().padding(contentPadding),
            )

            is DetailUiState.Content -> DetailContent(
                artwork = state.artwork,
                modifier = Modifier.fillMaxSize().padding(contentPadding),
            )
        }
    }
}

@Composable
private fun DetailContent(artwork: ArtworkDetail, modifier: Modifier = Modifier) {
    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        ArtworkImage(
            imageUrl = artwork.imageUrl,
            contentDescription = artwork.title,
            modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f),
            contentScale = ContentScale.Fit,
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = artwork.title, style = MaterialTheme.typography.headlineSmall)
            artwork.artistDisplay?.let {
                Text(text = it, style = MaterialTheme.typography.titleMedium)
            }
            artwork.dateDisplay?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            artwork.mediumDisplay?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }
            artwork.placeOfOrigin?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }
            artwork.description?.let {
                Text(
                    text = it.stripHtmlTags(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
            artwork.creditLine?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
        }
    }
}

// The API returns `description` as raw HTML (see README): strip tags with a
// simple regex instead of real sanitizing/rendering, which is out of scope.
private fun String.stripHtmlTags(): String = replace(Regex("<[^>]+>"), "").trim()
