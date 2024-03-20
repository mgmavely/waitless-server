package com.example.models.entities

import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.serialization.Serializable
import com.example.models.entities.GymService.Gym
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*

@Serializable
data class ExposedExercise(val name: String, val description: String?, val totalNumberOfMachines: Int, val numberOfMachinesAvailable: Int, val gymId: Int, val queueSize: Int)
class ExerciseService(private val database: Database) {
    object Exercise : IntIdTable() {
        val name = varchar("name", length = 255)
        val description = varchar("description", 1000).nullable()
        val totalNumberOfMachines = integer("total_number_of_machines")
        val numberOfMachinesAvailable = integer("number_of_machines_available")
        val gymId = reference("gym_id", Gym)
        val queueSize = integer("queue_size")


    }

    init {
        transaction(database) {
            SchemaUtils.create(Exercise)
        }
    }
}