package com.example.dam_a15044coolweatherapp.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

// faz um pedido http à API -> recebe JSON -> converte para WeatherData

/*
Em Kotlin, object define um singleton, ou seja, um objeto com uma única instância partilhada na aplicação. 
Usei object aqui porque o WeatherApiClient funciona como um serviço de acesso à API: 
não preciso de criar vários objetos diferentes para fazer pedidos meteorológicos. 
Assim, tenho um único cliente HTTP configurado e reutilizo esse cliente sempre que quero chamar a API. 
Isto evita criar um novo HttpClient a cada pedido e centraliza a lógica de comunicação com a API num só sítio.
 */

object WeatherApiClient {
    private val client = HttpClient(Android) { // cria o cliente de internet 
        install(ContentNegotiation) { // conversao automatica de dados
            json(Json {
                prettyPrint = true // organiza com \n caso imprima JSON nos logs
                isLenient = true // torna o servidor mais tolerante a erros de formatação
                ignoreUnknownKeys = true // Importante para não quebrar se a API enviar campos extras
            })
        }
    }

    /*
    Aqui estou a criar um HttpClient do Ktor usando o engine Android, ou seja, 
    um cliente HTTP apropriado para fazer pedidos de rede numa app Android. 
    Depois instalo o plugin ContentNegotiation, que permite negociar e converter o conteúdo recebido. 
    Neste caso, uso json(Json { ... }) para dizer que as respostas JSON devem ser convertidas automaticamente para objetos Kotlin, 
    como WeatherData. O prettyPrint melhora a formatação quando necessário, o isLenient torna o parser mais flexível, 
    e o ignoreUnknownKeys = true é importante porque a API pode enviar campos extra que eu não defini nas minhas data classes. 
    Assim, a app não falha só porque recebeu informação adicional.
     */

    // uma função suspend permite que seja suspendida a meio de execução e retomada 
    // ou seja, neste caso, fazemos a chamada http API, pausamos a função e quando recebemos os dados de volta a função retoma
    // evitando congelamento da app
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