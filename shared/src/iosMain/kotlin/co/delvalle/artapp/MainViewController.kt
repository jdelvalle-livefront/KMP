package co.delvalle.artapp

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import co.delvalle.artapp.data.DefaultArtworkRepository
import co.delvalle.artapp.data.remote.ArtworkApi
import co.delvalle.artapp.data.remote.createHttpClient

fun MainViewController() = ComposeUIViewController {
    val repository = remember { DefaultArtworkRepository(ArtworkApi(createHttpClient())) }
    App(repository)
}
