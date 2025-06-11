package com.bibliophile.models

import java.time.LocalDate
import kotlinx.serialization.Serializable
import com.bibliophile.utils.LocalDateSerializer

@Serializable
data class Review(
    val id: Int,
    val bookId: String,
    val username: String,
    val content: String?,
    val rate: Int,
    val favorite: Boolean,
    @Serializable(with = LocalDateSerializer::class)
    val reviewedAt: LocalDate
)

@Serializable
data class ReviewRequest(
    val bookId: String,
    val content: String?,
    val rate: Int,
    val favorite: Boolean,
    @Serializable(with = LocalDateSerializer::class)
    val reviewedAt: LocalDate
)