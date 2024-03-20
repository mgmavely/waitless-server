package com.example.models.entities

import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import com.example.models.entities.UserService.User
import com.example.models.entities.WorkoutService.Workout

@Serializable
data class ExposedUserWorkout(val user: Int, val workout: Int)
class UserWorkoutService(private val database: Database) {
    object UserWorkout : Table() {
        val user = reference("user_id", User)
        val workout = reference("workout_id", Workout)
        override val primaryKey = PrimaryKey(user, workout)
    }
    init {
        transaction(database) {
            SchemaUtils.create(UserWorkout)
        }
    }
}