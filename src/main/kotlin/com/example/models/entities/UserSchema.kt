package com.example.models.entities

import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class ExposedUser(
    val name: String,
    val email: String,
    val password: String,
    val authId: String
)
class UserService(private val database: Database) {
    object User : IntIdTable() {
        val authId = varchar("authId", 255).uniqueIndex()
        val email = varchar("email", 255).uniqueIndex()
        val password = varchar("password", 255)
        val name = varchar("display_name", 255)
    }
    init {
        transaction(database) {
            SchemaUtils.create(User)
        }
    }
}