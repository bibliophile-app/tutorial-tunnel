package com.bibliophile.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object QuotesTable : IntIdTable("quotes") {
    val userId = reference(
        name = "user_id",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE
    )
    val content = varchar("content", 255)
}