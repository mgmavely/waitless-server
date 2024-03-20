package com.example.data.repository

import com.example.models.entities.ExposedGym
import com.example.models.entities.GymService
import com.example.models.entities.GymService.Gym
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
class GymRepository {
    fun createGym(gym: ExposedGym): EntityID<Int> {
        return transaction {
            Gym.insert {
                it[name] = gym.name
            } get Gym.id
        }
    }

    fun readGym(gymId: Int?): List<ExposedGym> {
        return if (gymId != null) {
            transaction {
                Gym.select { (Gym.id eq gymId) }
                    .mapNotNull { toExposedGym(it) }
            }
        } else {
            transaction {
                Gym.selectAll()
                    .mapNotNull { toExposedGym(it) }
            }
        }
    }

    fun updateGym(id: Int, gym: ExposedGym) {
        transaction {
            Gym.update({ Gym.id eq id})  {
                it[name] = gym.name
            }
        }
    }

    fun deleteGym( id: Int) {
        transaction {
            Gym.deleteWhere { Gym.id eq id }
        }
    }
}

private fun toExposedGym(row: ResultRow): ExposedGym =
    ExposedGym(
        name = row[Gym.name],
    )