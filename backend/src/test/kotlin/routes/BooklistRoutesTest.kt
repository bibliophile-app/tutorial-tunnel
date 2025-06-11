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

class BooklistRoutesTest {

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
            booklistRoutes()
        }
    }

    private suspend fun HttpClient.createBooklist(
        sessionCookie: String,
        listName: String,
        listDescription: String
    ): HttpResponse = post("/booklists") {
        header(HttpHeaders.ContentType, ContentType.Application.Json)
        header(HttpHeaders.Cookie, sessionCookie)
        setBody("""
            {
                "listName": "$listName",
                "listDescription": "$listDescription"
            }
        """.trimIndent())
    }

    private suspend fun HttpClient.addBookToBooklist(
        sessionCookie: String,
        booklistId: Int,
        bookId: String
    ): HttpResponse = post("/booklists/$booklistId/books") {
        header(HttpHeaders.ContentType, ContentType.Application.Json)
        header(HttpHeaders.Cookie, sessionCookie)
        setBody("""
            {
                "bookId": "$bookId"
            }
        """.trimIndent())
    }

    private suspend fun HttpClient.updateBooklist(
        sessionCookie: String,
        booklistId: Int
    ): HttpResponse = put("/booklists/$booklistId") {
        header(HttpHeaders.ContentType, ContentType.Application.Json)
        header(HttpHeaders.Cookie, sessionCookie)
        setBody("""
            {
                "listName": "New Name",
                "listDescription": "New Desc"
            }
        """.trimIndent())
    }

    private fun String.extractBooklistId(): Int {
        val regex = """Booklist ID:\s*(\d+)""".toRegex()
        val match = regex.find(this)
        return match?.groupValues?.get(1)?.toInt() ?: error("Booklist ID not found in response")
    }

    @Test
    fun `test create booklist`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")
        val response = client.createBooklist(sessionCookie, "My Booklist", "A list of my favorite books")

        assertEquals(HttpStatusCode.Created, response.status)
        assertTrue(response.bodyAsText().contains("Booklist created successfully"))
    }

    @Test
    fun `test create booklist with duplicate name`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")
        
        client.createBooklist(sessionCookie, "My Booklist", "Description of booklist")    
        val response = client.createBooklist(sessionCookie, "My Booklist", "Another description")

        assertEquals(HttpStatusCode.Conflict, response.status)
    }

    @Test
    fun `test create booklist without authentication`() = testApplication {
        application { setupTestModule() }

        val response = client.createBooklist("", "My Unauthorized Booklist", "Description for unauthorized user")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `test get all booklists`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")

        client.createBooklist(sessionCookie, "Booklist 1", "Description 1")
        client.createBooklist(sessionCookie, "Booklist 2", "Description 2")

        val response = client.get("/booklists") {
            header(HttpHeaders.Cookie, sessionCookie)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val booklists = response.bodyAsText()
        assertTrue(booklists.contains("Booklist 1"))
        assertTrue(booklists.contains("Booklist 2"))
    }

    @Test
    fun `test get booklist by ID`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")

        val createResponse = client.createBooklist(sessionCookie, "Booklist 1", "Description 1")
        val booklistId = createResponse.bodyAsText().extractBooklistId()
        
        val response = client.get("/booklists/$booklistId") {
            header(HttpHeaders.Cookie, sessionCookie)
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test get books from booklist by ID`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")

        val createResponse = client.createBooklist(sessionCookie, "Booklist 1", "Description 1")
        val booklistId = createResponse.bodyAsText().extractBooklistId()
        
        val response = client.get("/booklists/$booklistId/books") {
            header(HttpHeaders.Cookie, sessionCookie)
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `test get non-existent booklist`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")

        val response = client.get("/booklists/999") {
            header(HttpHeaders.Cookie, sessionCookie)
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("Booklist not found"))
    }

    @Test
    fun `test get books non-existent from booklist`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")

        val response = client.get("/booklists/999/books") {
            header(HttpHeaders.Cookie, sessionCookie)
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("Booklist not found"))
    }

    @Test
    fun `test add book to booklist`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")

        val createResponse = client.createBooklist(sessionCookie, "My Booklist", "A list of my favorite books")
        val booklistId = createResponse.bodyAsText().extractBooklistId()

        val response = client.addBookToBooklist(sessionCookie, booklistId, "book123")

        assertEquals(HttpStatusCode.Created, response.status)
        assertTrue(response.bodyAsText().contains("Book added to booklist successfully"))
    }

    @Test
    fun `test add book to non-existing booklist`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")

        val response = client.addBookToBooklist(sessionCookie, 999, "book123")
        assertEquals(HttpStatusCode.Conflict, response.status)
    }

    @Test
    fun `test delete booklist by owner`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")

        val createResponse = client.createBooklist(sessionCookie, "My Booklist", "A list of my favorite books")
        val booklistId = createResponse.bodyAsText().extractBooklistId()

        val response = client.delete("/booklists/$booklistId") {
            header(HttpHeaders.Cookie, sessionCookie)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Booklist deleted successfully"))
    }

    @Test
    fun `test delete booklist by non-owner returns forbidden`() = testApplication {
        application { setupTestModule() }

        val ownerCookie = client.registerAndLoginUser("owner@example.com", "owner", "pass123")
        val createResponse = client.createBooklist(ownerCookie, "Owner's List", "Desc")
        val booklistId = createResponse.bodyAsText().extractBooklistId()

        val intruderCookie = client.registerAndLoginUser("intruder@example.com", "intruder", "pass123")
        val response = client.delete("/booklists/$booklistId") {
            header(HttpHeaders.Cookie, intruderCookie)
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertTrue(response.bodyAsText().contains("You don't own this booklist"))
    }

    @Test
    fun `test delete book from booklist by owner`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("test@example.com", "testuser", "password123")
        val createResponse = client.createBooklist(sessionCookie, "My Booklist", "A list of my favorite books")
        val booklistId = createResponse.bodyAsText().extractBooklistId()

        client.addBookToBooklist(sessionCookie, booklistId, "book123")

        val response = client.delete("/booklists/$booklistId/books/book123") {
            header(HttpHeaders.Cookie, sessionCookie)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Book deleted from booklist successfully"))
    }

    @Test
    fun `test delete book by non-owner returns forbidden`() = testApplication {
        application { setupTestModule() }

        val ownerCookie = client.registerAndLoginUser("owner@example.com", "owner", "pass123")
        val createResponse = client.createBooklist(ownerCookie, "Owner's List", "Desc")
        val booklistId = createResponse.bodyAsText().extractBooklistId()

        client.addBookToBooklist(ownerCookie, booklistId, "book123")

        val intruderCookie = client.registerAndLoginUser("intruder@example.com", "intruder", "pass123")
        val response = client.delete("/booklists/$booklistId/books/book123") {
            header(HttpHeaders.Cookie, intruderCookie)
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
        assertTrue(response.bodyAsText().contains("You don't own this booklist"))
    }

    @Test
    fun `test update booklist by owner`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("owner@example.com", "owner", "pass123")
        val createResponse = client.createBooklist(sessionCookie, "Original Name", "Original Desc")
        val booklistId = createResponse.bodyAsText().extractBooklistId()
        val updateResponse = client.updateBooklist(sessionCookie, booklistId)

        assertEquals(HttpStatusCode.OK, updateResponse.status)
        assertTrue(updateResponse.bodyAsText().contains("Booklist updated successfully"))
    }

    @Test
    fun `test update booklist to existing booklist name`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("owner@example.com", "owner", "pass123")
        client.createBooklist(sessionCookie, "New Name", "")    
        
        val createResponse = client.createBooklist(sessionCookie, "Original Name", "Original Desc")
        val booklistId = createResponse.bodyAsText().extractBooklistId()
        val updateResponse = client.updateBooklist(sessionCookie, booklistId)

        assertEquals(HttpStatusCode.Conflict, updateResponse.status)
    }

    @Test
    fun `test update by non-owner returns forbidden`() = testApplication {
        application { setupTestModule() }

        val ownerCookie = client.registerAndLoginUser("owner@example.com", "owner", "pass123")
        val createResponse = client.createBooklist(ownerCookie, "Owner's List", "Desc")
        val booklistId = createResponse.bodyAsText().extractBooklistId()

        val intruderCookie = client.registerAndLoginUser("intruder@example.com", "intruder", "pass123")
        val updateResponse = client.updateBooklist(intruderCookie, booklistId)

        assertEquals(HttpStatusCode.Forbidden, updateResponse.status)
        assertTrue(updateResponse.bodyAsText().contains("You don't own this booklist"))
    }

    @Test
    fun `test update nonexistent booklist fails`() = testApplication {
        application { setupTestModule() }

        val sessionCookie = client.registerAndLoginUser("user@example.com", "user", "pass123")
        val updateResponse = client.updateBooklist(sessionCookie, 999)

        assertEquals(HttpStatusCode.Forbidden, updateResponse.status)
        assertTrue(updateResponse.bodyAsText().contains("don't own this booklist"))
    }
}