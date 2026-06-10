package com.drivepulse.feature.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.CachePolicy
import com.drivepulse.core.designsystem.components.DrivePulseButton
import com.drivepulse.core.designsystem.theme.DpBackground
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpTextPrimary

import com.drivepulse.core.common.Constants

import androidx.compose.ui.res.stringResource
import com.drivepulse.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    if (uiState !is ProfileUiState.Success) {
        Box(modifier = Modifier.fillMaxSize().background(DpBackground), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val user = (uiState as ProfileUiState.Success).user

    var displayName by remember { mutableStateOf(user.displayName) }
    var bio by remember { mutableStateOf(user.bio) }
    var carBrand by remember { mutableStateOf(user.selectedCarBrand) }
    var carModel by remember { mutableStateOf(user.selectedCarModel) }
    var carYear by remember { mutableStateOf(user.selectedCarYear.toString()) }
    var isLoading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isLoading = true
            val inputStream = context.contentResolver.openInputStream(it)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) {
                viewModel.uploadImage(bytes) { success ->
                    isLoading = false
                    if (success) {
                        android.widget.Toast.makeText(context, context.getString(R.string.toast_photo_updated), android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        android.widget.Toast.makeText(context, context.getString(R.string.toast_photo_update_error), android.widget.Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_edit_profile), color = DpTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back), tint = DpTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DpBackground
                )
            )
        },
        containerColor = DpBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (!user.profileImageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(Constants.getCoilDataModel(user.profileImageUrl))
                            .memoryCacheKey("${user.profileImageUrl}_${user.updatedAt}")
                            .diskCachePolicy(CachePolicy.DISABLED)
                            .build(),
                        contentDescription = stringResource(R.string.cd_profile_picture),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(text = stringResource(R.string.tap_to_add_photo), color = DpTextPrimary, style = MaterialTheme.typography.bodySmall)
                }
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text(stringResource(R.string.field_display_name)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DpPrimaryRed,
                    focusedLabelColor = DpPrimaryRed,
                    unfocusedTextColor = DpTextPrimary,
                    focusedTextColor = DpTextPrimary
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text(stringResource(R.string.field_bio)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DpPrimaryRed,
                    focusedLabelColor = DpPrimaryRed,
                    unfocusedTextColor = DpTextPrimary,
                    focusedTextColor = DpTextPrimary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = carBrand,
                onValueChange = { carBrand = it },
                label = { Text(stringResource(R.string.field_car_brand)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DpPrimaryRed,
                    focusedLabelColor = DpPrimaryRed,
                    unfocusedTextColor = DpTextPrimary,
                    focusedTextColor = DpTextPrimary
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = carModel,
                onValueChange = { carModel = it },
                label = { Text(stringResource(R.string.field_car_model)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DpPrimaryRed,
                    focusedLabelColor = DpPrimaryRed,
                    unfocusedTextColor = DpTextPrimary,
                    focusedTextColor = DpTextPrimary
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = carYear,
                onValueChange = { carYear = it },
                label = { Text(stringResource(R.string.field_car_year)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DpPrimaryRed,
                    focusedLabelColor = DpPrimaryRed,
                    unfocusedTextColor = DpTextPrimary,
                    focusedTextColor = DpTextPrimary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            DrivePulseButton(
                text = stringResource(R.string.btn_save_changes),
                onClick = {
                    val updatedUser = user.copy(
                        displayName = displayName,
                        bio = bio,
                        selectedCarBrand = carBrand,
                        selectedCarModel = carModel,
                        selectedCarYear = carYear.toIntOrNull() ?: 0
                    )
                    isLoading = true
                    viewModel.updateUser(updatedUser) {
                        isLoading = false
                        onBackClick()
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
