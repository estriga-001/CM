package com.drivepulse.feature.community.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drivepulse.core.designsystem.theme.DpCard
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpTextPrimary
import com.drivepulse.core.designsystem.theme.DpTextSecondary
import com.drivepulse.domain.model.Run
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RunCard(
    run: Run,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DpCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header (Info do utilizador e data)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = run.title.ifBlank { "DrivePulse Run" },
                        color = DpTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Publicado a ${formatDate(run.startTime)}",
                        color = DpTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            // Mini Mapa Estático (liteMode)
            val polylinePoints = remember(run.coordinates) {
                run.coordinates.map { LatLng(it.latitude, it.longitude) }
            }

            val cameraPositionState = rememberCameraPositionState {
                if (polylinePoints.isNotEmpty()) {
                    position = CameraPosition.fromLatLngZoom(polylinePoints.first(), 14f)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(DpTextSecondary.copy(alpha = 0.1f))
            ) {
                if (polylinePoints.isNotEmpty()) {
                    GoogleMap(
                        modifier = Modifier.matchParentSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(
                            mapType = com.google.maps.android.compose.MapType.NORMAL
                        ),
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            scrollGesturesEnabled = false,
                            tiltGesturesEnabled = false,
                            rotationGesturesEnabled = false,
                            zoomGesturesEnabled = false
                        )
                    ) {
                        Polyline(
                            points = polylinePoints,
                            color = DpPrimaryRed,
                            width = 10f
                        )
                    }
                } else {
                    Text(
                        text = "Sem dados GPS",
                        modifier = Modifier.align(Alignment.Center),
                        color = DpTextSecondary
                    )
                }
            }

            // Estatísticas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "Distância", value = "%.2f km".format(run.distanceMeters / 1000f))
                StatItem(label = "Duração", value = formatDuration(run.durationSeconds))
                StatItem(label = "Vel. Média", value = "%.1f km/h".format(run.avgSpeedKmh))
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = DpTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = DpTextSecondary, fontSize = 12.sp)
    }
}

private fun formatDuration(totalSeconds: Long): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return if (h > 0) "%02d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}

private fun formatDate(timestamp: Long): String {
    if (timestamp == 0L) return "Desconhecido"
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
