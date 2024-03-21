package com.example.server.models.entities

import com.example.models.entities.UserService.User
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ExposedWorkout(val name: String, val user: Int)
class WorkoutService(private val database: Database) {
    object Workout : IntIdTable() {
        val name = varchar("name", length = 255)
        val user = reference("user_id", User)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Workout)
        }
    }
}