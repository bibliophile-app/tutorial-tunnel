package com.bibliophile.models

import kotlinx.serialization.Serializable

@Serializable
data class Follow(
    val id: Int,
    val followerId: Int,
    val followeeId: Int
) 

@Serializable
data class FollowRequest(
    val followeeId: Int
)
