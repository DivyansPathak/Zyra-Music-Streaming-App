package com.zyra.music.zyra.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


object HttpClientFactory {

    fun create() : HttpClient{
        return HttpClient(OkHttp){

            install(ContentNegotiation){
                json(json = Json { ignoreUnknownKeys = true })
            }
            install(HttpTimeout){
                socketTimeoutMillis = 50_000L
                requestTimeoutMillis = 50_000L
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }
}