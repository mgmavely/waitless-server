package com.example.data.repository

import com.example.models.entities.ExerciseService.Exercise
import com.example.models.entities.ExposedExercise
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class ExerciseRepository {
    fun createExercise(exercise: ExposedExercise): EntityID<Int> {
        return transaction {
            Exercise.insert {
                it[name] = exercise.name
                it[description] = exercise.description
                it[totalNumberOfMachines] = exercise.totalNumberOfMachines
                it[numberOfMachinesAvailable] = exercise.numberOfMachinesAvailable
                it[gymId] = exercise.gymId
                it[queueSize] = exercise.queueSize
                it[targetMuscleGroup] = exercise.targetMuscleGroup
                it[formDescription] = exercise.formDescription
                it[workingStatus] = exercise.workingStatus
                it[formVisual] = exercise.formVisual
            } get Exercise.id
        }
    }

    fun readExercise(gymId: Int, equipmentId: Int?): List<ExposedExercise> {
        return if (equipmentId === null) {
            transaction {
                Exercise.select { (Exercise.gymId eq gymId) }
                    .mapNotNull { toExposedExercise(it) }
            }
        } else {
            transaction {
                Exercise.select { (Exercise.id eq equipmentId) and (Exercise.gymId eq gymId) }
                    .mapNotNull { toExposedExercise(it) }
            }
        }
    }

    fun updateExercise(gymId: Int, equipmentId: Int, exercise: ExposedExercise) {
        transaction {
            Exercise.update({ (Exercise.id eq equipmentId) and (Exercise.gymId eq gymId)})  {
                it[name] = exercise.name
                it[description] = exercise.description
                it[totalNumberOfMachines] = exercise.totalNumberOfMachines
                it[numberOfMachinesAvailable] = exercise.numberOfMachinesAvailable
                it[this.gymId] = exercise.gymId
                it[queueSize] = exercise.queueSize
                it[targetMuscleGroup] = exercise.targetMuscleGroup
                it[formDescription] = exercise.formDescription
                it[workingStatus] = exercise.workingStatus
                it[formVisual] = exercise.formVisual
            }
        }
    }
    fun deleteExercise(gymId: Int, exerciseId: Int) {
        transaction {
            Exercise.deleteWhere { (Exercise.id eq exerciseId) and (Exercise.gymId eq gymId) }
        }
    }

    private fun toExposedExercise(row: ResultRow): ExposedExercise =
        ExposedExercise(
            id = row[Exercise.id].value,
            name = row[Exercise.name],
            description = row[Exercise.description],
            totalNumberOfMachines = row[Exercise.totalNumberOfMachines],
            numberOfMachinesAvailable = row[Exercise.numberOfMachinesAvailable],
            gymId = row[Exercise.gymId].value,
            queueSize = row[Exercise.queueSize],
            targetMuscleGroup = row[Exercise.targetMuscleGroup],
            formDescription = row[Exercise.formDescription],
            workingStatus = row[Exercise.workingStatus],
            formVisual = row[Exercise.formVisual]
        )
}
