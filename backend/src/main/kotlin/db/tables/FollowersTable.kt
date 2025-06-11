package com.bibliophile.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object FollowersTable : IntIdTable("followers") {

    val followerId = reference(
        name = "follower_id",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE
    )

    val followeeId = reference(
        name = "followee_id",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE
    )
}