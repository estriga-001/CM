package com.drivepulse.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drivepulse.core.designsystem.theme.DpBackground
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpTextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    uiState: OnboardingUiState,
    usernameState: UsernameState,
    onCheckUsername: (String) -> Unit,
    onSubmit: (username: String, firstName: String, lastName: String, carBrand: String, carModel: String, carYear: Int) -> Unit,
    onCancel: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var yearStr by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = DpBackground,
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (onCancel != null) {
                TopAppBar(
                    title = { Text("Configura o teu Perfil", color = DpTextPrimary) },
                    navigationIcon = {
                        IconButton(onClick = onCancel) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar",
                                tint = DpTextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DpBackground)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Bem-vindo ao DrivePulse",
                color = DpTextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Vamos configurar o teu perfil para entrares na comunidade.",
                color = DpTextPrimary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            // --- Informação Pessoal ---
            Text(
                text = "Informação Pessoal",
                color = DpTextPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { 
                    username = it 
                    onCheckUsername(it)
                },
                label = { Text("@username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                trailingIcon = {
                    when (usernameState) {
                        is UsernameState.Checking -> CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        is UsernameState.Available -> Icon(Icons.Default.CheckCircle, contentDescription = "Available", tint = Color(0xFF22C55E)) // DpSuccess
                        is UsernameState.Unavailable -> Icon(Icons.Default.Warning, contentDescription = "Unavailable", tint = DpPrimaryRed)
                        else -> {}
                    }
                },
                supportingText = {
                    if (usernameState is UsernameState.Unavailable) {
                        Text(text = usernameState.reason, color = DpPrimaryRed)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DpTextPrimary,
                    unfocusedTextColor = DpTextPrimary,
                    focusedBorderColor = if (usernameState is UsernameState.Unavailable) DpPrimaryRed else Color(0xFF22C55E),
                    cursorColor = DpPrimaryRed
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Primeiro Nome") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DpTextPrimary,
                        unfocusedTextColor = DpTextPrimary,
                        focusedBorderColor = DpPrimaryRed,
                        cursorColor = DpPrimaryRed
                    )
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Apelido") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = DpTextPrimary,
                        unfocusedTextColor = DpTextPrimary,
                        focusedBorderColor = DpPrimaryRed,
                        cursorColor = DpPrimaryRed
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- A Tua Máquina ---
            Text(
                text = "A Tua Máquina",
                color = DpTextPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text("Marca (ex: Porsche)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DpTextPrimary,
                    unfocusedTextColor = DpTextPrimary,
                    focusedBorderColor = DpPrimaryRed,
                    cursorColor = DpPrimaryRed
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("Modelo (ex: 911 GT3)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DpTextPrimary,
                    unfocusedTextColor = DpTextPrimary,
                    focusedBorderColor = DpPrimaryRed,
                    cursorColor = DpPrimaryRed
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = yearStr,
                onValueChange = { yearStr = it },
                label = { Text("Ano (ex: 2023)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DpTextPrimary,
                    unfocusedTextColor = DpTextPrimary,
                    focusedBorderColor = DpPrimaryRed,
                    cursorColor = DpPrimaryRed
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            if (uiState is OnboardingUiState.Error) {
                Text(
                    text = uiState.message,
                    color = DpPrimaryRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = {
                    val year = yearStr.toIntOrNull() ?: 0
                    onSubmit(username, firstName, lastName, brand, model, year)
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = uiState !is OnboardingUiState.Loading && usernameState is UsernameState.Available,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DpPrimaryRed,
                    disabledContainerColor = DpPrimaryRed.copy(alpha = 0.5f)
                )
            ) {
                if (uiState is OnboardingUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Começar a Conduzir", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
