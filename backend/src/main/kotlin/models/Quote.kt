package com.bibliophile.models

import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    val id: Int,
    val userId: Int,
    val content: String
) 

@Serializable
data class QuoteRequest(
    val content: String
) 