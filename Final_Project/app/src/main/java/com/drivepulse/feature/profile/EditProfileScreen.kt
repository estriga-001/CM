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
import com.drivepulse.core.designsystem.components.DrivePulseButton
import com.drivepulse.core.designsystem.theme.DpBackground
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpTextPrimary

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
                        android.widget.Toast.makeText(context, "Foto atualizada!", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        android.widget.Toast.makeText(context, "Erro a atualizar foto. Verifica a ligação ou o tamanho da imagem.", android.widget.Toast.LENGTH_LONG).show()
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
                title = { Text("Edit Profile", color = DpTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DpTextPrimary)
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
                        model = user.profileImageUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(text = "Tap to Add Photo", color = DpTextPrimary, style = MaterialTheme.typography.bodySmall)
                }
                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display Name") },
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
                label = { Text("Bio") },
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
                label = { Text("Car Brand") },
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
                label = { Text("Car Model") },
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
                label = { Text("Car Year") },
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
                text = "Save Changes",
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
