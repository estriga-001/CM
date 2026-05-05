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

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

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