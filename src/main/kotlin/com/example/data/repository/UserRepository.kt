package com.example.data.repository

import com.example.models.entities.UserService.User
import com.example.models.entities.ExposedUser
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {
    fun createUser(authId: String, name: String, email: String, password: String): EntityID<Int> {
        return transaction {
            User.insert {
                it[User.name] = name
                it[User.email] = email
                it[User.password] = password
                it[User.authId] = authId
            } get User.id
        }
    }

    fun readUser(id: Int): ExposedUser? {
        return transaction {
            User.select { User.id eq id }
                .mapNotNull { toExposedUser(it) }
                .singleOrNull()
        }
    }

    fun updateUser(id: Int, user: ExposedUser) {
        transaction {
            User.update({ User.id eq id }) {
                it[name] = user.name
                it[email] = user.email
            }
        }
    }

    fun deleteUser(id: Int) {
        transaction {
            User.deleteWhere { User.id eq id }
        }
    }

    private fun toExposedUser(row: ResultRow): ExposedUser =
        ExposedUser(
            name = row[User.name],
            email = row[User.email],
            password = row[User.password],
            authId = row[User.authId]
        )
}
