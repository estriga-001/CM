/**
 * Ecrã de criação/publicação de posts.
 *
 * Camada: UI
 * Feature: CreatePost
 *
 * Permite ao utilizador:
 * - Ver preview da run (mapa + estatísticas).
 * - Escrever uma descrição.
 * - Escolher uma fotografia da galeria.
 * - Publicar o post no feed da comunidade.
 */
package com.drivepulse.feature.createpost

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.res.stringResource
import com.drivepulse.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.drivepulse.core.designsystem.components.DrivePulseButton
import com.drivepulse.core.designsystem.theme.DpBackground
import com.drivepulse.core.designsystem.theme.DpCard
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpSurface
import com.drivepulse.core.designsystem.theme.DpTextPrimary
import com.drivepulse.core.designsystem.theme.DpTextSecondary
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    runId: String,
    onBackClick: () -> Unit,
    onPublished: () -> Unit,
    viewModel: CreatePostViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Navega automaticamente após publicação bem-sucedida
    LaunchedEffect(uiState.isPublished) {
        if (uiState.isPublished) onPublished()
    }

    // Launcher para selecionar imagem da galeria
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(it)?.readBytes()
            viewModel.onMediaSelected(bytes)
        }
    }

    Scaffold(
        containerColor = DpBackground,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_create_post), color = DpTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = DpTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DpSurface)
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = DpPrimaryRed)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Preview do mapa (se tiver coordenadas)
            if (uiState.runCoordinates.isNotEmpty()) {
                val polylinePoints = remember(uiState.runCoordinates) {
                    uiState.runCoordinates.map { LatLng(it.latitude, it.longitude) }
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
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(mapType = com.google.maps.android.compose.MapType.NORMAL),
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            scrollGesturesEnabled = false,
                            tiltGesturesEnabled = false,
                            rotationGesturesEnabled = false,
                            zoomGesturesEnabled = false
                        )
                    ) {
                        Polyline(points = polylinePoints, color = DpPrimaryRed, width = 10f)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Estatísticas da run
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DpCard, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatColumn(label = stringResource(R.string.stat_distance), value = "%.2f km".format(uiState.distanceMeters / 1000f))
                    StatColumn(label = stringResource(R.string.stat_duration), value = formatDuration(uiState.durationSeconds))
                    StatColumn(label = stringResource(R.string.stat_avg_speed), value = "%.1f km/h".format(uiState.avgSpeedKmh))
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Campo de descrição
            Text(stringResource(R.string.field_description), color = DpTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.onDescriptionChanged(it) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text(stringResource(R.string.placeholder_describe_route), color = DpTextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DpTextPrimary,
                    unfocusedTextColor = DpTextPrimary,
                    focusedBorderColor = DpPrimaryRed,
                    unfocusedBorderColor = DpTextSecondary.copy(alpha = 0.3f),
                    cursorColor = DpPrimaryRed,
                    focusedContainerColor = DpCard,
                    unfocusedContainerColor = DpCard
                ),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Seleção de foto
            Text(stringResource(R.string.field_photo_optional), color = DpTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.mediaBytes != null) {
                // Preview da imagem selecionada
                val bitmap = remember(uiState.mediaBytes) {
                    BitmapFactory.decodeByteArray(uiState.mediaBytes, 0, uiState.mediaBytes!!.size)
                }
                if (bitmap != null) {
                    androidx.compose.foundation.Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = stringResource(R.string.cd_preview),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                // Botão para adicionar foto
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(2.dp, DpTextSecondary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .background(DpCard)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.AddAPhoto,
                            contentDescription = stringResource(R.string.cd_add_photo),
                            tint = DpTextSecondary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(R.string.tap_to_add_photo), color = DpTextSecondary, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Erro
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Botão de publicar
            DrivePulseButton(
                text = if (uiState.isPublishing) stringResource(R.string.publishing) else stringResource(R.string.btn_publish),
                onClick = { if (!uiState.isPublishing) viewModel.publish() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
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
