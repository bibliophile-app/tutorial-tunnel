package com.bibliophile.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.application.*
import com.bibliophile.models.UserSession
import com.bibliophile.models.FollowRequest
import com.bibliophile.repositories.FollowerRepository


fun Route.followerRoutes() {

    route("followers") {
        get {
            runCatching {
                FollowerRepository.getAllFollows()
            }.onSuccess { list ->
                call.respond(HttpStatusCode.OK, list)
            }.onFailure {
                call.respondServerError("Failed to retrieve follows")
            }
        }

        get("/{identifier}/following") {
            val userId = call.resolveUserIdOrRespondNotFound() ?: return@get

            runCatching {
                if (userId < 0) throw IllegalArgumentException("User ID inválido")
                FollowerRepository.getFollowingUsers(userId)
            }.onSuccess { follows ->
                call.respond(HttpStatusCode.OK, follows)
            }.onFailure {
                call.respondServerError("Error retrieving following users")
            }
        }

        // retorna seguidores de um usuário
        get("/{identifier}/followers") {
            val userId = call.resolveUserIdOrRespondNotFound() ?: return@get

            runCatching {
                if (userId < 0) throw IllegalArgumentException("Invalid credentials")
                FollowerRepository.getFollowersOfUser(userId)
            }.onSuccess { followers ->
                call.respond(HttpStatusCode.OK, followers)
            }.onFailure {
                call.respondServerError("Error retrieving followers of user")
            }
        }

        // verificar se um usuário está seguindo outro
        get("/check") {
            val followerId = call.request.queryParameters["followerId"]?.toIntOrNull()
            val followeeId = call.request.queryParameters["followeeId"]?.toIntOrNull()
        
            if (followerId == null || followeeId == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Both user IDs are required"))
                return@get
            }
        
            runCatching {
                FollowerRepository.isFollowing(followerId, followeeId)
            }.onSuccess { isFollowing ->
                call.respond(HttpStatusCode.OK, mapOf("isFollowing" to isFollowing))
            }.onFailure {
                call.respondServerError("Error checking follow status")
            }
        }        


        authenticate("auth-session") {
            post {
                val session = call.sessions.get<UserSession>()
                val userId = session?.userId!!
                val follow = call.receive<FollowRequest>()

                runCatching {
                    if (userId == follow.followeeId) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("message" to "User cannot follow themselves"))
                        return@runCatching
                    }
                    
                    if (FollowerRepository.isFollowing(userId, follow.followeeId)) {
                        call.respond(HttpStatusCode.Conflict, mapOf("message" to "User is already following this person"))
                        return@runCatching
                    }
                    FollowerRepository.addFollow(userId, follow)
                }.onSuccess{
                    call.respond(HttpStatusCode.Created, mapOf("message" to "Follow created successfully"))
                }.onFailure {
                    call.respondServerError("Failed to create follow")
                }
            }

            delete {
                val session = call.sessions.get<UserSession>()
                val userId = session?.userId!!
                val follow = call.receive<FollowRequest>()

                runCatching {
                    FollowerRepository.deleteFollow(userId, follow)
                }.onSuccess {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Follow deleted successfully"))
                }.onFailure {
                    call.respondServerError("Failed to delete follow")
                }
            }
        }
    }
}