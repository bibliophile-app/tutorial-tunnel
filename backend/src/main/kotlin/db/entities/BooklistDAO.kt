package com.bibliophile.db.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import com.bibliophile.db.tables.BooklistsTable

class BooklistDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BooklistDAO>(BooklistsTable)

    var userId by BooklistsTable.userId
    var listName by BooklistsTable.listName
    var listDescription by BooklistsTable.listDescription
}
