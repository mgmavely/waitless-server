package com.example.server.data.repository

import com.example.models.entities.ExerciseService.Exercise
import com.example.models.entities.ExposedExercise
import com.example.server.models.entities.ExposedWorkout
import com.example.server.models.entities.WorkoutExerciseService.WorkoutExercise
import com.example.server.models.entities.WorkoutService.Workout
import kotlinx.serialization.Serializable
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

    @Serializable
    data class WorkoutWithExercises(val id: Int, val name: String, val exercises: List<ExposedExercise>)
    fun readWorkout(workoutId: Int): WorkoutWithExercises? {
        return transaction {
            (WorkoutExercise innerJoin Exercise innerJoin Workout)
                .slice(
                    Workout.id,
                    Workout.name,
                    Exercise.id,
                    Exercise.name,
                    Exercise.description,
                    Exercise.totalNumberOfMachines,
                    Exercise.numberOfMachinesAvailable,
                    Exercise.gymId,
                    Exercise.queueSize,
                    Exercise.targetMuscleGroup,
                    Exercise.formDescription,
                    Exercise.workingStatus,
                    Exercise.formVisual
                )
                .select { WorkoutExercise.workout eq workoutId }
                .groupBy({ it[Workout.id].value to it[Workout.name] }) { row ->
                    ExposedExercise(
                        row[Exercise.id].value,
                        row[Exercise.name],
                        row[Exercise.description],
                        row[Exercise.totalNumberOfMachines],
                        row[Exercise.numberOfMachinesAvailable],
                        row[Exercise.gymId].value,
                        row[Exercise.queueSize],
                        row[Exercise.targetMuscleGroup],
                        row[Exercise.formDescription],
                        row[Exercise.workingStatus],
                        row[Exercise.formVisual]
                    )
                }
                .map { (workoutIdName, exercises) ->
                    val (id, name) = workoutIdName
                    WorkoutWithExercises(id, name, exercises)
                }.firstOrNull()
        }
    }

    fun readWorkoutsByUser(userId: Int): List<WorkoutWithExercises?> {
        return transaction {
            Workout.select { Workout.user eq userId }
                .map { row ->
                    val workoutId = row[Workout.id].value
                    readWorkout(workoutId)
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
            WorkoutExercise.deleteWhere { workout eq workoutId }

            // Delete workout
            Workout.deleteWhere { Workout.id eq workoutId }
        }
    }
}
