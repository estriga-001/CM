package com.example.dam_a15044coolweatherapp

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import android.content.res.ColorStateList

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val weatherImage = findViewById<ImageView>(R.id.weatherImage)
        weatherImage.setImageResource(R.drawable.ic_sun)

        ImageViewCompat.setImageTintList(
            weatherImage,
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.cyber_cyan))
        )

        val tintColor = when (weatherCode) {
            0 -> R.color.cyber_cyan
            1, 2, 3 -> R.color.cyber_text_secondary
            45, 48 -> R.color.cyber_text_primary
            51, 53, 55, 61, 63, 65, 80, 81, 82 -> R.color.cyber_cyan
            95, 96, 99 -> R.color.cyber_magenta
            else -> R.color.cyber_text_primary
        }
        ImageViewCompat.setImageTintList(
            weatherImage,
            ColorStateList.valueOf(ContextCompat.getColor(this, tintColor))
        )
        // teste manual
        val weatherCode = 2

        val iconRes = getWeatherIcon(weatherCode)
        weatherImage.setImageResource(iconRes)
    }
    private fun getWeatherIcon(weatherCode: Int): Int {
        return when (weatherCode) {
            0 -> R.drawable.ic_sun
            1, 2, 3 -> R.drawable.ic_cloud
            45, 48 -> R.drawable.ic_fog
            51, 53, 55, 61, 63, 65 -> R.drawable.ic_rain
            95, 96, 99 -> R.drawable.ic_storm
            else -> R.drawable.ic_cloud
        }
    }
}