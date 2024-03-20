package com.example.routes
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import com.example.plugins.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import com.example.data.repository.UserRepository
import com.example.plugins.UserSession
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set

@Serializable
data class SignUpRequest(val name: String, val email: String, val password: String)
@Serializable
data class SignInRequest(val email: String, val password: String)

suspend fun signUp(e: String, p: String): Email.Result? {
    val supabase = SupabaseClient.supabase
    val result = supabase.auth.signUpWith(Email) {
        email = e
        password = p
    }
    return result
}

suspend fun signIn(e: String, p: String) {
    val supabase = SupabaseClient.supabase
    val result = supabase.auth.signInWith(Email) {
        email = e
        password = p
    }
    return result
}
suspend fun signOut() {
    val supabase = SupabaseClient.supabase
    return supabase.auth.signOut()
}

fun Route.authRoutes() {
    post("/auth/signup") {
        val signUpRequest = call.receive<SignUpRequest>()
        val uid: String

        // Attempt to create Supabase auth user
        try {
            val result = signUp(signUpRequest.email, signUpRequest.password)
            if (result === null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to sign up with Supabase")
                return@post
            } else {
                uid = result.id
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to sign up: ${e.localizedMessage}")
            return@post
        }

        // Attempt to create DB record
        try {
            val userRepository = UserRepository()
            val user = userRepository.createUser(uid, signUpRequest.name, signUpRequest.email, signUpRequest.password)
            call.respond(HttpStatusCode.Created, "User created successfully: ${user.value}")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to create user record: ${e.localizedMessage}")
        }
    }

    post("/auth/signin") {
        val signInRequest = call.receive<SignInRequest>()
        try {
            signIn(signInRequest.email, signInRequest.password)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to sign in: ${e.localizedMessage}")
        }

        try {
            val supabase = SupabaseClient.supabase
            val session = supabase.auth.currentSessionOrNull()
            if (session === null) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to read user record")
                return@post
            } else {
                call.sessions.set(UserSession(session.accessToken, session.refreshToken))
                call.respond(HttpStatusCode.OK, "User record read successfully: ${session}")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to read user record: ${e.localizedMessage}")
        }
    }

    post("/auth/signout") {
        try {
            signOut()
            call.sessions.clear<UserSession>()
            call.respond(HttpStatusCode.OK, "Signed out successfully")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to sign out: ${e.localizedMessage}")
        }
    }
}

