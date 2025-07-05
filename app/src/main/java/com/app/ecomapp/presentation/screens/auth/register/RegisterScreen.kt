package com.app.ecomapp.presentation.screens.auth.register

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.compose.jetshop.R
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.auth.RegisterResponse
import com.app.ecomapp.presentation.components.CircularImage
import com.app.ecomapp.presentation.components.CustomOutlinedTextField
import com.app.ecomapp.presentation.components.CustomPasswordField
import com.app.ecomapp.presentation.components.LoadingButton
import com.app.ecomapp.presentation.components.Spacer_10dp
import com.app.ecomapp.presentation.components.Spacer_16dp
import com.app.ecomapp.presentation.components.Spacer_20dp
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.components.SubtitleLarge
import com.app.ecomapp.presentation.components.TitleLarge
import com.app.ecomapp.presentation.components.TitleMedium
import com.app.ecomapp.presentation.components.TitleSmall
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.auth.AuthViewModel
import com.app.ecomapp.utils.CommonFunction.RegisterCheckbox
import com.app.ecomapp.utils.UserDataStore
import java.net.URLEncoder

@Composable
fun RegisterScreen(
    navController: NavHostController,
    referCode:String,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val registerState by authViewModel.registerResponse.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    val cleanReferCode = if (referCode == "{code}") "" else referCode
    var refer_Code by remember { mutableStateOf(cleanReferCode) }

    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(false) }
    val dataStoreHelper = remember { UserDataStore(context) }
    val privacyUrl = remember { mutableStateOf("") }
    val termsUrl = remember { mutableStateOf("") }
    var isChecked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val appInfo = dataStoreHelper.getAppInfoObject()
        appInfo?.let {
            privacyUrl.value = it.privacyUrl
            termsUrl.value = it.termsUrl
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF7F7F7)), // Light Background
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularImage(
            imageRes = R.drawable.logo
        )
        Spacer_20dp()

        TitleMedium(text = "Register")
        Spacer_8dp()

        SubtitleLarge("Create a new account to get started")

        Spacer_20dp()

        // Row for First Name and Last Name Fields
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomOutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = "First Name",
                leadingIcon = Icons.Default.Person,
                modifier = Modifier.weight(1f), // Take up equal space
            )

            CustomOutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = "Last Name",
                leadingIcon = Icons.Default.Person,
                modifier = Modifier.weight(1f), // Take up equal space
            )
        }

        Spacer_10dp()

        // Email Field
        CustomOutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            leadingIcon = Icons.Default.Email,
            keyboardType = KeyboardType.Email,
        )

        Spacer_10dp()

        CustomOutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = "Phone",
            leadingIcon = Icons.Default.Phone,
            keyboardType = KeyboardType.Phone
        )

        Spacer_10dp()

        CustomOutlinedTextField(
            value = refer_Code,
            onValueChange = { refer_Code = it },
            label = "Refer Code",
            leadingIcon = Icons.Default.IosShare,
            keyboardType = KeyboardType.Text
        )

        Spacer_10dp()

        // Password Field with Eye Icon
        CustomPasswordField(
            value = password, onValueChange = { password = it }, iserror = false
        )
        Spacer_10dp()
        RegisterCheckbox(
            checked = isChecked,
            onCheckedChange = { isChecked = it },
            onTermsClick = {
                // Open Terms URL
                val url = URLEncoder.encode(termsUrl.value, "UTF-8")
                navController.navigate(Screen.WebViewScreen.route+"/Terms & Conditions/$url")
            },
            onPrivacyClick = {
                // Open Privacy URL
                val url = URLEncoder.encode(privacyUrl.value, "UTF-8")
                navController.navigate(Screen.WebViewScreen.route+"/Privacy Policy/$url")
            }
        )

        Spacer_20dp()

        LoadingButton(
            text = "Register",
            isLoading = isLoading.value,
            onClick = {
                keyboardController?.hide() // Hide the keyboard
                if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "All fields are required!", Toast.LENGTH_SHORT).show()
                } else if (!isChecked) {
                    Toast.makeText(context, "You must agree to the Terms and Privacy Policy.", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("registerUser_", "MainScreemn: "+refer_Code)
                    authViewModel.registerUser(firstName, lastName, email, phone, password,refer_Code)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (registerState is Resource.Loading) {
            isLoading.value = true
        }
        LaunchedEffect(registerState) {
            when (registerState) {
                is Resource.Success -> {
                    isLoading.value = false
                    val registerStateData =
                        (registerState as Resource.Success<RegisterResponse?>).data
                    registerStateData?.let {
                        Toast.makeText(
                            context, "${it.message}", Toast.LENGTH_SHORT
                        ).show()
                        // ✅ Navigate to OTP Screen and remove Register Screen from Back Stack
                        navController.navigate("otp_screen/${email}/${firstName} ${lastName}") {
                            popUpTo(Screen.Register.route) { inclusive = true } // ✅ This removes Register Page from BackStack
                        }
                    }
                }

                is Resource.Error -> {
                    isLoading.value = false
                    val errorMessage = (registerState as Resource.Error).message
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
        }
    }
}
