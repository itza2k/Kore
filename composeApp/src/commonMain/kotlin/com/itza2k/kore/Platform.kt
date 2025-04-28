package com.itza2k.kore

interface Platform {
    val name: String
}
expect fun getPlatform(): Platform