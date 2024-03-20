package com.example.routes

import com.example.data.repository.GymAdminRepository
import com.example.data.repository.GymRepository
import com.example.models.entities.ExposedGym
import com.example.models.entities.ExposedGymAdmin
import com.example.plugins.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.gymRoutes() {
    val gymRepository = GymRepository()
    val gymAdminRepository = GymAdminRepository()

    // Create gym

    post("/gyms") {
        val supabase = SupabaseClient.supabase
        val session = supabase.auth.currentSessionOrNull()
        if (session === null) {
            call.respond(HttpStatusCode.InternalServerError, "You must login before you can use this endpoint")
        } else {
            val req = call.receive<ExposedGym>()
            try {
                val gym = gymRepository.createGym(req)
                call.respond(HttpStatusCode.Created, "Gym created successfully: ${gym.value}")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create gym: ${e.localizedMessage}")
            }
        }


    }

    // Get gym(s)

    get("/gyms") {
        val supabase = SupabaseClient.supabase
        val session = supabase.auth.currentSessionOrNull()
        if (session === null) {
            call.respond(HttpStatusCode.InternalServerError, "You must login before you can use this endpoint")
        } else {
            val id = call.parameters["id"]?.toIntOrNull()
            val gym = gymRepository.readGym(id)
            call.respond(HttpStatusCode.OK, gym)
        }
    }

    // Update gym

    put("/gyms/{id}") {
        val supabase = SupabaseClient.supabase
        val session = supabase.auth.currentSessionOrNull()
        if (session === null) {
            call.respond(HttpStatusCode.InternalServerError, "You must login before you can use this endpoint")
        } else {
            val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")
            val gym = call.receive<ExposedGym>()
            gymRepository.updateGym(id, gym)
            call.respond(HttpStatusCode.OK)
        }
    }

    // Delete gym

    delete("/gyms/{id}") {
        val supabase = SupabaseClient.supabase
        val session = supabase.auth.currentSessionOrNull()
        if (session === null) {
            call.respond(HttpStatusCode.InternalServerError, "You must login before you can use this endpoint")
        } else {
            val id =
                call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")
            gymRepository.deleteGym(id)
            call.respond(HttpStatusCode.OK)
        }
    }

    // Create gym admin

    post("/gyms/{gym_id}/admins") {
        val supabase = SupabaseClient.supabase
        val session = supabase.auth.currentSessionOrNull()
        if (session === null) {
            call.respond(HttpStatusCode.InternalServerError, "You must login before you can use this endpoint")
        } else {
            val req = call.receive<ExposedGymAdmin>()
            try {
                val gymAdmin = gymAdminRepository.createGymAdmin(req)
                call.respond(
                    HttpStatusCode.Created,
                    "Gym admin created successfully: ${gymAdmin.value}"
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "Failed to create gym admin: ${e.localizedMessage}"
                )
            }
        }
    }

    // Get gym admins

    get("/gyms/{gym_id}/admins") {
        val supabase = SupabaseClient.supabase
        val session = supabase.auth.currentSessionOrNull()
        if (session === null) {
            call.respond(HttpStatusCode.InternalServerError, "You must login before you can use this endpoint")
        } else {
            val id = call.parameters["gym_id"]?.toIntOrNull()
            val gym = gymAdminRepository.readGymAdmin(id)
            call.respond(HttpStatusCode.OK, gym)
        }
    }

    // Update gym admins

    put("/gyms/{gym_id}/admins/{admin_id}") {
        val supabase = SupabaseClient.supabase
        val session = supabase.auth.currentSessionOrNull()
        if (session === null) {
            call.respond(HttpStatusCode.InternalServerError, "You must login before you can use this endpoint")
        } else {
            val gymId = call.parameters["gym_id"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")
            val userId = call.parameters["admin_id"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")
            val gymAdmin = call.receive<ExposedGymAdmin>()
            gymAdminRepository.updateGymAdmin(gymId, userId, gymAdmin)
            call.respond(HttpStatusCode.OK)
        }
    }

    // Delete gym admin

    delete("/gyms/{gym_id}/admins/{admin_id}") {
        val supabase = SupabaseClient.supabase
        val session = supabase.auth.currentSessionOrNull()
        if (session === null) {
            call.respond(
                HttpStatusCode.InternalServerError,
                "You must login before you can use this endpoint"
            )
        } else {
            val gym_id = call.parameters["gym_id"]?.toIntOrNull() ?: throw IllegalArgumentException(
                "Invalid Gym ID"
            )
            val admin_id = call.parameters["admin_id"]?.toIntOrNull()
                ?: throw IllegalArgumentException("Invalid User ID")
            gymAdminRepository.deleteGymAdmin(gym_id, admin_id)
            call.respond(HttpStatusCode.OK)
        }
    }
}
