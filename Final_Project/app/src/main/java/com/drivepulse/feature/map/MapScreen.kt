package com.drivepulse.feature.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.drivepulse.domain.model.Post
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun MapRoute(
    onPinClick: (String) -> Unit,
    viewModel: MapViewModel = hiltViewModel()
) {
    val feedPosts by viewModel.feedPosts.collectAsStateWithLifecycle()
    
    MapScreen(
        feedPosts = feedPosts,
        onPinClick = onPinClick
    )
}

@Composable
fun MapScreen(
    feedPosts: List<Post>,
    onPinClick: (String) -> Unit
) {
    val context = LocalContext.current
    val hasLocationPermission = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        properties = MapProperties(isMyLocationEnabled = hasLocationPermission)
    ) {
        feedPosts.forEach { post ->
            if (post.runCoordinates.isNotEmpty()) {
                val firstCoord = post.runCoordinates.first()
                val latLng = LatLng(firstCoord.latitude, firstCoord.longitude)
                Marker(
                    state = MarkerState(position = latLng),
                    title = post.description.ifEmpty { "Run by @${post.username}" },
                    snippet = "Tap to view route",
                    onClick = {
                        onPinClick(post.id)
                        true
                    }
                )
            }
        }
    }
}
