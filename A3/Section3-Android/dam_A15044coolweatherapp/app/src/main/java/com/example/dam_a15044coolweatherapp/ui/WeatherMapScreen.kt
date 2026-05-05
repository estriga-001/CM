package com.example.dam_a15044coolweatherapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun WeatherMapScreen(
    latitude: Float,
    longitude: Float,
    onBackClick: () -> Unit,
    onLocationSelected: (Float, Float) -> Unit
) {
    val initialLocation = remember(latitude, longitude) {
        LatLng(latitude.toDouble(), longitude.toDouble())
    }

    var selectedLocation by remember(latitude, longitude) {
        mutableStateOf(initialLocation)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 12f)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { clickedLocation ->
                selectedLocation = clickedLocation
            }
        ) {
            Marker(
                state = MarkerState(position = selectedLocation),
                title = "Localização selecionada",
                snippet = "Lat: ${selectedLocation.latitude}, Lon: ${selectedLocation.longitude}"
            )
        }

        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f)
            )
        ) {
            Text(
                text = "Toca no mapa para escolher uma localização.",
                modifier = Modifier.padding(12.dp)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Voltar")
            }

            Button(
                onClick = {
                    onLocationSelected(
                        selectedLocation.latitude.toFloat(),
                        selectedLocation.longitude.toFloat()
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Usar localização")
            }
        }
    }
}