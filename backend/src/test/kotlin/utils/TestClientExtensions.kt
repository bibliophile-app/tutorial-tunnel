package com.bibliophile.utils

import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import com.bibliophile.models.UserSession

/**
 * Função de extensão para registrar um usuário através do endpoint `/register`.
 */
suspend fun HttpClient.registerUser(
    email: String,
    username: String,
    password: String
): HttpResponse = post("/register") {
    header(HttpHeaders.ContentType, ContentType.Application.Json)
    setBody("""
        {
            "email": "$email",
            "username": "$username",
            "password": "$password"
        }
    """.trimIndent())
}

/**
 * Função de extensão para fazer login de um usuário através do endpoint `/login`.
 */
suspend fun HttpClient.loginUser(
    username: String,
    password: String
): HttpResponse = post("/login") {
    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    setBody("""
        {
            "username": "$username",
            "password": "$password"
        }
    """.trimIndent())
}

/**
 * Função de extensão para criar e logar um usuário e retornar cookie
 */
suspend fun HttpClient.registerAndLoginUser(
    email: String,
    username: String, 
    password: String
): String {
    val sessionCookie = registerUser(email, username, password).setCookie().find { it.name == "USER_SESSION" }
    requireNotNull(sessionCookie) { "Login did not return a session cookie" }
    return "${sessionCookie.name}=${sessionCookie.value}"
}

/**
 * Função para fazer criar cookie de sessão manualmente
 */
fun createSessionCookie(session: UserSession): String {
    val serializer = io.ktor.serialization.kotlinx.json.DefaultJson.encodeToString(
        UserSession.serializer(), session
    )
    val encoded = java.net.URLEncoder.encode(serializer, "UTF-8")
    return "USER_SESSION=$encoded"
}