package com.zyra.music.zyra.data.remote

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

object SupabaseClient {

    private const val SUPABASE_URL =
         "https://kjegpczagrbhbgtubhxa.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImtqZWdwY3phZ3JiaGJndHViaHhhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTU0OTk3NTMsImV4cCI6MjA3MTA3NTc1M30.-9RrnlOP3Vm1u_-0Wfdno5sPA91GcNryUVx4oVRqn3A"

    val supabase = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ){
        install(Postgrest)
        install(Auth){
            alwaysAutoRefresh = true
            autoLoadFromStorage = true
        }
        install(Storage)
        install(Realtime)
    }
}