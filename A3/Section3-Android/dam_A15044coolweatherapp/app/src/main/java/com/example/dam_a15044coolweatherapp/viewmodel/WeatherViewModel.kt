package com.example.dam_a15044coolweatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dam_a15044coolweatherapp.data.WeatherApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/*
O WeatherViewModel.kt é responsável por gerir o estado da aplicação e fazer a ligação entre 
a UI e a camada de dados. 
A data class WeatherUiState guarda todos os valores que a interface precisa de mostrar, 
como latitude, longitude, temperatura, velocidade e direção do vento, código meteorológico, 
pressão ao nível do mar e hora. 
O WeatherViewModel expõe esse estado através de uiState, 
para que a UI o observe. Quando o utilizador altera as coordenadas ou carrega no botão de atualizar, 
a UI chama funções do ViewModel, como updateLatitude(), updateLongitude() ou fetchWeather(). 
Depois, o ViewModel chama o WeatherApiClient, recebe os dados da API e atualiza o estado. 
Como a UI observa esse estado, o ecrã atualiza automaticamente.
*/

data class WeatherUiState(
    val latitude: Float = 38.7223f,
    val longitude: Float = -9.1393f,
    val temperature: Float = 0f,
    val windspeed: Float = 0f,
    val winddirection: Int = 0,
    val weathercode: Int = 0,
    val seaLevelPressure: Float = 0f,
    val time: String = ""
)

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    // viewmodel pode alterar o estado
    // UI so pode observar o estado
    private val _uiState = MutableStateFlow(WeatherUiState()) // estado interno do viewmodel, pode ser alterado dentro do viewmodel
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow() // versao publica desse estado. a ui consegue ler e observar alteraçoes

    init {
        //getUserLocation()
        fetchWeather()
    }

    fun updateLatitude(value: Float) {
        _uiState.update { it.copy(latitude = value) }
    }

    fun updateLongitude(value: Float) {
        _uiState.update { it.copy(longitude = value) }
    }

    private fun getUserLocation() {
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(getApplication<Application>())
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        _uiState.update {
                            it.copy(
                                latitude = location.latitude.toFloat(),
                                longitude = location.longitude.toFloat()
                            )
                        }
                    }
                    fetchWeather()
                }
                .addOnFailureListener { fetchWeather() }
        } catch (e: SecurityException) {
            fetchWeather()
        }
    }

    fun fetchWeather() {
        val lat = _uiState.value.latitude
        val lon = _uiState.value.longitude
        // lança uma coroutine. 
        // pega no codigo dentro das {} e executa em segundo plano para n bloquear o ecrã
        viewModelScope.launch {
            try {
                val weather = WeatherApiClient.getWeather(lat.toDouble(), lon.toDouble())
                if (weather != null) {
                    _uiState.update { state ->
                        state.copy(
                            temperature = weather.current_weather.temperature.toFloat(),
                            windspeed = weather.current_weather.windspeed.toFloat(),
                            winddirection = weather.current_weather.winddirection,
                            weathercode = weather.current_weather.weathercode,
                            // 12 referencia do meio dia
                            // getornull-> é para caso a api devolver uma lista vazia, a app n crashar, entao devolve so null
                            // operador elvis ?: 0.0 -> para definir um valor padrao caso o passo anterior seja devolvido null
                            // no final convertemos para float
                            seaLevelPressure = (weather.hourly.pressure_msl.getOrNull(12) ?: 0.0).toFloat(),
                            time = weather.current_weather.time
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}