package com.bibliophile.db.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import com.bibliophile.db.tables.ReviewsTable

class ReviewDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ReviewDAO>(ReviewsTable)

    var bookId by ReviewsTable.bookId
    var userId by ReviewsTable.userId
    var content by ReviewsTable.content
    var rate by ReviewsTable.rate
    var favorite by ReviewsTable.favorite
    var reviewedAt by ReviewsTable.reviewedAt
}