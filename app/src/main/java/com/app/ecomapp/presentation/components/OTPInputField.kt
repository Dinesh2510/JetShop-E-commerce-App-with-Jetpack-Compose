package com.app.ecomapp.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OTPInputField(
    value: String,
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit
) { OutlinedTextField(
    value = value,
    onValueChange = { text ->
        if (text.length <= 1) { // Only allow 1 character
            onValueChange(text)
        }
    },
    modifier = Modifier
        .size(50.dp)
        .focusRequester(focusRequester)
        .clip(RoundedCornerShape(8.dp)),
    textStyle = TextStyle(
        fontSize = 18.sp,
        textAlign = TextAlign.Center // Center the text inside the field
    ),
    keyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.NumberPassword
    ),
    singleLine = true,
    maxLines = 1,
    visualTransformation = VisualTransformation.None,
    shape = RoundedCornerShape(10.dp),
    colors = OutlinedTextFieldDefaults.colors( // Use this for Material 3
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        cursorColor = MaterialTheme.colorScheme.primary
    )
)}
@Preview(showBackground = true)
@Composable
fun PreviewOTPTexField() {
    val focusRequester = remember { FocusRequester() }
    var otp by remember { mutableStateOf("") }
   Row   {

       OTPInputField(
           value = otp,
           focusRequester = focusRequester,
           onValueChange = { otp = it }
       )
       OTPInputField(
           value = otp,
           focusRequester = focusRequester,
           onValueChange = { otp = it }
       )
   }
}
