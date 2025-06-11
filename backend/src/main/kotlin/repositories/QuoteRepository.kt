package com.bibliophile.repositories

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.dao.id.EntityID

import com.bibliophile.db.daoToModel
import com.bibliophile.models.Quote
import com.bibliophile.models.QuoteRequest
import com.bibliophile.db.entities.QuoteDAO
import com.bibliophile.db.tables.QuotesTable
import com.bibliophile.db.tables.UsersTable
import com.bibliophile.db.suspendTransaction

object QuoteRepository {

    /** Retorna todas as quotes */
    suspend fun allQuotes(): List<Quote> = suspendTransaction {
        QuoteDAO.all().map(::daoToModel)
    }

    /** Busca uma quote pelo ID */
    suspend fun quote(quoteId: Int): Quote? = suspendTransaction {
        QuoteDAO.findById(quoteId)?.let(::daoToModel)
    }

    /** Adiciona uma nova quote e retorna a criada */
    suspend fun addQuote(userId: Int, quote: QuoteRequest): Unit = suspendTransaction {
        QuoteDAO.new {
            this.userId = EntityID(userId, UsersTable)
            this.content = quote.content
        }
    }

    /** Atualiza uma quote existente */
    suspend fun editQuote(quoteId: Int, userId: Int, quote: QuoteRequest): Boolean = suspendTransaction {
        val quoteDAO = QuoteDAO.findById(quoteId)
        if (quoteDAO != null && quoteDAO.userId.value == userId) {
            quoteDAO.apply {
                this.content = quote.content
            }
            true
        } else {
            false
        }
    }

     /** Deleta uma quote pelo ID e ID do usuário */
    suspend fun deleteQuote(quoteId: Int, userId: Int): Boolean = suspendTransaction {
        val quoteDAO = QuoteDAO.findById(quoteId)
        if (quoteDAO != null && quoteDAO.userId.value == userId) {
            quoteDAO.delete()
            true
        } else {
            false
        }
    }

}