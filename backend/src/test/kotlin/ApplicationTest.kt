package com.bibliophile

import kotlin.test.*
import io.ktor.http.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

import com.bibliophile.utils.*
import com.bibliophile.models.UserSession

class ApplicationTest {

   @Test
    fun `test module initialization`() = testApplication {
        application {
            module()
        }

        val response = client.get("/")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `test flyway migration alone`() {
        TestDatabaseFactory.init()
    }

    @Test
    fun `unauthenticated request returns 401`() = testApplication {
        application {
            module()
            routing {
                authenticate("auth-session") {
                    get("/protected") {
                        call.respond(HttpStatusCode.OK, "Access granted")
                    }
                }
            }
        }

        val response = client.get("/protected")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertTrue(response.bodyAsText().contains("Not authenticated"))
    }

    @Test
    fun `authenticated session grants access`() = testApplication {
        application {
            module()
            routing {
                authenticate("auth-session") {
                    get("/protected") {
                        call.respond(HttpStatusCode.OK, "Access granted")
                    }
                }
            }
        }

        val sessionCookie = createSessionCookie(UserSession(userId = 1)) // veja função abaixo

        val response = client.get("/protected") {
            header(HttpHeaders.Cookie, sessionCookie)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Access granted", response.bodyAsText())
    }

    @Test
    fun `invalid session triggers challenge`() = testApplication {
        application {
            module()

            routing {
                authenticate("auth-session") {
                    get("/protected") {
                        call.respondText("Hello, authenticated user!")
                    }
                }
            }
        }

        val invalidSession = java.net.URLEncoder.encode("""{"userId":null}""", "UTF-8")
        val response = client.get("/protected") {
            header(HttpHeaders.Cookie, "USER_SESSION=$invalidSession")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertTrue(response.bodyAsText().contains("Not authenticated"))
    }
}
