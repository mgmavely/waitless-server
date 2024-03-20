package com.example.data.repository

import com.example.models.entities.ExposedGymAdmin
import com.example.models.entities.GymAdminService.GymAdmin
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
class GymAdminRepository {
    fun createGymAdmin(gymAdmin: ExposedGymAdmin): EntityID<Int> {
        return transaction {
            GymAdmin.insert {
                it[user] = gymAdmin.user
                it[gym] = gymAdmin.gym
            } get GymAdmin.user
        }
    }

    fun readGymAdmin(gymId: Int?): List<ExposedGymAdmin> {
            return transaction {
                GymAdmin.select { (GymAdmin.gym eq gymId) }
                    .mapNotNull { toExposedGymAdmin(it) }
            }
    }

    fun updateGymAdmin(gymId: Int, userId: Int, gymAdmin: ExposedGymAdmin) {
        transaction {
            GymAdmin.update({ (GymAdmin.gym eq gymId) and (GymAdmin.user eq userId)})  {
                it[gym] = gymAdmin.gym
                it[user] = gymAdmin.user
            }
        }
    }

    fun deleteGymAdmin( gym: Int, user: Int) {
        transaction {
            GymAdmin.deleteWhere { (GymAdmin.user eq user) and (GymAdmin.gym eq gym) }
        }
    }
}

private fun toExposedGymAdmin(row: ResultRow): ExposedGymAdmin =
    ExposedGymAdmin(
        gym = row[GymAdmin.gym].value,
        user = row[GymAdmin.user].value,
    )