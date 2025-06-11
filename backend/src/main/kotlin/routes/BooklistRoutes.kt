package com.bibliophile.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.application.*
import com.bibliophile.models.UserSession
import com.bibliophile.models.BookRequest
import com.bibliophile.models.BooklistRequest
import com.bibliophile.repositories.BooklistRepository

fun Route.booklistRoutes() {

    route("booklists") {

        get {
            call.respond(HttpStatusCode.OK, BooklistRepository.allBooklists())
        }

        get("/{id}") {
            val id = call.getIntParam() ?: return@get

            val booklist = BooklistRepository.booklist(id)
            if (booklist == null)
                call.respond(HttpStatusCode.NotFound, mapOf("message" to "Booklist not found"))
            else
                call.respond(HttpStatusCode.OK, booklist)
        }

        get("/{id}/books") {            
            val id = call.getIntParam() ?: return@get

            val booklist = BooklistRepository.booklistWithBooks(id)
            if (booklist == null)
                call.respond(HttpStatusCode.NotFound, mapOf("message" to "Booklist not found"))
            else
                call.respond(HttpStatusCode.OK, booklist)
        }

        authenticate("auth-session") { 
            post {
                val booklist = call.receive<BooklistRequest>()
                val session = call.sessions.get<UserSession>()
               
                runCatching {
                    BooklistRepository.addBooklist(session?.userId!!, booklist)
                }.onSuccess {
                    call.respond(HttpStatusCode.Created, mapOf("message" to "Booklist created successfully - Booklist ID: ${it.id}"))
                }.onFailure {
                    call.respondSqlException(it) { sql ->
                        sql.message?.contains("unique constraint", ignoreCase = true) == true
                    }
                }
            }

            post("/{id}/books") {
                val id = call.getIntParam() ?: return@post
                val book = call.receive<BookRequest>()
                val session = call.sessions.get<UserSession>()
                
                val response = BooklistRepository.addBookToBooklist(id, session?.userId!!, book.bookId)
                if (response)
                    call.respond(HttpStatusCode.Created, mapOf("message" to "Book added to booklist successfully"))
                else 
                    call.respond(HttpStatusCode.Conflict)
            }

            put("/{id}") {
                val id = call.getIntParam() ?: return@put
                val booklist = call.receive<BooklistRequest>()
                val session = call.sessions.get<UserSession>()

                runCatching {
                    BooklistRepository.updateBooklist(id, session?.userId!!, booklist)
                }.onSuccess {
                    if (it)
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Booklist updated successfully"))
                    else
                        call.respond(HttpStatusCode.Forbidden, mapOf("message" to "You don't own this booklist"))
                }.onFailure {
                    call.respondSqlException(it)
                }
            }

            delete("/{id}") {
                val id = call.getIntParam() ?: return@delete
                val session = call.sessions.get<UserSession>()
                
                val status = BooklistRepository.removeBooklist(id, session?.userId!!)
                if (status)
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Booklist deleted successfully"))
                else
                    call.respond(HttpStatusCode.Forbidden, mapOf("message" to "You don't own this booklist"))
            }

            delete("/{id}/books/{bookId}") {
                val id = call.getIntParam() ?: return@delete
                val bookId = call.getParam("bookId") ?: return@delete
                val session = call.sessions.get<UserSession>()

                val status = BooklistRepository.removeBookFromBooklist(id, session?.userId!!, bookId)
                if (status)
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Book deleted from booklist successfully"))
                else
                    call.respond(HttpStatusCode.Forbidden, mapOf("message" to "You don't own this booklist"))
            }
        }
    }
}