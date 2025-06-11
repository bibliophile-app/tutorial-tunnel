package com.bibliophile.repositories

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import com.bibliophile.db.daoToModel
import com.bibliophile.db.suspendTransaction
import com.bibliophile.models.User
import com.bibliophile.models.UserProfileResponse
import com.bibliophile.db.entities.*
import com.bibliophile.db.tables.*

object UserRepository {

    /** Retorna todos os usuários do banco */
    suspend fun getAllUsers(): List<User> = suspendTransaction {
        UserDAO.all().map(::daoToModel)
    }

    /** Busca um usuário pelo ID */
    suspend fun findById(id: Int): User? = suspendTransaction {
        UserDAO.findById(id)?.let(::daoToModel)
    }

    /** Busca um usuário pelo username */
    suspend fun findByUsername(username: String): User? = suspendTransaction {
        UserDAO.find { UsersTable.username eq username }
            .map(::daoToModel)
            .firstOrNull()
    }

    /** Retorna o perfil completo do usuário com suas reviews, booklists e quotes */
    suspend fun getUserProfile(userId: Int): UserProfileResponse? {
        val user = suspendTransaction {
            UserDAO.findById(userId)
        } ?: return null

        val reviews = ReviewRepository.getReviewsByUserId(userId)

        val (booklists, quotes) = suspendTransaction {
            val booklists = BooklistDAO.find { BooklistsTable.userId eq userId }.map(::daoToModel)
            val quotes = QuoteDAO.find { QuotesTable.userId eq userId }.map(::daoToModel)
            Pair(booklists, quotes)
        }

        return UserProfileResponse(
            id = user.id.value,
            username = user.username,
            booklists = booklists,
            quotes = quotes,
            reviews = reviews
        )
    }

    suspend fun authenticate(username: String, passwordHash: String): User? {
        val user = findByUsername(username)
        return if (user != null && user.passwordHash == passwordHash) user else null
    }

    /** Adiciona um novo usuário e retorna o usuário criado */
    suspend fun create(email: String, username: String, passwordHash: String): User = suspendTransaction {
        UserDAO.new {
            this.email = email
            this.username = username
            this.passwordHash = passwordHash
        }.let(::daoToModel)
    }

    /** Atualiza um usuário pelo ID */
    suspend fun update(user: User): Boolean = suspendTransaction {
        val userDAO = UserDAO.findById(user.id!!)
        if (userDAO != null) {
            userDAO.username = user.username
            userDAO.passwordHash = user.passwordHash
            true
        } else {
            false
        }
    }

    /** Deleta um usuário pelo ID */
    suspend fun delete(id: Int): Boolean = suspendTransaction {
        val deletedRows = UsersTable.deleteWhere { UsersTable.id eq id }
        deletedRows > 0
    }

    /** Deleta um usuário pelo username */
    suspend fun delete(username: String): Boolean = suspendTransaction {
        val deletedRows = UsersTable.deleteWhere { UsersTable.username eq username }
        deletedRows > 0
    }
}