package com.example.routes

import com.example.data.repository.UserRepository
import com.example.models.entities.ExposedUser
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.plugins.SupabaseClient
import com.example.utils.EnvUtils.getEnvVariable
import io.github.jan.supabase.gotrue.auth

fun Route.userRoutes() {
    val userRepository = UserRepository()
    val supabase = SupabaseClient.supabase
    val adminGoTrueClient = supabase.auth.admin
    val supabaseServiceRoleKey = getEnvVariable("SUPABASE_SERVICE_ROLE_KEY")

    // Read user
    get("/users/{id}") {
        supabase.auth.importAuthToken(supabaseServiceRoleKey)
        val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")
        val user = userRepository.readUser(id)
        if (user != null) {
            call.respond(HttpStatusCode.OK, user)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }

    // Update user
    put("/users/{id}") {
        supabase.auth.importAuthToken(supabaseServiceRoleKey)
        val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")
        val user = call.receive<ExposedUser>()
        userRepository.updateUser(id, user)
        call.respond(HttpStatusCode.OK)
    }

    // Delete user
    delete("/users/{id}") {
        supabase.auth.importAuthToken(supabaseServiceRoleKey)
        val id = call.parameters["id"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid ID")
        val uid: String

        // Find uid and remove user for given id
        try {
            val user = userRepository.readUser(id)
            val users = adminGoTrueClient.retrieveUsers()
            if (user != null) {
                uid = user.authId
                adminGoTrueClient.deleteUser(uid)
            } else {
                call.respond(HttpStatusCode.NotFound, "User not found")
                return@delete
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to find user in Supabase: ${e.localizedMessage}")
            return@delete
        }

        // remove user from db
        try {
            userRepository.deleteUser(id)
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to delete user from DB: ${e.localizedMessage}")
            return@delete
        }
    }
}
