package com.dinarastepina.decomposedictionary

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform