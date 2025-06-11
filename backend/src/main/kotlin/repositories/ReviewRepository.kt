package com.bibliophile.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDate

import com.bibliophile.models.Review
import com.bibliophile.models.ReviewRequest
import com.bibliophile.db.entities.ReviewDAO
import com.bibliophile.db.tables.ReviewsTable
import com.bibliophile.db.tables.UsersTable
import com.bibliophile.db.suspendTransaction

object ReviewRepository {

    /** Busca uma review pelo ID com informações do usuário */
    suspend fun review(id: Int): Review? = suspendTransaction {
        (ReviewsTable innerJoin UsersTable)
            .slice(
                ReviewsTable.id,
                ReviewsTable.bookId,
                UsersTable.username,
                ReviewsTable.content,
                ReviewsTable.rate,
                ReviewsTable.favorite,
                ReviewsTable.reviewedAt
            )
            .select { ReviewsTable.id eq id }
            .singleOrNull()
            ?.let { row ->
                Review(
                    id = row[ReviewsTable.id].value,
                    bookId = row[ReviewsTable.bookId],
                    username = row[UsersTable.username],
                    content = row[ReviewsTable.content],
                    rate = row[ReviewsTable.rate],
                    favorite = row[ReviewsTable.favorite],
                    reviewedAt = row[ReviewsTable.reviewedAt]
                )
            }
    }

    /** Retorna todas as reviews com informações dos usuários */
    suspend fun allReviews(): List<Review> = suspendTransaction {
        (ReviewsTable innerJoin UsersTable)
            .slice(
                ReviewsTable.id,
                ReviewsTable.bookId,
                UsersTable.username,
                ReviewsTable.content,
                ReviewsTable.rate,
                ReviewsTable.favorite,
                ReviewsTable.reviewedAt
            )
            .selectAll()
            .map { row ->
                Review(
                    id = row[ReviewsTable.id].value,
                    bookId = row[ReviewsTable.bookId],
                    username = row[UsersTable.username],
                    content = row[ReviewsTable.content],
                    rate = row[ReviewsTable.rate],
                    favorite = row[ReviewsTable.favorite],
                    reviewedAt = row[ReviewsTable.reviewedAt]
                )
            }
    }

    /** Busca reviews por usuário com informações do usuário */
    suspend fun getReviewsByUserId(userId: Int): List<Review> = suspendTransaction {
        (ReviewsTable innerJoin UsersTable)
            .slice(
                ReviewsTable.id,
                ReviewsTable.bookId,
                UsersTable.username,
                ReviewsTable.content,
                ReviewsTable.rate,
                ReviewsTable.favorite,
                ReviewsTable.reviewedAt
            )
            .select { ReviewsTable.userId eq userId }
            .map { row ->
                Review(
                    id = row[ReviewsTable.id].value,
                    bookId = row[ReviewsTable.bookId],
                    username = row[UsersTable.username],
                    content = row[ReviewsTable.content],
                    rate = row[ReviewsTable.rate],
                    favorite = row[ReviewsTable.favorite],
                    reviewedAt = row[ReviewsTable.reviewedAt]
                )
            }
    }

    /** Busca reviews por Book ID com informações do usuário */
    suspend fun getReviewsById(bookId: String): List<Review> = suspendTransaction {
        (ReviewsTable innerJoin UsersTable)
            .slice(
                ReviewsTable.id,
                ReviewsTable.bookId,
                UsersTable.username,
                ReviewsTable.content,
                ReviewsTable.rate,
                ReviewsTable.favorite,
                ReviewsTable.reviewedAt
            )
            .select { ReviewsTable.bookId eq bookId }
            .map { row ->
                Review(
                    id = row[ReviewsTable.id].value,
                    bookId = row[ReviewsTable.bookId],
                    username = row[UsersTable.username],
                    content = row[ReviewsTable.content],
                    rate = row[ReviewsTable.rate],
                    favorite = row[ReviewsTable.favorite],
                    reviewedAt = row[ReviewsTable.reviewedAt]
                )
            }
    }

    /** Adiciona uma nova review e retorna a criada */
    suspend fun addReview(userId: Int, review: ReviewRequest): Review = suspendTransaction {
        val reviewDAO = ReviewDAO.new {
            this.userId = EntityID(userId, UsersTable)
            this.bookId = review.bookId
            this.content = review.content
            this.rate = review.rate
            this.favorite = review.favorite
            this.reviewedAt = review.reviewedAt
        }

        val username = (UsersTable)
            .slice(UsersTable.username)
            .select { UsersTable.id eq userId }
            .single()[UsersTable.username]

        Review(
            id = reviewDAO.id.value,
            bookId = reviewDAO.bookId,
            username = username,
            content = reviewDAO.content,
            rate = reviewDAO.rate,
            favorite = reviewDAO.favorite,
            reviewedAt = reviewDAO.reviewedAt
        )
    }

    /** Atualiza uma review existente */
    suspend fun updateReview(reviewId: Int, userId: Int, review: ReviewRequest): Boolean = suspendTransaction {
        val reviewDAO = ReviewDAO.findById(reviewId)
        if (reviewDAO != null && reviewDAO.userId.value == userId) {
            reviewDAO.apply {
                bookId = review.bookId
                content = review.content
                rate = review.rate
                favorite = review.favorite
                reviewedAt = review.reviewedAt
            }
            true
        } else {
            false
        }
    }
        
    /** Deleta uma review pelo ID e ID do usuário */
    suspend fun deleteReview(reviewId: Int, userId: Int): Boolean = suspendTransaction {
        val reviewDAO = ReviewDAO.findById(reviewId)
        if (reviewDAO != null && reviewDAO.userId.value == userId) {
            reviewDAO.delete()
            true
        } else {
            false
        }
    }
}