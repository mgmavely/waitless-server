package com.example.data.repository

import com.example.models.entities.ExposedUser
import com.example.models.entities.UserService.User
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

@Serializable
data class CreateSessionResponse(
    var name: String,
    var email: String,
    var password: String,
    var id: Int
)

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

    fun readUserByAuthId(authId: String): CreateSessionResponse? {
        return transaction {
            User.select { User.authId eq authId }
                .mapNotNull { CreateSessionResponse(it[User.name], it[User.email], it[User.password], it[User.id].value) }
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

    fun updateUserByAuthId(authId: String, user: CreateSessionResponse) {
        transaction {
            User.update({ User.authId eq authId }) {
                it[name] = user.name
                it[email] = user.email
                it[password] = user.password
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
