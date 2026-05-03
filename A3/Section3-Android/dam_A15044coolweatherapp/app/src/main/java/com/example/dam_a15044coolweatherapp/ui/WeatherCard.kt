package com.example.dam_a15044coolweatherapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dam_a15044coolweatherapp.R

@Composable
fun WeatherCard(
    temperature: Float,
    windSpeed: Float,
    windDirection: Int,
    weathercode: Int,
    seaLevelPressure: Float,
    time: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.weather_card_title),
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            WeatherRow(
                label = stringResource(R.string.temperature_label),
                value = "$temperature °C"
            )
            WeatherRow(
                label = stringResource(R.string.wind_speed_label),
                value = "$windSpeed km/h"
            )
            WeatherRow(
                label = stringResource(R.string.wind_direction_label),
                value = "$windDirection°"
            )
            WeatherRow(
                label = stringResource(R.string.pressure_label),
                value = "$seaLevelPressure hPa"
            )
            WeatherRow(
                label = stringResource(R.string.time_label),
                value = time
            )
        }
    }
}