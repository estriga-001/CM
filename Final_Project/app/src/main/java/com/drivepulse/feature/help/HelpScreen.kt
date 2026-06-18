package com.drivepulse.feature.help

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drivepulse.R
import com.drivepulse.core.designsystem.components.DrivePulseTopBar
import com.drivepulse.core.designsystem.theme.DpBackground
import com.drivepulse.core.designsystem.theme.DpPrimaryRed
import com.drivepulse.core.designsystem.theme.DpTextPrimary
import com.drivepulse.core.designsystem.theme.DpTextSecondary

@Composable
fun HelpScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DpBackground)
    ) {
        DrivePulseTopBar(
            title = stringResource(R.string.title_help_faq),
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            FaqItem(
                question = stringResource(R.string.help_faq_1_q),
                answer = stringResource(R.string.help_faq_1_a)
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            FaqItem(
                question = stringResource(R.string.help_faq_2_q),
                answer = stringResource(R.string.help_faq_2_a)
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            FaqItem(
                question = stringResource(R.string.help_faq_3_q),
                answer = stringResource(R.string.help_faq_3_a)
            )
        }
    }
}

@Composable
private fun FaqItem(question: String, answer: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = question,
            color = DpPrimaryRed,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = answer,
            color = DpTextSecondary,
            fontSize = 16.sp,
            lineHeight = 24.sp
        )
    }
}
