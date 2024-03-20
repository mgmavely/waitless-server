package com.example.plugins
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.serializer.JacksonSerializer
import com.example.utils.EnvUtils.getEnvVariable

object SupabaseClient {
    private val supabaseUrl = getEnvVariable("SUPABASE_URL")
    private val supabaseKey = getEnvVariable("SUPABASE_ANON_KEY")

    val supabase = createSupabaseClient(supabaseUrl, supabaseKey) {
        defaultSerializer = JacksonSerializer()
        install(Auth) {
            alwaysAutoRefresh = false // default: true
            autoLoadFromStorage = false // default: true
        }
    }
}