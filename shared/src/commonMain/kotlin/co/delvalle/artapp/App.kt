@file:OptIn(coil3.annotation.ExperimentalCoilApi::class)

package co.delvalle.artapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import co.delvalle.artapp.data.ArtworkRepository
import co.delvalle.artapp.data.remote.createHttpClient
import co.delvalle.artapp.ui.detail.DetailScreen
import co.delvalle.artapp.ui.detail.DetailViewModel
import co.delvalle.artapp.ui.list.ListScreen
import co.delvalle.artapp.ui.list.ListViewModel
import co.delvalle.artapp.ui.theme.ArtAppTheme
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import kotlinx.serialization.Serializable

@Serializable
private data object ArtworkListRoute

@Serializable
private data class ArtworkDetailRoute(val artworkId: Long)

@Composable
fun App(repository: ArtworkRepository) {
    ArtAppTheme {
        val imageHttpClient = remember { createHttpClient() }
        setSingletonImageLoaderFactory { context ->
            ImageLoader.Builder(context)
                .components { add(KtorNetworkFetcherFactory(imageHttpClient)) }
                .build()
        }

        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = ArtworkListRoute) {
            composable<ArtworkListRoute> {
                ListScreen(
                    viewModel = viewModel { ListViewModel(repository) },
                    onArtworkClick = { artworkId -> navController.navigate(ArtworkDetailRoute(artworkId)) },
                )
            }
            composable<ArtworkDetailRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ArtworkDetailRoute>()
                DetailScreen(
                    viewModel = viewModel { DetailViewModel(repository, route.artworkId) },
                    onBackClick = navController::popBackStack,
                )
            }
        }
    }
}
