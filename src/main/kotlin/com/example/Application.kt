package com.example

import com.example.plugins.*
import com.example.server.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.routing
import com.example.routes.authRoutes
import com.example.routes.exerciseRoutes
import com.example.routes.gymRoutes
import com.example.routes.userRoutes
import com.example.utils.EnvUtils.getEnvVariable

fun main() {
    val port = getEnvVariable("PORT").toInt()
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureRouting()
    configureSessions()
    routing {
        userRoutes()
        exerciseRoutes()
        authRoutes()
        gymRoutes()
    }
}
