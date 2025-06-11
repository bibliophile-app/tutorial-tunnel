package com.bibliophile.config

import java.net.URI
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureHTTP() {

    val viteAllowedHost = System.getenv("VITE_ALLOWED_HOST")

    install(CORS) {
        allowCredentials = true
        allowHost("localhost:3000", schemes = listOf("http"))
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)

        viteAllowedHost?.let {
            try {
                val uri = URI("https://$it")
                allowHost(uri.host, schemes = listOf(uri.scheme))
            } catch (e: Exception) {
                this@configureHTTP.log.warn("VITE_ALLOWED_HOST is not a valid URI: $it")
            }
        }
    }
}