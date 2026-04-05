package com.example.dam_a15044coolweatherapp

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.ImageViewCompat
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Remove the manual setTheme calls to allow the system to handle it
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val latitudeInput = findViewById<EditText>(R.id.latitudeInput)
        val longitudeInput = findViewById<EditText>(R.id.longitudeInput)
        val updateButton = findViewById<Button>(R.id.updateButton)

        latitudeInput.setText("38.076")
        longitudeInput.setText("-9.12")

        fetchWeatherData(38.076, -9.12).start()

        updateButton.setOnClickListener {
            val lat = latitudeInput.text.toString().trim().toDoubleOrNull()
            val lon = longitudeInput.text.toString().trim().toDoubleOrNull()

            if (lat != null && lon != null) {
                fetchWeatherData(lat, lon).start()
            } else {
                latitudeInput.error = getString(R.string.invalid_value)
                longitudeInput.error = getString(R.string.invalid_value)
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
            return Gson().fromJson(
                InputStreamReader(it, "UTF-8"),
                WeatherData::class.java
            )
        }
    }

    private fun fetchWeatherData(lat: Double, lon: Double): Thread {
        return Thread {
            try {
                val weather = weatherApiCall(lat, lon)
                updateUI(weather)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateUI(request: WeatherData) {
        runOnUiThread {
            val weatherImage = findViewById<ImageView>(R.id.weatherImage)
            val pressureValue = findViewById<TextView>(R.id.pressureValue)
            val windDirectionValue = findViewById<TextView>(R.id.windDirectionValue)
            val windSpeedValue = findViewById<TextView>(R.id.windSpeedValue)
            val temperatureValue = findViewById<TextView>(R.id.temperatureValue)
            val timeValue = findViewById<TextView>(R.id.timeValue)

            pressureValue.text = "${request.hourly.pressure_msl.firstOrNull() ?: "--"} hPa"
            windDirectionValue.text = "${request.current_weather.winddirection}°"
            windSpeedValue.text = "${request.current_weather.windspeed} km/h"
            temperatureValue.text = "${request.current_weather.temperature} °C"
            timeValue.text = request.current_weather.time

            val weatherMap = getWeatherCodeMap()
            val wCode = weatherMap[request.current_weather.weathercode]

            val imageName = wCode?.image ?: "ic_cloud"
            val resId = resources.getIdentifier(imageName, "drawable", packageName)
            weatherImage.setImageResource(resId)

            // Determine if the current theme is night
            val isNightTheme = (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES

            val tintColor = when (request.current_weather.weathercode) {
                0 -> if (!isNightTheme) R.color.day_primary else R.color.night_primary
                1, 2, 3 -> if (!isNightTheme) R.color.day_text_secondary else R.color.night_text_secondary
                45, 48 -> if (!isNightTheme) R.color.day_text_primary else R.color.night_text_primary
                51, 53, 55, 61, 63, 65, 80, 81, 82 -> if (!isNightTheme) R.color.day_primary else R.color.night_primary
                95, 96, 99 -> if (!isNightTheme) R.color.day_secondary else R.color.night_secondary
                else -> if (!isNightTheme) R.color.day_text_primary else R.color.night_text_primary
            }

            ImageViewCompat.setImageTintList(
                weatherImage,
                ColorStateList.valueOf(ContextCompat.getColor(this, tintColor))
            )
        }
    }
}