package com.example.models.entities

import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import com.example.models.entities.ExerciseService.Exercise
import com.example.models.entities.GymService.Gym

@Serializable
data class ExposedGymExercise(val gym: Int, val exercise: Int)
class GymExerciseService(private val database: Database) {
    // Many-to-Many relationship table between
    object GymExercise: Table() {
        val gym = reference("gym_id", Gym)
        val exercise = reference("exercise_id", Exercise)
        override val primaryKey = PrimaryKey(gym, exercise)
    }
    init {
        transaction(database) {
            SchemaUtils.create(GymExercise)
        }
    }
}