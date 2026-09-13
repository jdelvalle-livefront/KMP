package co.delvalle.artapp.data.remote

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

internal actual fun platformHttpClientEngine(): HttpClientEngine = OkHttp.create()
