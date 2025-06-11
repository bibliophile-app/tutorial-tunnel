package com.bibliophile.routes

import io.ktor.http.*
import java.sql.SQLException
import io.ktor.server.application.*
import io.ktor.server.response.*
import com.bibliophile.repositories.UserRepository

/**
 * Extension function to get a parameter value from the request.
 * 
 * @param param The name of the parameter to get
 * @return The parameter value or null if the parameter is missing or blank
 */
suspend fun ApplicationCall.getParam(param: String): String? {
    val paramValue = parameters[param]
    return if (paramValue.isNullOrBlank()) {
        respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing '$param' parameter"))
        null
    } else {
        paramValue
    }
}

/**
 * Extension function to get an integer parameter value from the request.
 * 
 * @param param The name of the parameter to get (defaults to "id")
 * @return The parameter value as Int or null if the parameter is missing or invalid
 */
suspend fun ApplicationCall.getIntParam(param: String = "id"): Int? {
    val paramValue = parameters[param]?.toIntOrNull()
    if (paramValue == null) {
        respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid or missing '$param' parameter"))
    }
    return paramValue
}

/**
 * Extension function to respond with a server error.
 * 
 * @param message The error message to include in the response
 */
suspend fun ApplicationCall.respondServerError(message: String) {
    respond(HttpStatusCode.InternalServerError, mapOf("message" to message))
}

/**
 * Extension function to handle SQL exceptions and respond with appropriate error messages.
 * 
 * @param ex The throwable to handle
 * @param customCheck Optional custom check function for SQL exceptions (defaults to always returning false)
 */
suspend fun ApplicationCall.respondSqlException(
    ex: Throwable,
    customCheck: (SQLException) -> Boolean = { false }
) {
    when (ex) {
        is SQLException -> {
            val message = when {
                customCheck(ex) -> "Database violation"
                ex.message?.contains("constraint", ignoreCase = true) == true -> "Database constraint violation"
                else -> "Database error: ${ex.message}"
            }
            respond(HttpStatusCode.Conflict, mapOf("message" to message))
        }
        else -> {
            respond(HttpStatusCode.BadRequest, mapOf("message" to "Unexpected error: ${ex.message}"))
        }
    }
}

/**
 * Extension function to resolve a user ID from either an ID or username parameter.
 * 
 * @param param The name of the parameter to use for lookup (defaults to "identifier")
 * @return The user ID or null if the user cannot be found
 */
suspend fun ApplicationCall.resolveUserIdOrRespondNotFound(
    param: String = "identifier"
): Int? {
    val identifier = parameters[param]
    if (identifier.isNullOrBlank()) {
        respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing '$param' parameter"))
        return null
    }

    val user = identifier.toIntOrNull()?.let { id ->
        UserRepository.findById(id)
    } ?: UserRepository.findByUsername(identifier)

    if (user == null) {
        respond(HttpStatusCode.NotFound, mapOf("message" to "User not found"))
        return null
    }

    return user.id
}