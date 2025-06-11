package com.bibliophile.db.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import com.bibliophile.db.tables.FollowersTable

class FollowerDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FollowerDAO>(FollowersTable)

    var followerId by FollowersTable.followerId
    var followeeId by FollowersTable.followeeId 
}