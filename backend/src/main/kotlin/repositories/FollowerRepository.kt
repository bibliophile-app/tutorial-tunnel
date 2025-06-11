package com.bibliophile.repositories

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.dao.id.EntityID

import com.bibliophile.db.daoToModel
import com.bibliophile.models.Follow
import com.bibliophile.models.FollowRequest
import com.bibliophile.db.entities.FollowerDAO
import com.bibliophile.db.tables.FollowersTable
import com.bibliophile.db.tables.UsersTable
import com.bibliophile.db.suspendTransaction

object FollowerRepository {

    // retorna todas as relações de follow 
    suspend fun getAllFollows(): List<Follow> = suspendTransaction {
        FollowerDAO.all().map(::daoToModel)
    }

    // retorna a lista de usuários que seguem um determinado usuário
    suspend fun getFollowersOfUser(userId: Int): List<Follow> = suspendTransaction {
        FollowerDAO.find { FollowersTable.followeeId eq userId }
                   .map(::daoToModel)
    }

    // obtém a lista de usuários que um determinado usuário está seguindo
    suspend fun getFollowingUsers(userId: Int): List<Follow> = suspendTransaction {
        FollowerDAO.find { FollowersTable.followerId eq userId }
                   .map(::daoToModel)
    }

    // cria uma nova relação de follow entre dois usuários
    suspend fun addFollow(userId: Int, follow: FollowRequest): Follow = suspendTransaction {
        FollowerDAO.new {
            followerId = EntityID(userId, UsersTable)
            followeeId  = EntityID(follow.followeeId, UsersTable)
        }.let(::daoToModel)
    }

    // verifica se um usuário já está seguindo outro
    suspend fun isFollowing(followerId: Int, followeeId: Int): Boolean = suspendTransaction {
        FollowerDAO.find {
            (FollowersTable.followerId eq followerId) and
            (FollowersTable.followeeId  eq followeeId)
        }.any()
    }

    // exclui uma relação de follow
    suspend fun deleteFollow(userId: Int, follow: FollowRequest): Boolean = suspendTransaction {
        val toDelete = FollowerDAO.find {
            (FollowersTable.followerId eq userId) and
            (FollowersTable.followeeId eq follow.followeeId)
        }
        toDelete.map { it.delete(); it }.isNotEmpty()
    }
}