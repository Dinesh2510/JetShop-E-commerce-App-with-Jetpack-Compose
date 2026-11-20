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
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions

import androidx.compose.material3.OutlinedTextFieldDefaults

import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.input.ImeAction

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecomapp.ui.theme.Montserrat
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
    modifier: Modifier = Modifier,
    label: String = "Password",
    iserror: Boolean = false,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    // Color definitions
    val primaryBlue = Color(0xFF2563EB)
    val neutralBorder = Color(0xFFE2E8F0)
    val errorColor = Color(0xFFDC2626)
    val textPrimary = Color(0xFF0F172A)
    val placeholderColor = Color(0xFF94A3B8)
    val backgroundContainer = Color(0xFFF8FAFC)

    val animatedIconColor by animateColorAsState(
        targetValue = when {
            iserror -> errorColor
            isFocused -> primaryBlue
            else -> Color(0xFF64748B)
        },
        animationSpec = tween(durationMillis = 200),
        label = "passwordIconColor"
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                fontFamily = Montserrat,
                fontSize = 13.sp,
                fontWeight = if (isFocused) FontWeight.SemiBold else FontWeight.Medium
            )
        },
        textStyle = TextStyle(
            fontFamily = Montserrat,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = textPrimary
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Password Icon",
                tint = animatedIconColor,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            IconButton(
                onClick = { passwordVisible = !passwordVisible },
                modifier = Modifier.size(36.dp)
            ) {
                Crossfade(
                    targetState = passwordVisible,
                    animationSpec = tween(180),
                    label = "visibilityToggle"
                ) { isVisible ->
                    Icon(
                        imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isVisible) "Hide password" else "Show password",
                        tint = if (isVisible) primaryBlue else Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        singleLine = true,
        isError = iserror,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = backgroundContainer,
            errorContainerColor = Color(0xFFFEF2F2),
            focusedBorderColor = primaryBlue,
            unfocusedBorderColor = neutralBorder,
            errorBorderColor = errorColor,
            focusedLabelColor = primaryBlue,
            unfocusedLabelColor = placeholderColor,
            errorLabelColor = errorColor,
            cursorColor = primaryBlue,
            errorCursorColor = errorColor
        ),
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused }
    )
}
