package com.drivepulse.feature.routedetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.drivepulse.R
import com.drivepulse.core.common.Constants
import com.drivepulse.core.designsystem.components.DrivePulseTopBar
import com.drivepulse.core.designsystem.theme.*
import com.drivepulse.domain.model.Post
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class RouteDetailActivity : AppCompatActivity() {

    private val viewModel: RouteDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val postId = intent.getStringExtra(Constants.EXTRA_ROUTE_ID) ?: ""
        if (postId.isNotEmpty()) {
            viewModel.loadRouteDetail(postId)
        }

        setContent {
            DrivePulseTheme {
                val uiState by viewModel.uiState.collectAsState()

                Scaffold(
                    topBar = {
                        DrivePulseTopBar(
                            title = stringResource(R.string.title_route_detail),
                            onBackClick = { finish() }
                        )
                    },
                    containerColor = DpBackground
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (val state = uiState) {
                            is RouteDetailUiState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center),
                                    color = DpPrimaryRed
                                )
                            }
                            is RouteDetailUiState.Error -> {
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = state.message,
                                        color = DpPrimaryRed,
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            is RouteDetailUiState.Success -> {
                                RouteDetailContent(post = state.post)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RouteDetailContent(post: Post) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Top Half: Map
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (post.runCoordinates.isNotEmpty()) {
                val polylinePoints = remember(post.runCoordinates) {
                    post.runCoordinates.map { LatLng(it.latitude, it.longitude) }
                }
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                        polylinePoints.firstOrNull() ?: LatLng(0.0, 0.0),
                        14f
                    )
                }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        mapType = MapType.NORMAL
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        mapToolbarEnabled = true
                    )
                ) {
                    Polyline(
                        points = polylinePoints,
                        color = DpPrimaryRed,
                        width = 8f
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(R.string.no_map_available), color = DpTextSecondary)
                }
            }
        }

        // Bottom Half: Stats & Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f)
                .background(DpBackground)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // User Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!post.userProfileImage.isNullOrEmpty()) {
                    AsyncImage(
                        model = Constants.getCoilDataModel(post.userProfileImage),
                        contentDescription = stringResource(R.string.cd_avatar),
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = post.username.take(1).uppercase(), color = DpTextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "@${post.username}",
                        color = DpTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(post.createdAt)),
                        color = DpTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            if (post.description.isNotBlank()) {
                Text(
                    text = post.description,
                    color = DpTextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Stats Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DpCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val distanceKm = post.distanceMeters / 1000f
                    val speedKmh = post.avgSpeedKmh
                    
                    val hours = post.durationSeconds / 3600
                    val minutes = (post.durationSeconds % 3600) / 60
                    val seconds = post.durationSeconds % 60
                    val durationStr = if (hours > 0) {
                        String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    } else {
                        String.format("%02d:%02d", minutes, seconds)
                    }

                    StatBox(value = String.format("%.2f km", distanceKm), label = stringResource(R.string.stat_distance))
                    StatBox(value = durationStr, label = stringResource(R.string.stat_duration))
                    StatBox(value = String.format("%.1f km/h", speedKmh), label = stringResource(R.string.stat_avg_speed))
                }
            }

            // Media image
            if (!post.mediaUrl.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = Constants.getCoilDataModel(post.mediaUrl),
                    contentDescription = stringResource(R.string.cd_post_image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StatBox(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = DpTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = DpTextSecondary,
            fontSize = 12.sp
        )
    }
}
