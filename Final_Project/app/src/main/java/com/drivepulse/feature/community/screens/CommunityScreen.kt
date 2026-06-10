package com.drivepulse.feature.community.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drivepulse.core.designsystem.components.DrivePulseTopBar
import com.drivepulse.core.designsystem.theme.DpBackground
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpTextPrimary
import com.drivepulse.core.designsystem.theme.DpTextSecondary
import com.drivepulse.feature.community.CommunityUiState
import com.drivepulse.feature.community.CommunityViewModel
import com.drivepulse.feature.community.screens.components.PostCard

import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.drivepulse.core.common.Constants
import com.drivepulse.feature.routedetail.RouteDetailActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.res.stringResource
import com.drivepulse.R

@Composable
fun CommunityScreen(
    uiState: CommunityUiState,
    viewModel: CommunityViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val likedPostIds by viewModel.likedPostIds.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DpBackground)
    ) {
        DrivePulseTopBar(title = stringResource(R.string.title_community))

        when (uiState) {
            is CommunityUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DpPrimaryRed)
                }
            }

            is CommunityUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = uiState.message,
                        color = DpPrimaryRed,
                        textAlign = TextAlign.Center
                    )
                }
            }

            is CommunityUiState.Success -> {
                if (uiState.posts.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🌍", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.feed_empty_title),
                                color = DpTextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.feed_empty_desc),
                                color = DpTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.posts, key = { it.id }) { post ->
                            PostCard(
                                post = post,
                                hasLiked = likedPostIds.contains(post.id),
                                onLikeClick = { viewModel.toggleLike(post.id) },
                                onCommentClick = { /* TODO: Show comments sheet */ },
                                onClick = {
                                    val intent = Intent(context, RouteDetailActivity::class.java).apply {
                                        putExtra(Constants.EXTRA_ROUTE_ID, post.id)
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        }
                        
                        // Add some space at the bottom for the BottomBar
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}
