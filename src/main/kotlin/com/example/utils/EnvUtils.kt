package com.example.utils

import io.github.cdimascio.dotenv.dotenv

object EnvUtils {
    private val dotenv = dotenv()

    fun getEnvVariable(key: String): String {
        val env = System.getenv("ENV") ?: "development"
        return if (env == "development") {
            dotenv[key] ?: throw IllegalStateException("Environment variable '$key' not found in dotenv.")
        } else {
            System.getenv(key) ?: throw IllegalStateException("Environment variable '$key' not found.")
        }
    }
}