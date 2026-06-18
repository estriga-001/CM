/**
 * Ecrã de Perfil do utilizador.
 *
 * Camada: UI
 * Feature: Profile
 *
 * Mostra:
 * - Avatar, nome, bio, carro
 * - Estatísticas (runs, km, followers)
 * - Lista das publicações do utilizador
 * - Botões de ação (Editar, Settings, Premium, Logout)
 */
package com.drivepulse.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.drivepulse.core.common.AppResult
import com.drivepulse.core.common.Constants
import com.drivepulse.feature.routedetail.RouteDetailActivity
import android.content.Intent
import com.drivepulse.core.designsystem.components.DrivePulseButton
import com.drivepulse.core.designsystem.theme.DpBackground
import com.drivepulse.core.designsystem.theme.DpCard
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpTextPrimary
import com.drivepulse.core.designsystem.theme.DpTextSecondary
import com.drivepulse.feature.community.screens.components.PostCard

import androidx.compose.ui.res.stringResource
import com.drivepulse.R

@Composable
fun ProfileScreen(
    onEditProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onAboutClick: () -> Unit,
    onPremiumClick: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val userPosts by viewModel.userPosts.collectAsState()
    val profileStats by viewModel.profileStats.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DpBackground),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is ProfileUiState.Loading -> {
                CircularProgressIndicator(color = DpPrimaryRed)
            }
            is ProfileUiState.Error -> {
                Text(text = "${stringResource(R.string.error_loading_profile)}: ${state.message}", color = MaterialTheme.colorScheme.error)
            }
            is ProfileUiState.Success -> {
                val user = state.user
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- Cabeçalho do Perfil ---
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Avatar
                            if (!user.profileImageUrl.isNullOrEmpty()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(Constants.getCoilDataModel(user.profileImageUrl))
                                        .memoryCacheKey("${user.profileImageUrl}_${user.updatedAt}")
                                        .diskCachePolicy(CachePolicy.DISABLED)
                                        .build(),
                                    contentDescription = stringResource(R.string.cd_profile_picture),
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.displayName.take(1).uppercase(),
                                        color = DpTextPrimary,
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = user.displayName.ifEmpty { user.username },
                                color = DpTextPrimary,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            if (user.username.isNotEmpty()) {
                                Text(
                                    text = "@${user.username}",
                                    color = DpTextSecondary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            if (user.bio.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = user.bio,
                                    color = DpTextSecondary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            if (user.selectedCarBrand.isNotBlank()) {
                                Text(
                                    text = "${user.selectedCarBrand} ${user.selectedCarModel} ${user.selectedCarYear}",
                                    color = DpTextPrimary,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Estatísticas calculadas em tempo real a partir das runs locais
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem(
                                    label = stringResource(R.string.stat_runs),
                                    value = profileStats.totalRuns.toString()
                                )
                                StatItem(
                                    label = stringResource(R.string.stat_km),
                                    value = String.format("%.1f", profileStats.totalKm)
                                )
                                StatItem(
                                    label = stringResource(R.string.stat_minutes),
                                    value = profileStats.totalMinutes.toString()
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Botões de ação
                            DrivePulseButton(text = stringResource(R.string.btn_edit_profile), onClick = onEditProfileClick)
                            Spacer(modifier = Modifier.height(8.dp))
                            DrivePulseButton(text = stringResource(R.string.btn_settings), onClick = onSettingsClick)
                            Spacer(modifier = Modifier.height(8.dp))
                            DrivePulseButton(text = stringResource(R.string.btn_help), onClick = onHelpClick)
                            Spacer(modifier = Modifier.height(8.dp))
                            DrivePulseButton(text = stringResource(R.string.btn_about), onClick = onAboutClick)
                            Spacer(modifier = Modifier.height(8.dp))
                            DrivePulseButton(text = stringResource(R.string.btn_premium), onClick = onPremiumClick)
                            Spacer(modifier = Modifier.height(8.dp))
                            DrivePulseButton(
                                text = stringResource(R.string.btn_logout),
                                onClick = { viewModel.logout(onLogoutSuccess = onLoginClick) }
                            )

                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }

                    // --- Secção: As Minhas Publicações ---
                    item {
                        Text(
                            text = stringResource(R.string.my_posts),
                            color = DpTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )
                    }

                    when (val posts = userPosts) {
                        is AppResult.Success -> {
                            if (posts.data.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = stringResource(R.string.profile_no_posts_yet),
                                            color = DpTextSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                items(posts.data, key = { it.id }) { post ->
                                    PostCard(
                                        post = post,
                                        onClick = {
                                            val intent = Intent(context, RouteDetailActivity::class.java).apply {
                                                putExtra(Constants.EXTRA_ROUTE_ID, post.id)
                                            }
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                }
                            }
                        }
                        is AppResult.Error -> {
                            item {
                                Text(
                                    text = stringResource(R.string.error_loading_posts),
                                    color = DpPrimaryRed,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }

                    // Espaço para o BottomBar
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = DpTextPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(text = label, color = DpTextSecondary, style = MaterialTheme.typography.bodySmall)
    }
}
