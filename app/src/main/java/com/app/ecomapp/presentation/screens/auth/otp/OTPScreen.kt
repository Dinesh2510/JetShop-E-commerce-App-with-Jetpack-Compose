package com.app.ecomapp.presentation.screens.auth.otp

import android.os.CountDownTimer
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.auth.RegisterResponse
import com.app.ecomapp.presentation.components.LoadingButton
import com.app.ecomapp.presentation.components.OTPInputField
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.auth.AuthViewModel
import com.app.ecomapp.ui.theme.Montserrat


@Composable
fun OTPScreen(
    navController: NavController, backStackEntry: NavBackStackEntry,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val otpVerifyState by authViewModel.verifyOTPResponse.collectAsState()
    val isLoading = remember { mutableStateOf(false) }

    val otpValues = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    var timerText by remember { mutableStateOf("120s") }
    var isResendEnabled by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val email = backStackEntry.arguments?.getString("email") ?: ""
    val name = backStackEntry.arguments?.getString("name") ?: ""
    // Countdown Timer
    LaunchedEffect(Unit) {
        object : CountDownTimer(120000, 1000) { // ✅ 2 minutes countdown
            override fun onTick(millisUntilFinished: Long) {
                timerText = "${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                isResendEnabled = true
                timerText = "Resend"
            }
        }.start()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Row
        ToolbarWithBackButtonAndTitle(
            title = "OTP Verification",
            onBackClick = { navController.popBackStack() }
        )
        Spacer(modifier = Modifier.height(50.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buildAnnotatedString {
                    append("We sent your code to $email\n")
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Medium,
                            fontFamily = Montserrat,
                            color = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        append("This code expires in $timerText")
                    }
                },
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(50.dp))

            // OTP Input Fields
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                otpValues.forEachIndexed { index, value ->
                    OTPInputField(
                        value = value,
                        focusRequester = focusRequesters[index],
                        onValueChange = { newText ->
                            if (newText.length <= 1) {
                                otpValues[index] = newText
                                if (newText.isNotEmpty() && index < 5) {
                                    focusRequesters[index + 1].requestFocus()
                                } else if (newText.isEmpty() && index > 0) {
                                    focusRequesters[index - 1].requestFocus()
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            LoadingButton(
                text = "Verify",
                isLoading = isLoading.value,
                onClick = {
                    val otpCode = otpValues.joinToString("")
                    if (otpCode.length == 6) {
                        authViewModel.verifyOTP(otpCode, email)
                    } else {
                        Toast.makeText(context, "Enter the OTP", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Resend OTP

            // TODO: Resend OTP Code
           /* Text(
                text = "Resend OTP Code",
                style = TextStyle(textDecoration = TextDecoration.Underline),
                color = if (isResendEnabled) MaterialTheme.colorScheme.primary else Color.Gray,
                fontWeight = FontWeight.Medium,
                fontFamily = Montserrat,
                modifier = Modifier.clickable(enabled = isResendEnabled) {
                    // Handle OTP Resend Logic Here
                    Toast.makeText(
                        context, "Under Development...", Toast.LENGTH_SHORT
                    ).show()
                }
            )*/


            if (otpVerifyState is Resource.Loading) {
                isLoading.value = true
            }
            LaunchedEffect(otpVerifyState) {
                when (otpVerifyState) {
                    is Resource.Success -> {
                        isLoading.value =false
                        val registerStateData =
                            (otpVerifyState as Resource.Success<RegisterResponse?>).data
                        registerStateData?.let {
                            Toast.makeText(
                                context, it.message, Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.OtpScreen.route) { inclusive = true } // ✅ This removes Register Page from BackStack
                            }
                        }
                    }

                    is Resource.Error -> {
                        isLoading.value =false
                        val errorMessage = (otpVerifyState as Resource.Error).message
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }
}
