package com.app.ecomapp.presentation.screens.auth.login


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.compose.jetshop.R
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.auth.RegisterResponse
import com.app.ecomapp.presentation.components.BlurView
import com.app.ecomapp.presentation.components.CircularImage
import com.app.ecomapp.presentation.components.CustomOutlinedTextField
import com.app.ecomapp.presentation.components.CustomPasswordField
import com.app.ecomapp.presentation.components.LoadingButton
import com.app.ecomapp.presentation.components.Spacer_12dp
import com.app.ecomapp.presentation.components.Spacer_16dp
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.components.TitleMedium
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.auth.AuthViewModel
import com.app.ecomapp.ui.theme.AppMainTypography
import com.app.ecomapp.ui.theme.AppTypography
import com.app.ecomapp.ui.theme.BluePrimary
import com.app.ecomapp.utils.AppLogger
import com.app.ecomapp.utils.HandleApiState
import com.app.ecomapp.utils.UserDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavHostController, viewModel: AuthViewModel = hiltViewModel()) {

    BlurView()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginData by remember { mutableStateOf<RegisterResponse?>(null) } // ✅ Store success data
    val userDataStore = UserDataStore(context)


    // ✅ Login API handle
    HandleApiState(
        apiState = viewModel.loginResponse, // ✅ Pass the API state
        showLoader = false, // ✅ Enable/disable loader
        navController = navController,
        onSuccess = { data ->
            loginData = data // ✅ Store success data
            viewModel.getUserData(loginData!!.user_id)
            // ✅ Navigate only if needed (user can decide)
            // navController.navigate(Screen.Home.route)
            /*Toast.makeText(context, loginData!!.message, Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.IO).launch {
                userDataStore.saveValue(
                    UserDataStore.USER_ID,
                    loginData!!.user_id
                ) // ✅ Save user_id
            }*/

        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularImage(
                    imageRes = R.drawable.logo
                )
                Spacer_16dp()

                TitleMedium(text = "Login")
                Spacer_16dp()

                CustomOutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    leadingIcon = Icons.Default.Email,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer_12dp()

                CustomPasswordField(
                    value = password,
                    onValueChange = { password = it },
                    iserror = false
                )

                Spacer_16dp()

                LoadingButton(
                    text = "Login",
                    isLoading = viewModel.loginResponse.collectAsState().value is Resource.Loading,
                    onClick = {
                        keyboardController?.hide() // ✅ Hide keyboard
                        if (email.isEmpty() || password.isEmpty()) {
                            Toast.makeText(context, "Please enter both fields", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            viewModel.loginUser(email, password) // ✅ Call API
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer_12dp()

                TextButton(onClick = {
                    navController.navigate(Screen.ForgotPassword.route)
                }) {
                    Text("Forgot Password?", color = BluePrimary, style = AppMainTypography.subHeader)
                }
                Spacer_8dp()

                TextButton(onClick = {
                    navController.navigate(Screen.Register.route)
                }) {
                    Text("New user? Register Now", color = BluePrimary, style = AppMainTypography.subHeader)
                }

                /*// ✅ Show success data (example)
                loginData?.let {
                    Text("Welcome, ${it.message}", style = AppTypography.bodyLarge)
                }*/
            }
        }
    }
    HandleApiState(
        apiState = viewModel.userDataResponse, // ✅ Pass the API state
        showLoader = false, // ✅ Enable/disable loader
        navController = navController,
        onSuccess = { data ->
            // ✅ Navigate only if needed (user can decide)
            // navController.navigate(Screen.Home.route)
            data?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    userDataStore.saveUser(
                        userId = it.userData.userId.toString(),
                        name = it.userData.name,
                        first_name = it.userData.firstName,
                        last_name = it.userData.lastName,
                        email = it.userData.email,
                        phone = it.userData.phone,
                        user_profile = it.userData.profileImage,
                        isPrimeActive = it.userData.isPrimeActive.toString(),
                        referralCode = it.userData.referralCode ?: "",  // Add referral_code
                        referredBy = it.userData.referredBy ?: "",      // Add referred_by
                        walletBalance = it.userData.walletBalance       // Add wallet_balance
                    )
                    AppLogger.d("LoginScreen",""+it.userData.name)

                }
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    userDataStore.saveLoginState(context, true) // Mark user as logged in
                }
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Login.route) {
                        inclusive = true
                    } // ✅ Removes Login Screen from backstack
                }

            }

        }
    ) {}

}
