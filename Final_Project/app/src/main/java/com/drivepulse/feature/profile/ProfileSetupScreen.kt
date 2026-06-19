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
import androidx.compose.ui.res.stringResource
import com.drivepulse.R
import com.drivepulse.domain.validation.CarYearValidator

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
    var yearTouched by remember { mutableStateOf(false) }

    val parsedYear = yearStr.toIntOrNull()
    val isYearValid = parsedYear != null && CarYearValidator.isValid(parsedYear)
    val showYearError = yearTouched && !isYearValid

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = DpBackground,
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (onCancel != null) {
                TopAppBar(
                    title = { Text(stringResource(R.string.title_setup_profile), color = DpTextPrimary) },
                    navigationIcon = {
                        IconButton(onClick = onCancel) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_back),
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
                text = stringResource(R.string.welcome_to_drivepulse),
                color = DpTextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.setup_profile_desc),
                color = DpTextPrimary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            // --- Informação Pessoal ---
            Text(
                text = stringResource(R.string.personal_info_section),
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
                        is UsernameState.Available -> Icon(Icons.Default.CheckCircle, contentDescription = stringResource(R.string.cd_available), tint = Color(0xFF22C55E)) // DpSuccess
                        is UsernameState.Unavailable -> Icon(Icons.Default.Warning, contentDescription = stringResource(R.string.cd_unavailable), tint = DpPrimaryRed)
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
                    label = { Text(stringResource(R.string.field_first_name)) },
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
                    label = { Text(stringResource(R.string.field_last_name)) },
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
                text = stringResource(R.string.car_info_section),
                color = DpTextPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text(stringResource(R.string.field_car_brand_hint)) },
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
                label = { Text(stringResource(R.string.field_car_model_hint)) },
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
                onValueChange = { newValue ->
                    if (newValue.length <= 4 && newValue.all(Char::isDigit)) {
                        yearStr = newValue
                        yearTouched = true
                    }
                },
                label = { Text(stringResource(R.string.field_car_year_hint)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = showYearError,
                supportingText = {
                    if (showYearError) {
                        Text(
                            text = stringResource(
                                R.string.error_car_year_range,
                                CarYearValidator.MIN_YEAR,
                                CarYearValidator.maxYear
                            )
                        )
                    }
                },
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
                    yearTouched = true
                    val validYear = parsedYear
                    if (validYear != null && CarYearValidator.isValid(validYear)) {
                        onSubmit(
                            username,
                            firstName,
                            lastName,
                            brand,
                            model,
                            validYear
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = uiState !is OnboardingUiState.Loading &&
                    usernameState is UsernameState.Available &&
                    isYearValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DpPrimaryRed,
                    disabledContainerColor = DpPrimaryRed.copy(alpha = 0.5f)
                )
            ) {
                if (uiState is OnboardingUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.btn_start_driving), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
