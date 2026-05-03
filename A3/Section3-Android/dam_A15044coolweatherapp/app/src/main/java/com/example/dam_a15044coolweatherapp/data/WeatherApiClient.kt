package com.example.dam_a15044coolweatherapp.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object WeatherApiClient {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true // Importante para não quebrar se a API enviar campos extras
            })
        }
    }

    suspend fun getWeather(lat: Double, lon: Double): WeatherData? {
        val reqString = buildString {
            append("https://api.open-meteo.com/v1/forecast?")
            append("latitude=${lat}&longitude=${lon}&")
            append("current_weather=true&")
            append("hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m")
        }

        println("Getting URL: $reqString")

        return try {
            client.get(reqString).body<WeatherData>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}