package co.delvalle.artapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import co.delvalle.artapp.data.DefaultArtworkRepository
import co.delvalle.artapp.data.remote.ArtworkApi
import co.delvalle.artapp.data.remote.createHttpClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val repository = DefaultArtworkRepository(ArtworkApi(createHttpClient()))

        setContent {
            App(repository)
        }
    }
}
