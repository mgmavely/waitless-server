package com.example.models.entities

import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*

@Serializable
data class ExposedGym(val name: String)
class GymService(private val database: Database) {
    object Gym : IntIdTable() {
        val name = varchar("name", length = 255)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Gym)
        }
    }
}