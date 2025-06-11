package com.bibliophile.db.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import com.bibliophile.db.tables.QuotesTable


class QuoteDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<QuoteDAO>(QuotesTable)

    var userId by QuotesTable.userId
    var content by QuotesTable.content
}
