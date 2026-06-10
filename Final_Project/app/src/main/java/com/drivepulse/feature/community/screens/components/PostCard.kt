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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.drivepulse.core.designsystem.theme.DpCard
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpTextPrimary
import com.drivepulse.core.designsystem.theme.DpTextSecondary
import com.drivepulse.domain.model.Post
import com.drivepulse.core.common.Constants
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

import androidx.compose.ui.res.stringResource
import com.drivepulse.R

@Composable
fun PostCard(
    post: Post,
    hasLiked: Boolean = false,
    onLikeClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!post.userProfileImage.isNullOrEmpty()) {
                    AsyncImage(
                        model = Constants.getCoilDataModel(post.userProfileImage),
                        contentDescription = stringResource(R.string.cd_avatar),
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = post.username.take(1).uppercase(), color = DpTextPrimary)
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
                        text = formatDate(post.createdAt),
                        color = DpTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            if (post.description.isNotBlank()) {
                Text(
                    text = post.description,
                    color = DpTextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 14.sp
                )
            }

            // Media (Foto/Video)
            if (!post.mediaUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = Constants.getCoilDataModel(post.mediaUrl),
                    contentDescription = stringResource(R.string.cd_post_image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Mini Mapa Estático (liteMode) se tiver runId
            if (post.runId != null && post.runCoordinates.isNotEmpty()) {
                val polylinePoints = remember(post.runCoordinates) {
                    post.runCoordinates.map { LatLng(it.latitude, it.longitude) }
                }

            val cameraPositionState = rememberCameraPositionState {
                if (polylinePoints.isNotEmpty()) {
                    position = CameraPosition.fromLatLngZoom(polylinePoints.first(), 14f)
                }
            }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(DpTextSecondary.copy(alpha = 0.1f))
                ) {
                    GoogleMap(
                        modifier = Modifier.matchParentSize(),
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

                // Estatísticas da Run
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(label = stringResource(R.string.stat_distance), value = "%.2f km".format(post.distanceMeters / 1000f))
                    StatItem(label = stringResource(R.string.stat_duration), value = formatDuration(post.durationSeconds))
                    StatItem(label = stringResource(R.string.stat_avg_speed), value = "%.1f km/h".format(post.avgSpeedKmh))
                }
            }

            // Ações (Likes e Comentários)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        imageVector = if (hasLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = stringResource(R.string.cd_like),
                        tint = if (hasLiked) DpPrimaryRed else DpTextSecondary
                    )
                }
                Text(text = "${post.likesCount}", color = DpTextSecondary, fontSize = 14.sp)
                
                Spacer(modifier = Modifier.width(16.dp))
                
                IconButton(onClick = onCommentClick) {
                    Icon(
                        imageVector = Icons.Filled.ChatBubbleOutline,
                        contentDescription = stringResource(R.string.cd_comment),
                        tint = DpTextSecondary
                    )
                }
                Text(text = "${post.commentsCount}", color = DpTextSecondary, fontSize = 14.sp)
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

@Composable
private fun formatDate(timestamp: Long): String {
    if (timestamp == 0L) return stringResource(R.string.unknown)
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
