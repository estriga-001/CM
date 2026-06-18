package com.drivepulse.feature.community.screens.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.drivepulse.R
import com.drivepulse.core.common.Constants
import com.drivepulse.core.designsystem.theme.DpCard
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpTextPrimary
import com.drivepulse.core.designsystem.theme.DpTextSecondary
import com.drivepulse.domain.model.Coordinate
import com.drivepulse.domain.model.Post
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DpCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            PostHeader(post = post)

            if (post.description.isNotBlank()) {
                Text(
                    text = post.description,
                    color = DpTextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 14.sp
                )
            }

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

            if (post.runId != null && post.runCoordinates.isNotEmpty()) {
                RoutePolylinePreview(coordinates = post.runCoordinates)
                RunStatistics(post = post)
            }

            PostActions(
                likesCount = post.likesCount,
                commentsCount = post.commentsCount,
                hasLiked = hasLiked,
                onLikeClick = onLikeClick,
                onCommentClick = onCommentClick
            )
        }
    }
}

@Composable
private fun PostHeader(post: Post) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
                Text(
                    text = post.username.take(1).uppercase(),
                    color = DpTextPrimary
                )
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
}

@Composable
private fun RoutePolylinePreview(coordinates: List<Coordinate>) {
    val normalizedPoints = remember(coordinates) {
        normalizeRouteCoordinates(coordinates)
    }
    val startPointColor = DpTextPrimary

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(DpTextSecondary.copy(alpha = 0.08f))
            .padding(16.dp)
    ) {
        if (normalizedPoints.size < 2) {
            return@Canvas
        }

        val points = normalizedPoints.map { point ->
            Offset(
                x = point.x * size.width,
                y = point.y * size.height
            )
        }

        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { point ->
                lineTo(point.x, point.y)
            }
        }

        drawPath(
            path = path,
            color = DpPrimaryRed,
            style = Stroke(
                width = 8f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        drawCircle(
            color = startPointColor,
            radius = 7f,
            center = points.first()
        )
        drawCircle(
            color = DpPrimaryRed,
            radius = 8f,
            center = points.last()
        )
    }
}

private fun normalizeRouteCoordinates(coordinates: List<Coordinate>): List<NormalizedPoint> {
    if (coordinates.size < 2) {
        return emptyList()
    }

    val minLatitude = coordinates.minOf { it.latitude }
    val maxLatitude = coordinates.maxOf { it.latitude }
    val minLongitude = coordinates.minOf { it.longitude }
    val maxLongitude = coordinates.maxOf { it.longitude }

    val latitudeRange = (maxLatitude - minLatitude).takeIf { it > 0.0 } ?: 1.0
    val longitudeRange = (maxLongitude - minLongitude).takeIf { it > 0.0 } ?: 1.0

    return coordinates.map { coordinate ->
        NormalizedPoint(
            x = ((coordinate.longitude - minLongitude) / longitudeRange).toFloat(),
            y = ((maxLatitude - coordinate.latitude) / latitudeRange).toFloat()
        )
    }
}

@Composable
private fun RunStatistics(post: Post) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            label = stringResource(R.string.stat_distance),
            value = "%.2f km".format(post.distanceMeters / 1000f)
        )
        StatItem(
            label = stringResource(R.string.stat_duration),
            value = formatDuration(post.durationSeconds)
        )
        StatItem(
            label = stringResource(R.string.stat_avg_speed),
            value = "%.1f km/h".format(post.avgSpeedKmh)
        )
    }
}

@Composable
private fun PostActions(
    likesCount: Int,
    commentsCount: Int,
    hasLiked: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onLikeClick) {
            Icon(
                imageVector = if (hasLiked) {
                    Icons.Filled.Favorite
                } else {
                    Icons.Filled.FavoriteBorder
                },
                contentDescription = stringResource(R.string.cd_like),
                tint = if (hasLiked) DpPrimaryRed else DpTextSecondary
            )
        }
        Text(
            text = likesCount.toString(),
            color = DpTextSecondary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.width(16.dp))

        IconButton(onClick = onCommentClick) {
            Icon(
                imageVector = Icons.Filled.ChatBubbleOutline,
                contentDescription = stringResource(R.string.cd_comment),
                tint = DpTextSecondary
            )
        }
        Text(
            text = commentsCount.toString(),
            color = DpTextSecondary,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = DpTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = DpTextSecondary,
            fontSize = 12.sp
        )
    }
}

private fun formatDuration(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}

@Composable
private fun formatDate(timestamp: Long): String {
    if (timestamp == 0L) {
        return stringResource(R.string.unknown)
    }

    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

private data class NormalizedPoint(
    val x: Float,
    val y: Float
)
