package com.drivepulse.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drivepulse.core.designsystem.theme.DpBackground
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpTextPrimary

@Composable
fun ProfileSetupScreen(
    onSetupComplete: (brand: String, model: String, year: Int) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var yearStr by remember { mutableStateOf("") }

    Scaffold(
        containerColor = DpBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Configura a tua Máquina",
                color = DpTextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Que carro conduzes atualmente? Vamos criar o teu avatar personalizado.",
                color = DpTextPrimary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text("Marca (ex: Porsche)") },
                modifier = Modifier.fillMaxWidth(),
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = DpTextPrimary,
                    unfocusedTextColor = DpTextPrimary,
                    focusedBorderColor = DpPrimaryRed,
                    cursorColor = DpPrimaryRed
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    val year = yearStr.toIntOrNull() ?: 0
                    onSetupComplete(brand, model, year)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DpPrimaryRed)
            ) {
                Text("Gravar Perfil", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSkip,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text("Saltar por agora", color = DpTextPrimary.copy(alpha = 0.5f))
            }
        }
    }
}
