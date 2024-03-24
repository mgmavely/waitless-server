package com.example.routes

import com.example.data.repository.UserRepository
import com.example.models.entities.ExposedUser
import com.example.plugins.SupabaseClient
import com.example.server.models.entities.ExposedWorkout
import io.github.jan.supabase.gotrue.auth
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.put

@Serializable
data class CreateSessionRequest(
    var name: String,
    var email: String,
    var password: String
)
fun Route.sessionRoutes() {
    val userRepository = UserRepository()

    // Read specific workout
    get("/sessions") {
        val supabase = SupabaseClient.supabase
        val currentUser = supabase.auth.retrieveUserForCurrentSession(updateSession = true)
        val user = userRepository.readUserByAuthId(currentUser.id)
        if (user !== null) {
            call.respond(HttpStatusCode.OK, user)
        } else {
            call.respond(HttpStatusCode.InternalServerError, "You must login before you can use this endpoint")
        }
    }

    put("/sessions") {
        val supabase = SupabaseClient.supabase
        val currentUser = supabase.auth.retrieveUserForCurrentSession(updateSession = true)
        val user = userRepository.readUserByAuthId(currentUser.id)
        if (user !== null) {
            val request = call.receive<CreateSessionRequest>()
            if (request.email !== "") {
                user.email = request.email
                supabase.auth.modifyUser {
                    email = request.email
                }
            } else if (request.name !== "") {
                user.name = request.name
                supabase.auth.modifyUser {
                    data {
                        put("name", request.name)
                    }
                }
            } else if (request.password !== "") {
                user.password = request.password
                supabase.auth.modifyUser {
                    password = request.password
                }
            }

            userRepository.updateUserByAuthId(currentUser.id, user)
            call.respond(HttpStatusCode.OK)
        } else {
            call.respond(HttpStatusCode.InternalServerError, "You must login before you can use this endpoint")
        }
    }
}

