package com.app.ecomapp.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecomapp.ui.theme.Montserrat

@Composable
fun OtpInputField(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    otpLength: Int = 6,
    isError: Boolean = false
) {
    BasicTextField(
        value = otpValue,
        onValueChange = { input ->
            val filtered = input.filter { it.isDigit() }
            if (filtered.length <= otpLength) {
                onOtpChange(filtered)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = modifier.fillMaxWidth(),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (index in 0 until otpLength) {
                    val char = otpValue.getOrNull(index)?.toString().orEmpty()
                    val isFocused = otpValue.length == index || (otpValue.length == otpLength && index == otpLength - 1)
                    val isFilled = char.isNotEmpty()

                    val borderColor by animateColorAsState(
                        targetValue = when {
                            isError -> Color(0xFFDC2626)
                            isFocused -> Color(0xFF2563EB)
                            isFilled -> Color(0xFF2563EB).copy(alpha = 0.8f)
                            else -> Color(0xFFCBD5E1)
                        },
                        label = "borderColor"
                    )

                    val bgColor = if (isFocused) Color(0xFFF0F7FF) else Color(0xFFF8FAFC)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgColor)
                            .border(
                                BorderStroke(
                                    width = if (isFocused) 1.8.dp else 1.dp,
                                    color = borderColor
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Montserrat,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )
                    }

                    if (index < otpLength - 1) {
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }
        }
    )
}