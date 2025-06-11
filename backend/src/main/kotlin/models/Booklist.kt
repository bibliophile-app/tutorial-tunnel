package com.bibliophile.models

import kotlinx.serialization.Serializable

@Serializable
data class Booklist(
    val id: Int,
    val userId: Int,
    val listName: String,
    val listDescription: String?,
)

@Serializable
data class BooklistWithBooks(
    val id: Int,
    val userId: Int,
    val listName: String,
    val listDescription: String?,
    val books: List<String>
)

@Serializable
data class BookRequest(
    val bookId: String,
)

@Serializable
data class BooklistRequest(
    val listName: String,
    val listDescription: String?,
)
