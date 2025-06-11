package com.bibliophile.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object BooklistsTable : IntIdTable("booklists") {
    val userId = reference(
        name = "user_id",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE
    )
    val listName = varchar("list_name", 50)
    val listDescription = varchar("list_description", 255).nullable()

    init {
        uniqueIndex("uq_listname_per_user", userId, listName)
    }
}