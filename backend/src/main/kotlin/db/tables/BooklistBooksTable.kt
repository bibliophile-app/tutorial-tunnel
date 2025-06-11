package com.bibliophile.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object BooklistBooksTable : IntIdTable("booklist_books") {
    val booklistId = reference(
        name = "booklist_id",
        foreign = BooklistsTable,
        onDelete = ReferenceOption.CASCADE
    )
    val bookId = varchar("book_id", 32)

    init {
        uniqueIndex("uq_book_per_list", bookId, booklistId)
    }
}