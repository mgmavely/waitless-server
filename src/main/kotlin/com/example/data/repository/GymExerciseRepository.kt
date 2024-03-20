package com.example.data.repository

import com.example.models.entities.ExposedGymExercise
import com.example.models.entities.GymAdminService.GymAdmin
import com.example.models.entities.GymExerciseService.GymExercise
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
class GymExerciseRepository {
    fun createGymExercise(gymExercise: ExposedGymExercise): EntityID<Int> {
        return transaction {
            GymExercise.insert {
                it[gym] = gymExercise.gym
                it[exercise] = gymExercise.exercise
            } get GymExercise.exercise
        }
    }

    fun deleteGymExercise( gym: Int, exercise: Int) {
        transaction {
            GymAdmin.deleteWhere { (GymExercise.gym eq gym) and (GymExercise.exercise eq exercise) }
        }
    }
}