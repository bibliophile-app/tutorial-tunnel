package com.bibliophile.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.application.*
import com.bibliophile.models.UserSession
import com.bibliophile.models.ReviewRequest
import com.bibliophile.repositories.ReviewRepository

fun Route.reviewRoutes() {

    route("reviews") {
        
        get {
            call.respond(HttpStatusCode.OK, ReviewRepository.allReviews())
        }

        get("/{id}") {
            val id = call.getIntParam() ?: return@get

            val review = ReviewRepository.review(id)
            if (review != null) {
                call.respond(HttpStatusCode.OK, review)
            } else {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to "Review not found"))
            }
        }

        get("/user/{identifier}") {
            val userId = call.resolveUserIdOrRespondNotFound() ?: return@get

            val reviews = ReviewRepository.getReviewsByUserId(userId)
            call.respond(HttpStatusCode.OK, reviews)
        }

        get("/book/{bookId}") {
            val bookId = call.getParam("bookId") ?: return@get

            val reviews = ReviewRepository.getReviewsById(bookId)
            call.respond(HttpStatusCode.OK, reviews)
        }

        authenticate("auth-session") { 
            post {
                val review = call.receive<ReviewRequest>()
                val session = call.sessions.get<UserSession>()

                validateReview(review)?.let {
                    call.respond(it.first, mapOf("message" to it.second))
                    return@post
                }
            
                val response = ReviewRepository.addReview(session?.userId!!, review)
                call.respond(HttpStatusCode.Created, mapOf("message" to "Review created successfully - Review ID: ${response.id}"))           
            }
            
            put("/{id}") {
                val id = call.getIntParam() ?: return@put       
                val review = call.receive<ReviewRequest>()
                val session = call.sessions.get<UserSession>()

                validateReview(review)?.let {
                    call.respond(it.first, mapOf("message" to it.second))
                    return@put
                }
                
                val status = ReviewRepository.updateReview(id, session?.userId!!, review)
                if (status) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Review updated successfully"))
                } else {
                    call.respond(HttpStatusCode.Forbidden, mapOf("message" to "You don't own this review"))
                }
            }

            delete("/{id}") {
                val id = call.getIntParam() ?: return@delete
                val session = call.sessions.get<UserSession>()
            
                val status = ReviewRepository.deleteReview(id, session?.userId!!)
                if (status) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Review deleted successfully"))
                } else {
                    call.respond(HttpStatusCode.Forbidden, mapOf("message" to "You don't own this review"))
                } 
            }        
        }
    }
}

private fun validateReview(review: ReviewRequest): Pair<HttpStatusCode, String>? {
    return when {
        review.rate !in 0..10 -> HttpStatusCode.BadRequest to "Rate must be between 0 and 10"
        review.bookId.isBlank() -> HttpStatusCode.BadRequest to "Book ID is required"
        else -> null
    }
}