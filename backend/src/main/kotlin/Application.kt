package com.bibliophile

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.sessions.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import com.bibliophile.config.*
import com.bibliophile.models.UserSession
import com.bibliophile.db.DatabaseFactory


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(Sessions) {
        cookie<UserSession>("USER_SESSION") {
            cookie.path = "/"
            cookie.httpOnly = true 
            cookie.secure = true // `false` only if running locally (not HTTPS)
            cookie.extensions["SameSite"] = "None"
            cookie.maxAgeInSeconds = 60 * 60 * 24
        }
    }

    install(Authentication) {
        session<UserSession>("auth-session") {
            validate { session ->
                if(session.userId != null) session else null
            }
            challenge {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Not authenticated"))
            }
        }
    }

    configureSerialization()
    configureMonitoring()
    configureRouting()
    configureHTTP()

    DatabaseFactory.init()
}
