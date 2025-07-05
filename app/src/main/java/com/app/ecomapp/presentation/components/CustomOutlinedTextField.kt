package com.app.ecomapp.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import com.compose.jetshop.R

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    isEnabled: Boolean = true
) {
    val customFont = FontFamily(Font(R.font.montserrat_medium)) // Replace with your font resource

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                fontFamily = customFont,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        },
        textStyle = TextStyle(
            fontFamily = customFont,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        ),
        leadingIcon = leadingIcon?.let {
            { Icon(imageVector = it, contentDescription = label) }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        enabled = isEnabled, // ✅ Controlled by state


    )
}

@Composable
fun CustomPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Password",
    modifier: Modifier = Modifier,
    iserror: Boolean
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val customFont = FontFamily(Font(R.font.montserrat_medium)) // Replace with your font resource

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                fontFamily = customFont,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        },
        textStyle = TextStyle(
            fontFamily = customFont,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        ),
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val icon =
                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = icon, contentDescription = "Toggle Password Visibility")
            }
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        isError = iserror
    )
}
