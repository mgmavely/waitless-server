package com.example.plugins

import com.example.models.entities.ExerciseService
import com.example.models.entities.GymAdminService
import com.example.models.entities.GymService
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import com.example.models.entities.UserService
import com.example.server.models.entities.WorkoutExerciseService
import com.example.server.models.entities.WorkoutService
import org.jetbrains.exposed.sql.transactions.transaction
import com.example.utils.EnvUtils.getEnvVariable

fun Application.configureDatabases() {
    val dbUrl = getEnvVariable("DATABASE_URL")
    val dbUser = getEnvVariable("DB_USER")
    val dbPassword = getEnvVariable("DB_PASSWORD")

    val database = Database.connect(
        url = dbUrl,
        driver = "org.postgresql.Driver",
        user = dbUser,
        password = dbPassword
    )

    transaction {
        UserService(database = database)
        ExerciseService(database = database)
        GymService(database = database)
        GymAdminService(database = database)
        WorkoutService(database = database)
        WorkoutExerciseService(database = database)
    }
}
