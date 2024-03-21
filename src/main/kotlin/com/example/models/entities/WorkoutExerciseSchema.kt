package com.example.server.models.entities

import com.example.models.entities.ExerciseService.Exercise
import com.example.server.models.entities.WorkoutService.Workout
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ExposedWorkoutExercise(val workout: Int, val exercise: Int)
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