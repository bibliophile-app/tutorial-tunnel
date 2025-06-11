package com.bibliophile.routes

import kotlin.test.*
import io.ktor.http.*
import io.ktor.client.*
import io.ktor.server.auth.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.testing.*

import com.bibliophile.models.*
import com.bibliophile.utils.*

class ReviewRoutesTest {

    @BeforeTest
    fun setup() {
        TestDatabaseFactory.init()
    }

    @AfterTest
    fun teardown() {
        TestDatabaseFactory.reset()
    }
    
    private fun Application.setupTestModule() {
        install(ContentNegotiation) {
            json()
        }
        install(Sessions) {
            cookie<UserSession>("USER_SESSION") {
                cookie.path = "/"
                cookie.httpOnly = true
            }
        }
        install(Authentication) {
            session<UserSession>("auth-session") {
                validate { session ->
                    if (session.userId != null) session else null
                }
                challenge {
                    call.respond(HttpStatusCode.Unauthorized, null)
                }
            }
        }
        routing {
            authRoutes()
            reviewRoutes()
        }
    }

    private suspend fun HttpClient.createReview(
        sessionCookie: String,
        bookId: String,
        content: String,
        rate: Int,
        favorite: Boolean,
        reviewedAt: String
    ): HttpResponse = post("/reviews") {
        header(HttpHeaders.ContentType, ContentType.Application.Json)
        header(HttpHeaders.Cookie, sessionCookie)
        setBody("""
            {
                "bookId": "$bookId",
                "content": "$content",
                "rate": $rate,
                "favorite": $favorite,
                "reviewedAt": "$reviewedAt"
            }
        """.trimIndent())
    }

    private suspend fun HttpClient.updateReview(
        sessionCookie: String,
        reviewId: Int,
        bookId: String,
        content: String,
        rate: Int,
        favorite: Boolean,
        reviewedAt: String
    ): HttpResponse = put("/reviews/$reviewId") {
        header(HttpHeaders.ContentType, ContentType.Application.Json)
        header(HttpHeaders.Cookie, sessionCookie)
        setBody("""
            {
                "bookId": "$bookId",
                "content": "$content",
                "rate": $rate,
                "favorite": $favorite,
                "reviewedAt": "$reviewedAt"
            }
        """.trimIndent())
    }

    @Test
    fun `test create review`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")
        val response = client.createReview(
            sessionCookie,
            bookId = "book123",
            content = "Amazing book!",
            rate = 5,
            favorite = true,
            reviewedAt = "2023-05-11"
        )

        assertEquals(HttpStatusCode.Created, response.status)
        assertTrue(response.bodyAsText().contains("Review created successfully"))
    }

    @Test
    fun `test create review without authentication`() = testApplication {
        application { setupTestModule() }

        val response = client.createReview(
            sessionCookie = "",
            bookId = "book123",
            content = "Unauthorized review",
            rate = 3,
            favorite = false,
            reviewedAt = "2023-05-11"
        )

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `test create review with rate above`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")
        val response = client.createReview(
            sessionCookie,
            bookId = "book123",
            content = "Invalid rate",
            rate = 15,
            favorite = false,
            reviewedAt = "2023-05-11"
        )

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Rate must be between 0 and 10"))
    }

    @Test
    fun `test create review with rate under`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")
        val response = client.createReview(
            sessionCookie,
            bookId = "book123",
            content = "Invalid rate",
            rate = -1,
            favorite = false,
            reviewedAt = "2023-05-11"
        )

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Rate must be between 0 and 10"))
    }

    @Test
    fun `test create review with empty bookId`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")
        val response = client.createReview(
            sessionCookie,
            bookId = "",
            content = "Missing bookId",
            rate = 7,
            favorite = false,
            reviewedAt = "2023-05-11"
        )

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Book ID is required"))
    }

    @Test
    fun `test get all reviews`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")

        client.createReview(sessionCookie, "book123", "Great book!", 5, true, "2023-05-11")
        client.createReview(sessionCookie, "book456", "Not bad", 3, false, "2023-05-12")

        val response = client.get("/reviews") {
            header(HttpHeaders.Cookie, sessionCookie)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val reviews = response.bodyAsText()
        assertTrue(reviews.contains("Great book!"))
        assertTrue(reviews.contains("Not bad"))
    }

    @Test
    fun `test get review by ID`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")
        val createResponse = client.createReview(
            sessionCookie,
            bookId = "book123",
            content = "Good book",
            rate = 4,
            favorite = false,
            reviewedAt = "2023-05-11"
        )

        val reviewId = createResponse.bodyAsText().extractReviewId()
        val response = client.get("/reviews/$reviewId")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Good book"))
    }

    @Test
    fun `test get review by userID`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")
        val createResponse = client.createReview(
            sessionCookie,
            bookId = "book123",
            content = "Good book",
            rate = 4,
            favorite = false,
            reviewedAt = "2023-05-11"
        )
        
        val response = client.get("/reviews/user/1")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Good book"))
    }

    @Test
    fun `test get review by bookID`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")
        val createResponse = client.createReview(
            sessionCookie,
            bookId = "book123",
            content = "Good book",
            rate = 4,
            favorite = false,
            reviewedAt = "2023-05-11"
        )
        
        val response = client.get("/reviews/book/book123") {
            header(HttpHeaders.Cookie, sessionCookie)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Good book"))
    }

    @Test
    fun `test get nonexistent review`() = testApplication {
        application { setupTestModule() }

        val response = client.get("/reviews/999")

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("Review not found"))
    }

    @Test
    fun `test get review with invalid id parameter`() = testApplication {
        application { setupTestModule() }

        val response = client.get("/reviews/invalid-id")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `test get reviews by user with invalid userId param`() = testApplication {
        application { setupTestModule() }

        val response = client.get("/reviews/user/abc")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `test update review`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")

        val createResponse = client.createReview(
            sessionCookie,
            bookId = "book123",
            content = "Good book",
            rate = 4,
            favorite = false,
            reviewedAt = "2023-05-11"
        )
        val reviewId = createResponse.bodyAsText().extractReviewId()

        val updateResponse = client.updateReview(
            sessionCookie,
            reviewId,
            bookId = "book123",
            content = "Excellent book",
            rate = 5,
            favorite = true,
            reviewedAt = "2023-05-12"
        )

        assertEquals(HttpStatusCode.OK, updateResponse.status)
        assertTrue(updateResponse.bodyAsText().contains("Review updated successfully"))
    }

    @Test
    fun `test create review with invalid rate`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")
        val createResponse = client.createReview(
            sessionCookie,
            bookId = "book123",
            content = "Good book",
            rate = 4,
            favorite = false,
            reviewedAt = "2023-05-11"
        )
        val reviewId = createResponse.bodyAsText().extractReviewId()

        val updateResponse = client.updateReview(
            sessionCookie,
            reviewId,
            bookId = "book123",
            content = "Excellent book",
            rate = 11,
            favorite = true,
            reviewedAt = "2023-05-12"
        )

        assertEquals(HttpStatusCode.BadRequest, updateResponse.status)
        assertTrue(updateResponse.bodyAsText().contains("Rate must be between 0 and 10"))
    }

    @Test
    fun `test update review with empty bookId`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")
        val createResponse = client.createReview(
            sessionCookie,
            bookId = "book123",
            content = "Good book",
            rate = 4,
            favorite = false,
            reviewedAt = "2023-05-11"
        )
        val reviewId = createResponse.bodyAsText().extractReviewId()

        val updateResponse = client.updateReview(
            sessionCookie,
            reviewId,
            bookId = "",
            content = "Missing bookId",
            rate = 7,
            favorite = false,
            reviewedAt = "2023-05-12"
        )

        assertEquals(HttpStatusCode.BadRequest, updateResponse.status)
        assertTrue(updateResponse.bodyAsText().contains("Book ID is required"))
    }

    @Test
    fun `test update review by non-owner returns forbidden`() = testApplication {
        application { setupTestModule() }

        val ownerCookie = client.registerAndLoginUser("owner@example.com", "owner", "password123")
        val createResponse = client.createReview(
            ownerCookie,
            bookId = "book123",
            content = "Owner's review",
            rate = 5,
            favorite = true,
            reviewedAt = "2023-05-11"
        )
        val reviewId = createResponse.bodyAsText().extractReviewId()

        val intruderCookie = client.registerAndLoginUser("intruder@example.com", "intruder", "password123")
        val updateResponse = client.updateReview(
            intruderCookie,
            reviewId,
            bookId = "book123",
            content = "Excellent book",
            rate = 5,
            favorite = true,
            reviewedAt = "2023-05-12"
        )

        assertEquals(HttpStatusCode.Forbidden, updateResponse.status)
        assertTrue(updateResponse.bodyAsText().contains("You don't own this review"))
    }

    @Test
    fun `test delete review`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")

        val createResponse = client.createReview(
            sessionCookie,
            bookId = "book123",
            content = "Good book",
            rate = 4,
            favorite = false,
            reviewedAt = "2023-05-11"
        )
        val reviewId = createResponse.bodyAsText().extractReviewId()

        val deleteResponse = client.delete("/reviews/$reviewId") {
            header(HttpHeaders.Cookie, sessionCookie)
        }

        assertEquals(HttpStatusCode.OK, deleteResponse.status)
        assertTrue(deleteResponse.bodyAsText().contains("Review deleted successfully"))
    }

    @Test
    fun `test delete review by non-owner returns forbidden`() = testApplication {
        application { setupTestModule() }

        val ownerCookie = client.registerAndLoginUser("owner@example.com", "owner", "password123")
        val createResponse = client.createReview(
            ownerCookie,
            bookId = "book123",
            content = "Owner's review",
            rate = 5,
            favorite = true,
            reviewedAt = "2023-05-11"
        )
        val reviewId = createResponse.bodyAsText().extractReviewId()

        val intruderCookie = client.registerAndLoginUser("intruder@example.com", "intruder", "password123")
        val response = client.delete("/reviews/$reviewId") {
            header(HttpHeaders.Cookie, intruderCookie)
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertTrue(response.bodyAsText().contains("You don't own this review"))
    }

    private fun String.extractReviewId(): Int {
        val regex = """Review ID:\s*(\d+)""".toRegex()
        val match = regex.find(this)
        return match?.groupValues?.get(1)?.toInt() ?: error("Review ID not found in response")
    }
}