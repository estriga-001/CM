package com.example.dam_a15044coolweatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.content.ContextCompat
import com.example.dam_a15044coolweatherapp.ui.WeatherUI

/*
Quando o utilizador altera a latitude ou longitude na interface, os composables chamam funções 
do ViewModel, como updateLatitude() e updateLongitude(). 
O ViewModel guarda esses valores no estado da aplicação. 
Quando o utilizador carrega no botão de atualizar, a UI chama fetchWeather() no ViewModel. 
O ViewModel usa esses valores de latitude e longitude para chamar o WeatherApiClient, 
que está na camada data e faz o pedido à API. Quando recebe a resposta, 
o ViewModel atualiza o uiState. Como a UI em Compose observa esse estado, 
quando o estado muda, a interface é redesenhada automaticamente com os novos dados.
 */

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkLocationPermissions()

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    WeatherUI()
                }
            }
        }
    }

    private fun checkLocationPermissions() {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { /* ViewModel handles location in init */ }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}