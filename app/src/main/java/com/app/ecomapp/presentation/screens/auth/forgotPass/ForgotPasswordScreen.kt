package com.app.ecomapp.presentation.screens.auth.forgotPass

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.ecomapp.presentation.navigation.Screen


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.compose.jetshop.R
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.auth.RegisterResponse
import com.app.ecomapp.presentation.components.LoadingButton
import com.app.ecomapp.presentation.screens.auth.AuthViewModel
import com.app.ecomapp.ui.theme.AppTypography
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.HandleApiState

@Composable
fun ForgotPasswordScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var apiData by remember { mutableStateOf<RegisterResponse?>(null) } // ✅ Store success data

    HandleApiState(
        apiState = viewModel.forgotPasswordResponse, // ✅ Pass the API state
        showLoader = false, // ✅ Enable/disable loader
        navController = navController,
        onSuccess = { data ->
            apiData = data // ✅ Store success data
            Toast.makeText(context, apiData!!.message, Toast.LENGTH_SHORT).show()

            // ✅ Navigate only if needed (user can decide)
            navController.navigate(Screen.PasswordReset.route+"/${email}/${"empty"}") {
                popUpTo(Screen.ForgotPassword.route) { inclusive = true } // ✅ This removes Register Page from BackStack
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.forgot_bg), // Replace with your image resource
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 1.5f)),
                            startY = 300f
                        )
                    )
            )

            // Content at Bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(20.dp)
            ) {
                Text(
                    text = "Forgot Password",
                    fontSize = 26.sp,
                    style = AppTypography.displayLarge,
                    color = colorResource(id = R.color.white),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Enter your registered email address to reset your password.",
                    style = AppTypography.titleLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = it.isEmpty()
                    },
                    label = {
                        Text(
                            "Email Address",
                            color = Color.White,
                            fontFamily = Montserrat,  // Apply Montserrat font
                            fontWeight = FontWeight.Medium,
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = emailError,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.3f),
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.7f),
                        cursorColor = Color.White
                    ),
                    textStyle = TextStyle(
                        fontFamily = Montserrat,  // Apply Montserrat font
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    ),
                    visualTransformation = VisualTransformation.None
                )

                if (emailError) {
                    Text(text = "Email can't be empty!", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                LoadingButton(
                    text = "Submit",
                    isLoading = viewModel.forgotPasswordResponse.collectAsState().value is Resource.Loading,
                    onClick = {
                        if (email.isNotEmpty()) {
                            viewModel.forgotPassword(email) // ✅ Call API
                        } else {
                            emailError = true
                        }

                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

