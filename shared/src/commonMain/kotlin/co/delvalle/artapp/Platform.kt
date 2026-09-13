package co.delvalle.artapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform