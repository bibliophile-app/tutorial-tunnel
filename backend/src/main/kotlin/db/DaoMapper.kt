package com.bibliophile.db

import com.bibliophile.models.User
import com.bibliophile.models.Quote
import com.bibliophile.models.Review
import com.bibliophile.models.Follow
import com.bibliophile.models.Booklist
import com.bibliophile.db.entities.UserDAO
import com.bibliophile.db.entities.QuoteDAO
import com.bibliophile.db.entities.ReviewDAO
import com.bibliophile.db.entities.FollowerDAO
import com.bibliophile.db.entities.BooklistDAO

fun daoToModel(dao: UserDAO) = User(
    id = dao.id.value,
    email = dao.email,
    username = dao.username,
    passwordHash = dao.passwordHash
)

fun daoToModel(dao: BooklistDAO) = Booklist(
    id = dao.id.value,
    userId = dao.userId.value,
    listName = dao.listName,
    listDescription = dao.listDescription ?: "",
)

fun daoToModel(dao: QuoteDAO) = Quote (
    id = dao.id.value,
    userId = dao.userId.value,
    content = dao.content
)

fun daoToModel(dao: FollowerDAO) = Follow (
    id = dao.id.value,
    followerId = dao.followerId.value,
    followeeId = dao.followeeId.value
)
