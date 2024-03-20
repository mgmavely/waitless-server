package com.example.models.entities

import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import com.example.models.entities.WorkoutService.Workout
import com.example.models.entities.ExerciseService.Exercise

@Serializable
data class ExposedWorkoutExercise(val user: Int, val workout: Int)
class WorkoutExerciseService(private val database: Database) {
    // Many-to-Many relationship table between Workout and Exercise
    object WorkoutExercise: Table() {
        val workout = reference("workout_id", Workout)
        val exercise = reference("exercise_id", Exercise)
        override val primaryKey = PrimaryKey(workout, exercise)
    }
    init {
        transaction(database) {
            SchemaUtils.create(WorkoutExercise)
        }
    }
}