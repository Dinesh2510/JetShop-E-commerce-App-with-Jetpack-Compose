package com.app.ecomapp.presentation.screens.auth.resetPassword

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.app.ecomapp.data.Resource
import com.app.ecomapp.presentation.components.AppPrimaryButton
import com.app.ecomapp.presentation.components.AppTextField
import com.app.ecomapp.presentation.components.CustomPasswordField
import com.app.ecomapp.presentation.components.OtpInputField
import com.app.ecomapp.presentation.components.friendlyErrorMessage
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.auth.AuthViewModel
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.HandleApiState

@Composable
fun ResetPasswordScreen(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var otpEntered by remember { mutableStateOf("") }
    val email = remember { backStackEntry.arguments?.getString("email").orEmpty() }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordsMatch by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val resetState by viewModel.resetPasswordResponse.collectAsState()

    HandleApiState(
        apiState = viewModel.resetPasswordResponse,
        showLoader = false,
        showErrorScreen = false,        // NEW: this is a form, keep the toast
        navController = navController,
        onSuccess = { data ->
            errorMessage = null
            Toast.makeText(context, data?.message ?: "Password reset successful!", Toast.LENGTH_SHORT).show()
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        },
        onError = { error ->
            errorMessage = friendlyErrorMessage(error, "Failed to reset password. Please try again.")
        }
    ) {
        Scaffold(
            containerColor = Color(0xFFF8FAFC),
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Navigation Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shadowElevation = 1.dp,
                        modifier = Modifier.size(42.dp)
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Back",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Header Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Password,
                            contentDescription = "Reset Password",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Reset Password",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Montserrat,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Enter the 6-digit verification code sent to",
                        fontSize = 13.sp,
                        fontFamily = Montserrat,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = email,
                        fontSize = 13.sp,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2563EB),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Card Container
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Animated Inline Error Banner
                        AnimatedVisibility(
                            visible = !errorMessage.isNullOrBlank(),
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut() + slideOutVertically(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFEF2F2),
                                border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = errorMessage.orEmpty(),
                                        fontSize = 12.sp,
                                        fontFamily = Montserrat,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFB91C1C),
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Verification Code",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Montserrat,
                            color = Color(0xFF1E293B)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Clean, unclipped 6-Digit OTP Field
                        OtpInputField(
                            otpValue = otpEntered,
                            onOtpChange = {
                                otpEntered = it
                                errorMessage = null
                                if (it.length == 6) {
                                    keyboardController?.hide()
                                }
                            },
                            isError = errorMessage != null && otpEntered.length < 6
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Verified Email Field (Disabled & Read-Only)
                        AppTextField(
                            value = email,
                            onValueChange = {},
                            label = "Account Email",
                            leadingIcon = Icons.Default.Email,
                            enabled = false,
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // New Password
                        CustomPasswordField(
                            value = password,
                            onValueChange = {
                                password = it
                                passwordsMatch = password == confirmPassword || confirmPassword.isEmpty()
                                errorMessage = null
                            },
                            label = "New Password",
                            iserror = false
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Confirm Password
                        CustomPasswordField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                passwordsMatch = password == confirmPassword
                                errorMessage = null
                            },
                            label = "Confirm Password",
                            iserror = !passwordsMatch
                        )

                        if (!passwordsMatch) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Passwords do not match",
                                    color = Color(0xFFDC2626),
                                    fontSize = 12.sp,
                                    fontFamily = Montserrat,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        AppPrimaryButton(
                            text = "Reset Password",
                            isLoading = resetState is Resource.Loading,
                            onClick = {
                                keyboardController?.hide()
                                when {
                                    otpEntered.length < 6 -> {
                                        errorMessage = "Please enter the complete 6-digit code"
                                    }
                                    password.isBlank() || confirmPassword.isBlank() -> {
                                        errorMessage = "Please fill in all password fields"
                                    }
                                    password != confirmPassword -> {
                                        passwordsMatch = false
                                        errorMessage = "Passwords do not match"
                                    }
                                    else -> {
                                        errorMessage = null
                                        viewModel.resetPassword(email, otpEntered, password)
                                    }
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Login Option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Remembered your credentials? ",
                        fontSize = 13.sp,
                        fontFamily = Montserrat,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "Sign In",
                        fontSize = 13.sp,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB),
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}