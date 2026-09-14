package co.delvalle.artapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.delvalle.artapp.domain.model.ArtworkError

@Composable
fun ErrorState(error: ArtworkError, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = error.toMessage(),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

private fun ArtworkError.toMessage(): String = when (this) {
    ArtworkError.NotFound -> "We couldn't find this artwork."
    ArtworkError.ServerError -> "The API server had a problem. Please try again in a moment."
    ArtworkError.Network -> "No connection. Check your network and try again."
    is ArtworkError.Unknown -> "An unexpected error occurred."
}
