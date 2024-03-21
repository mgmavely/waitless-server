package com.example.server.routes

import com.example.plugins.SupabaseClient
import com.example.server.data.repository.WorkoutRepository
import com.example.server.models.entities.ExposedWorkout
import io.github.jan.supabase.gotrue.auth
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import kotlinx.serialization.Serializable

@Serializable
data class CreateWorkoutRequest(
    val workout: ExposedWorkout,
    val exercises: List<Int>
)
fun Route.workoutRoutes() {
    val workoutRepository = WorkoutRepository()
    // Create workout
    post("/workouts") {
        val supabase = SupabaseClient.supabase
        val session = supabase.auth.currentSessionOrNull()
        if (session === null) {
            call.respond(HttpStatusCode.InternalServerError, "You must login before you can use this endpoint")
        } else {
            try {
                val request = call.receive<CreateWorkoutRequest>()
                workoutRepository.createWorkout(request.workout, request.exercises)
                call.respond(HttpStatusCode.Created, "Workout created successfully")
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "Failed to create workout: ${e.localizedMessage}"
                )
            }
        }
    }

    // Read specific workout
    get("workouts/{workout_id}") {
        val supabase = SupabaseClient.supabase
        val session = supabase.auth.currentSessionOrNull()
        if (session === null) {
            call.respond(HttpStatusCode.InternalServerError, "You must login before you can use this endpoint")
        } else {
            val workoutId = call.parameters["workout_id"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid Workout ID")
            val workout = workoutRepository.readWorkout(workoutId)
            if (workout != null) {
                call.respond(HttpStatusCode.OK, workout)
            } else {
                call.respond(HttpStatusCode.NotFound, "Workout not found")
            }
        }
    }

    //Read users workouts
    get("workouts/user/{user_id}") {
        val supabase = SupabaseClient.supabase
        val session = supabase.auth.currentSessionOrNull()
        if (session === null) {
            call.respond(HttpStatusCode.InternalServerError, "You must login before you can use this endpoint")
        } else {
            val userId = call.parameters["user_id"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid User ID")

            val workouts = workoutRepository.readWorkoutsByUser(userId)
            call.respond(HttpStatusCode.OK, workouts)
        }
    }

    // Update workout
    put("workouts/{workout_id}") {
        val supabase = SupabaseClient.supabase
        val session = supabase.auth.currentSessionOrNull()
        if (session === null) {
            call.respond(HttpStatusCode.InternalServerError, "You must login before you can use this endpoint")
        } else {
            val workoutId = call.parameters["workout_id"]?.toIntOrNull()
            if (workoutId !== null) {
                val request = call.receive<CreateWorkoutRequest>()
                workoutRepository.updateWorkout(workoutId, request.workout, request.exercises)
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid Workout ID")
            }

        }
    }

    // Delete workout
    delete("workouts/{workout_id}") {
        val supabase = SupabaseClient.supabase
        val session = supabase.auth.currentSessionOrNull()
        if (session === null) {
            call.respond(HttpStatusCode.InternalServerError, "You must login before you can use this endpoint")
        } else {
            val workoutId = call.parameters["workout_id"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid Workout ID")
            workoutRepository.deleteWorkout(workoutId)
            call.respond(HttpStatusCode.OK)
        }
    }
}
