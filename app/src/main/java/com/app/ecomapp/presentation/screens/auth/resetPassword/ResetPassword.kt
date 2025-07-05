package com.app.ecomapp.presentation.screens.auth.resetPassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.compose.jetshop.R
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.auth.RegisterResponse
import com.app.ecomapp.presentation.components.CircularImage
import com.app.ecomapp.presentation.components.CustomOutlinedTextField
import com.app.ecomapp.presentation.components.CustomPasswordField
import com.app.ecomapp.presentation.components.LoadingButton
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.auth.AuthViewModel
import com.app.ecomapp.utils.HandleApiState


@Composable
fun ResetPasswordScreen(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var apiData by remember { mutableStateOf<RegisterResponse?>(null) } // ✅ Store success data

    val otpValues = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = List(6) { FocusRequester() }
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordsMatch by remember { mutableStateOf(true) }
    email = backStackEntry.arguments?.getString("email") ?: ""
    HandleApiState(
        apiState = viewModel.resetPasswordResponse, // ✅ Pass the API state
        showLoader = false, // ✅ Enable/disable loader
        navController = navController,
        onSuccess = { data ->
            apiData = data // ✅ Store success data
            Toast.makeText(context, apiData!!.message, Toast.LENGTH_SHORT).show()

            // ✅ Navigate only if needed (user can decide)
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true } // ✅ Removes all previous screens
            }

        }
    ) {
        Image(
            painter = painterResource(id = R.drawable.forgot_bg), // Replace with your image resource
            contentDescription = "Background Image",
            modifier = Modifier
                .fillMaxSize()
                .blur(12.dp),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.9f),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularImage(
                        imageRes = R.drawable.forgot_bg
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Enter the 6-digit OTP sent on $email",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    // Email Field
                    CustomOutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        leadingIcon = Icons.Default.Email,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardType = KeyboardType.Email, isEnabled = false
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    CustomPasswordField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordsMatch = password == confirmPassword
                        },
                        iserror = false
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CustomPasswordField(
                        value = confirmPassword, onValueChange = {
                            confirmPassword = it
                            passwordsMatch = password == confirmPassword
                        }, label = "Confirm Password", iserror = !passwordsMatch
                    )
                    if (!passwordsMatch) {
                        Text("Passwords do not match!", color = Color.Red, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Enter the OTP")
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp) // ✅ Added spacing between boxes
                    ) {
                        otpValues.forEachIndexed { index, value ->
                            OutlinedTextField(value = value,
                                onValueChange = { newValue ->
                                    if (newValue.length <= 1) {
                                        otpValues[index] = newValue
                                        if (newValue.isNotEmpty() && index < 5) {
                                            focusRequesters[index + 1].requestFocus() // ✅ Move focus forward
                                        }
                                    }
                                },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier
                                    .weight(1f) // ✅ Equal width
                                    .aspectRatio(1f) // ✅ Square shape
                                    .focusRequester(focusRequesters[index])
                                    .onKeyEvent { event ->
                                        if (event.key == Key.Backspace && event.type == KeyEventType.KeyUp) {
                                            if (otpValues[index].isNotEmpty()) {
                                                otpValues[index] = "" // ✅ Clear only if not empty
                                            } else if (index > 0) {
                                                focusRequesters[index - 1].requestFocus() // ✅ Move focus back
                                            }
                                            true
                                        } else {
                                            false
                                        }
                                    })
                        }
                    }
                    // Submit Button
                    Spacer(modifier = Modifier.height(16.dp))
                    LoadingButton(
                        text = "Submit",
                        isLoading = viewModel.resetPasswordResponse.collectAsState().value is Resource.Loading,
                        onClick = {
                            val otpEntered = otpValues.joinToString("")
                            if (email.isBlank() || password.isBlank() || confirmPassword.isBlank() || otpEntered.length < 6) {
                                Toast.makeText(
                                    context,
                                    "All fields are required!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else if (!passwordsMatch) {
                                Toast.makeText(
                                    context,
                                    "Passwords do not match!",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else {
                                viewModel.resetPassword(email, otpEntered, password)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}