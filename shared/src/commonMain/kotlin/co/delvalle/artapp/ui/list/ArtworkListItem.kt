package co.delvalle.artapp.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import co.delvalle.artapp.domain.model.Artwork
import co.delvalle.artapp.ui.components.ArtworkImage

@Composable
fun ArtworkListItem(artwork: Artwork, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ArtworkImage(
            imageUrl = artwork.imageUrl,
            contentDescription = artwork.title,
            modifier = Modifier.size(72.dp).clip(RoundedCornerShape(8.dp)),
        )
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(text = artwork.title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = listOfNotNull(artwork.artistDisplay, artwork.dateDisplay).joinToString(" · "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
