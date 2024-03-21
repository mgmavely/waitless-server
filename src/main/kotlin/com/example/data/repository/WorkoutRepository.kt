package com.example.server.data.repository

import com.example.server.models.entities.ExposedWorkout
import com.example.server.models.entities.ExposedWorkoutExercise
import com.example.server.models.entities.WorkoutExerciseService.WorkoutExercise
import com.example.server.models.entities.WorkoutService.Workout
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class WorkoutRepository {
    fun createWorkout(workout: ExposedWorkout, exercises: List<Int>) {
        transaction {
            val workoutId = Workout.insert {
                it[name] = workout.name
                it[user] = workout.user
            } get Workout.id

            exercises.forEach { exercise ->
                WorkoutExercise.insert {
                    it[this.workout] = workoutId
                    it[this.exercise] = exercise
                }
            }
        }
    }

    fun readWorkout(workoutId: Int): List<ExposedWorkoutExercise> {
        return transaction {
            WorkoutExercise.select {
                (WorkoutExercise.workout eq workoutId)
            }.map {
                ExposedWorkoutExercise(
                    it[WorkoutExercise.workout].value,
                    it[WorkoutExercise.exercise].value
                )
            }
        }
    }
    fun updateWorkout(workoutId: Int, workout: ExposedWorkout, exercises: List<Int>) {
        transaction {
            // Update workout
            Workout.update({ Workout.id eq workoutId }) {
                it[name] = workout.name
                it[user] = workout.user
            }

            // Delete existing workout exercises
            WorkoutExercise.deleteWhere { WorkoutExercise.workout eq workoutId }

            // Insert updated workout exercises
            exercises.forEach { exercise ->
                WorkoutExercise.insert {
                    it[this.workout] = workoutId
                    it[this.exercise] = exercise
                }
            }
        }
    }

    fun deleteWorkout(workoutId: Int) {
        transaction {
            // Delete associated workout exercises
            WorkoutExercise.deleteWhere { WorkoutExercise.workout eq workoutId }

            // Delete workout
            Workout.deleteWhere { Workout.id eq workoutId }
        }
    }

    private fun toExposedWorkout(row: ResultRow): ExposedWorkout =
        ExposedWorkout(
            name = row[Workout.name],
            user = row[Workout.user].value
        )
}
