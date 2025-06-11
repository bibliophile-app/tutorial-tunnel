package com.bibliophile.routes

import io.ktor.http.*
import io.ktor.util.date.GMTDate
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable

import com.bibliophile.utils.*
import com.bibliophile.models.UserSession
import com.bibliophile.models.LoginRequest
import com.bibliophile.models.RegisterRequest
import com.bibliophile.repositories.UserRepository

fun Route.authRoutes() {

    post("/register") {
        val data = call.receive<RegisterRequest>()
        if (UserRepository.findByUsername(data.username) != null) {
            call.respond(HttpStatusCode.Conflict, "Username already exists")
            return@post
        }
        val user = UserRepository.create(data.email, data.username, hashPassword(data.password))
        call.sessions.set(UserSession(user.id))
        call.respond(HttpStatusCode.OK, mapOf("message" to "Registered"))
    }
    
    post("/login") {
        val data = call.receive<LoginRequest>()
        val user = UserRepository.findByUsername(data.username)
        if (user == null || !verifyPassword(data.password, user.passwordHash)) {
            call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            return@post
        }
        call.sessions.set(UserSession(user.id))
        call.respond(HttpStatusCode.OK, mapOf("message" to "Logged in"))
    }
    
    get("/logout") {
        call.sessions.clear<UserSession>()
        call.response.cookies.append(expiredSessionCookie())
        call.respond(HttpStatusCode.OK, mapOf("message" to "Logged out"))
    }    

    get("/me") {
        val session = call.sessions.get<UserSession>()
        val userId = session?.userId

        if (userId == null)
            call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Not authenticated"))
        else {
            val profile = UserRepository.getUserProfile(userId)
            if (profile == null) 
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Profile not found"))
            else
                call.respond(profile)
        }
    }

    get("/users/{username}") {
        val username = call.getParam("username") ?: return@get
        val user = UserRepository.findByUsername(username)
        if (user == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            call.respond(mapOf("username" to user.username))
        }
    }
}

private fun expiredSessionCookie(): Cookie {
    return Cookie(
        name = "USER_SESSION",
        value = "",
        path = "/",                
        secure = true,
        httpOnly = true,
        extensions = mapOf("SameSite" to "None"),
        expires = GMTDate.START       
    )
}