package com.example.udlearning.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.udlearning.ui.theme.color.White
import com.example.udlearning.ui.theme.color.YellowText

@Composable
fun TitleHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "‹  ",
            color = White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { onBackClick() }
                .padding(end = 8.dp)
        )
        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = White)) {
                    append("UD")
                }
                withStyle(style = SpanStyle(color = YellowText)) {
                    append("LEARNING")
                }
            },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
