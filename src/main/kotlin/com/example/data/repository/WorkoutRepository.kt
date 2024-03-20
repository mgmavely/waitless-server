package com.example.data.repository

import com.example.models.entities.WorkoutService.Workout
import com.example.models.entities.ExposedWorkout
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class WorkoutRepository {
    fun createWorkout(workout: ExposedWorkout) {
        return transaction {
            Workout.insert {
                it[name] = workout.name
            } get Workout.id
        }
    }

    fun readUser(id: Int): ExposedWorkout? {
        return transaction {
            Workout.select { Workout.id eq id }
                .mapNotNull { toExposedWorkout(it) }
                .singleOrNull()
        }
    }

    fun updateWorkout(id: Int, workout: ExposedWorkout) {
        transaction {
            Workout.update({ Workout.id eq id }) {
                it[name] = Workout.name
            }
        }
    }

    fun deleteWorkout(id: Int) {
        transaction {
            Workout.deleteWhere { Workout.id eq id }
        }
    }

    private fun toExposedWorkout(row: ResultRow): ExposedWorkout =
        ExposedWorkout(
            name = row[Workout.name]
        )
}
