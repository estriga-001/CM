package com.example.dam_a15044coolweatherapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.net.URL

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    val latitude = MutableLiveData<String>("38.7223")
    val longitude = MutableLiveData<String>("-9.1393")

    private val _pressure = MutableLiveData<String>()
    val pressure: LiveData<String> = _pressure

    private val _windDirection = MutableLiveData<String>()
    val windDirection: LiveData<String> = _windDirection

    private val _windSpeed = MutableLiveData<String>()
    val windSpeed: LiveData<String> = _windSpeed

    private val _temperature = MutableLiveData<String>()
    val temperature: LiveData<String> = _temperature

    private val _time = MutableLiveData<String>()
    val time: LiveData<String> = _time

    private val _weatherImageName = MutableLiveData<String>()
    val weatherImageName: LiveData<String> = _weatherImageName

    private val _weatherCode = MutableLiveData<Int>()
    val weatherCode: LiveData<Int> = _weatherCode

    init {
        getUserLocation()
    }

    private fun getUserLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication<Application>())
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        latitude.value = location.latitude.toString()
                        longitude.value = location.longitude.toString()
                        fetchWeather()
                    } else {
                        fetchWeather() // Fallback to default
                    }
                }
                .addOnFailureListener {
                    fetchWeather()
                }
        } catch (e: SecurityException) {
            fetchWeather() // Fallback to default if no permission
        }
    }

    fun onUpdateClick() {
        fetchWeather()
    }

    private fun fetchWeather() {
        val lat = latitude.value?.toDoubleOrNull() ?: 38.7223
        val lon = longitude.value?.toDoubleOrNull() ?: -9.1393

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val weather = weatherApiCall(lat, lon)
                updateUI(weather)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun weatherApiCall(lat: Double, lon: Double): WeatherData {
        val reqString = buildString {
            append("https://api.open-meteo.com/v1/forecast?")
            append("latitude=$lat&longitude=$lon&")
            append("current_weather=true&")
            append("hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m")
        }
        val url = URL(reqString)
        url.openStream().use {
            return Gson().fromJson(InputStreamReader(it, "UTF-8"), WeatherData::class.java)
        }
    }

    private fun updateUI(weather: WeatherData) {
        viewModelScope.launch(Dispatchers.Main) {
            _pressure.value = "${weather.hourly.pressure_msl.getOrNull(12) ?: "--"} hPa"
            _windDirection.value = "${weather.current_weather.winddirection}°"
            _windSpeed.value = "${weather.current_weather.windspeed} km/h"
            _temperature.value = "${weather.current_weather.temperature} °C"
            _time.value = weather.current_weather.time
            _weatherCode.value = weather.current_weather.weathercode
            
            _weatherImageName.value = getWeatherImageFromXml(weather.current_weather.weathercode)
        }
    }

    private fun getWeatherImageFromXml(code: Int): String {
        val context = getApplication<Application>().applicationContext
        try {
            val codesArray = context.resources.getStringArray(R.array.weather_codes)
            for (item in codesArray) {
                val parts = item.split("|")
                if (parts.size >= 3 && parts[0].toIntOrNull() == code) {
                    return parts[2]
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "ic_cloud"
    }
}