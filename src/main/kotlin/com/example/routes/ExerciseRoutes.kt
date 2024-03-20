package com.example.routes

import com.example.data.repository.ExerciseRepository
import com.example.models.entities.ExposedExercise
import com.example.plugins.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
fun Route.exerciseRoutes() {
    val exerciseRepository = ExerciseRepository()

    // Create exercise
    post("/gyms/{gym_id}/exercises") {
        val supabase = SupabaseClient.supabase
        val session = supabase.auth.currentSessionOrNull()
        if (session === null) {
            call.respond(HttpStatusCode.InternalServerError, "You must login before you can use this endpoint")
        } else {
            try {
                val body = call.receive<ExposedExercise>()
                val exercise = exerciseRepository.createExercise(body)
                call.respond(
                    HttpStatusCode.Created,
                    "Exercise created successfully: ${exercise.value}"
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "Failed to create exercise: ${e.localizedMessage}"
                )
            }
        }
    }

    // Read specific exercise
    get("/gyms/{gym_id}/exercises") {
        val supabase = SupabaseClient.supabase
        val session = supabase.auth.currentSessionOrNull()
        if (session === null) {
            call.respond(HttpStatusCode.InternalServerError, "You must login before you can use this endpoint")
        } else {
            val equipmentId = call.parameters["equipment_id"]?.toIntOrNull()
            val gymId = call.parameters["gym_id"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid Gym ID")
            val exercise = exerciseRepository.readExercise(gymId, equipmentId)
            call.respond(HttpStatusCode.OK, exercise)
        }
    }

    // Update exercise
    put("/gyms/{gym_id}/exercises/{exercise_id}") {
        val supabase = SupabaseClient.supabase
        val session = supabase.auth.currentSessionOrNull()
        if (session === null) {
            call.respond(HttpStatusCode.InternalServerError, "You must login before you can use this endpoint")
        } else {
            val gymId = call.parameters["gym_id"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid ID")
            val exerciseId = call.parameters["exercise_id"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid ID")
            val exercise = call.receive<ExposedExercise>()
            exerciseRepository.updateExercise(gymId, exerciseId, exercise)
            call.respond(HttpStatusCode.OK)
        }
    }

    // Delete exercise
    delete("/gyms/{gym_id}/exercises/{exercise_id}") {
        val supabase = SupabaseClient.supabase
        val session = supabase.auth.currentSessionOrNull()
        if (session === null) {
            call.respond(
                HttpStatusCode.InternalServerError,
                "You must login before you can use this endpoint"
            )
        } else {
            val exerciseId = call.parameters["exercise_id"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid ID")
            val gymId = call.parameters["gym_id"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid Gym ID")
            exerciseRepository.deleteExercise(gymId, exerciseId)
            call.respond(HttpStatusCode.OK)
        }
    }
}