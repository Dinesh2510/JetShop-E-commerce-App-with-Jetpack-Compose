package com.app.ecomapp.presentation.screens.auth.register

import android.util.Patterns
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.auth.RegisterResponse
import com.app.ecomapp.presentation.components.AppPrimaryButton
import com.app.ecomapp.presentation.components.AppTextField
import com.app.ecomapp.presentation.components.CustomPasswordField
import com.app.ecomapp.presentation.components.friendlyErrorMessage
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.auth.AuthViewModel
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.CommonFunction.RegisterCheckbox
import com.app.ecomapp.utils.UserDataStore
import com.compose.jetshop.R
import java.net.URLEncoder

@Composable
fun RegisterScreen(
    navController: NavHostController,
    referCode: String,
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

    // Error & validation states
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isPasswordError by remember { mutableStateOf(false) }

    val context = LocalContext.current
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

    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is Resource.Success -> {
                errorMessage = null
                isPasswordError = false
                val registerStateData = (state as Resource.Success<RegisterResponse?>).data
                registerStateData?.let {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    navController.navigate("otp_screen/${email.trim()}/${firstName.trim()} ${lastName.trim()}") {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            }

            is Resource.Error -> {
                val rawError = state.message
                errorMessage = friendlyErrorMessage(rawError, "Registration failed. Please try again.")
                isPasswordError = rawError?.contains("password", ignoreCase = true) == true
            }

            else -> {}
        }
    }

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
            // Top Navigation Back Button
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

            Spacer(modifier = Modifier.height(6.dp))

            // Branding & Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shadowElevation = 2.dp,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "JetShop Logo",
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Create an Account",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Montserrat,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Join JetShop to unlock member discounts, track shipments & enjoy fast checkout.",
                    fontSize = 13.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Registration Form Card
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
                    // Inline Error Banner
                    AnimatedVisibility(
                        visible = !errorMessage.isNullOrBlank(),
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFEF2F2),
                            border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp)
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

                    // First Name & Last Name (Side-by-side)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppTextField(
                            value = firstName,
                            onValueChange = {
                                firstName = it
                                errorMessage = null
                            },
                            label = "First Name",
                            leadingIcon = Icons.Default.Person,
                            modifier = Modifier.weight(1f)
                        )

                        AppTextField(
                            value = lastName,
                            onValueChange = {
                                lastName = it
                                errorMessage = null
                            },
                            label = "Last Name",
                            leadingIcon = Icons.Default.Person,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AppTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        label = "Email Address",
                        leadingIcon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AppTextField(
                        value = phone,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() } && input.length <= 10) {
                                phone = input
                                errorMessage = null
                            }
                        },
                        label = "Phone Number",
                        leadingIcon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AppTextField(
                        value = refer_Code,
                        onValueChange = {
                            refer_Code = it
                            errorMessage = null
                        },
                        label = "Referral Code (Optional)",
                        leadingIcon = Icons.Default.IosShare,
                        keyboardType = KeyboardType.Text
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CustomPasswordField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                            isPasswordError = false
                        },
                        iserror = isPasswordError
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    RegisterCheckbox(
                        checked = isChecked,
                        onCheckedChange = {
                            isChecked = it
                            if (it) errorMessage = null
                        },
                        onTermsClick = {
                            val url = URLEncoder.encode(termsUrl.value, "UTF-8")
                            navController.navigate("${Screen.WebViewScreen.route}/Terms & Conditions/$url")
                        },
                        onPrivacyClick = {
                            val url = URLEncoder.encode(privacyUrl.value, "UTF-8")
                            navController.navigate("${Screen.WebViewScreen.route}/Privacy Policy/$url")
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    AppPrimaryButton(
                        text = "Create Account",
                        isLoading = registerState is Resource.Loading,
                        onClick = {
                            keyboardController?.hide()
                            val trimmedFirst = firstName.trim()
                            val trimmedLast = lastName.trim()
                            val trimmedEmail = email.trim()
                            val trimmedPhone = phone.trim()
                            val trimmedPassword = password.trim()

                            when {
                                trimmedFirst.isEmpty() -> {
                                    errorMessage = "Please enter your first name"
                                }
                                trimmedLast.isEmpty() -> {
                                    errorMessage = "Please enter your last name"
                                }
                                trimmedEmail.isEmpty() -> {
                                    errorMessage = "Please enter your email address"
                                }
                                !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() -> {
                                    errorMessage = "Please enter a valid email address"
                                }
                                trimmedPhone.isEmpty() -> {
                                    errorMessage = "Please enter your phone number"
                                }
                                trimmedPhone.length < 10 -> {
                                    errorMessage = "Please enter a valid 10-digit mobile number"
                                }
                                trimmedPassword.isEmpty() -> {
                                    errorMessage = "Please create a password"
                                    isPasswordError = true
                                }
                                trimmedPassword.length < 6 -> {
                                    errorMessage = "Password must be at least 6 characters"
                                    isPasswordError = true
                                }
                                !isChecked -> {
                                    errorMessage = "Please agree to the Terms and Privacy Policy"
                                }
                                else -> {
                                    errorMessage = null
                                    isPasswordError = false
                                    authViewModel.registerUser(
                                        trimmedFirst,
                                        trimmedLast,
                                        trimmedEmail,
                                        trimmedPhone,
                                        trimmedPassword,
                                        refer_Code.trim()
                                    )
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalDivider(
                    thickness = 0.8.dp,
                    color = Color(0xFFE2E8F0),
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Already have an account? ",
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
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}