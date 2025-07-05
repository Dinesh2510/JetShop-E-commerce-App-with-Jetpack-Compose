package com.app.ecomapp.presentation.screens.splash

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.traceEventEnd
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.appInfo.AppInfoResponse
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator
import com.app.ecomapp.presentation.components.Spacer_16dp
import com.app.ecomapp.presentation.components.Spacer_32dp
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.compose.jetshop.R
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.auth.AuthViewModel
import com.app.ecomapp.ui.theme.AppMainTypography
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.UserDataStore
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavHostController, viewModel: AuthViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val userDataStore = remember { UserDataStore(context) }
    val appInfo by viewModel.appInfo.collectAsState()
    val activity = (context as? Activity)

    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }
    var isUpdateDialog by remember { mutableStateOf(false) }

    // ✅ Start API Call
    LaunchedEffect(Unit) {
        viewModel.fetchAppInfo()
    }

    // ✅ React to API Response
    LaunchedEffect(appInfo) {
        when (val state = appInfo) {
            is Resource.Success -> {
                val data = state.data.appInfoData
                val json = Gson().toJson(data)
                userDataStore.saveAppInfoJson(json)

                when {
                    data.isMaintenanceMode -> {
                        dialogTitle = "🛠 Maintenance"
                        dialogMessage = data.maintenanceMessage ?: "We'll be back soon!"
                        showDialog = true
                        isUpdateDialog = false
                    }

                    data.isUpdateRequired -> {
                        dialogTitle = "🚀 App Update"
                        dialogMessage = data.updateMessage ?: "Please update your app"
                        showDialog = true
                        isUpdateDialog = true
                    }

                    else -> {
                        delay(1500)
                        // Launch coroutine for suspend call
                        CoroutineScope(Dispatchers.Main).launch {
                            navigateBasedOnUser(userDataStore, navController, context)
                        }
                    }
                }
            }

            is Resource.Error -> {
                Toast.makeText(context, state.message ?: "Failed to load app info", Toast.LENGTH_SHORT).show()
                // fallback or retry
            }

            else -> Unit
        }
    }

    // ✅ UI + Loader
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedSplashIconWithText()

        if (appInfo is Resource.Loading) {
           // CenteredCircularProgressIndicator()
        }
    }

    // ✅ Dialog with Icon & Dual Button for Update
    if (showDialog) {
        CustomDialogHeaderAction(
            title = dialogTitle,
            message = dialogMessage,
            onFinish = { activity!!.finishAffinity() },
            showUpdateButtons = isUpdateDialog, // ✅ Fix here
            onDismiss = {
                showDialog = false
                CoroutineScope(Dispatchers.Main).launch {
                    navigateBasedOnUser(userDataStore, navController, context)
                }
            },
            onPositiveClick = {
                // ✅ Open Play Store
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
                context.startActivity(intent)
            },
            icon = painterResource(id = R.drawable.maintenance)
        )
    }

}
@Composable
fun CustomDialogHeaderAction(
    title: String,
    message: String,
    showUpdateButtons: Boolean = false,
    onDismiss: () -> Unit,
    onFinish: () -> Unit,
    onPositiveClick: (() -> Unit)? = null,
    positiveButtonText: String = "OK",
    negativeButtonText: String = "Later",
    icon: Painter = painterResource(id = R.drawable.maintenance)
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = icon,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(2 / 1f)
                        .fillMaxWidth()
                )
                Spacer_16dp()
                Text(
                    text = title,
                    modifier = Modifier.padding(horizontal = 20.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer_8dp()
                Text(
                    text = message,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.outline
                    )

                )

                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (showUpdateButtons) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = { onPositiveClick?.invoke() }
                        ) {
                            Text("Update", style = AppMainTypography.subHeader)
                        }
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = onDismiss
                        ) {
                            Text(negativeButtonText,style = AppMainTypography.subHeader)
                        }
                    } else {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onFinish
                        ) {
                            Text(positiveButtonText)
                        }
                    }
                }

                Spacer_16dp()
            }
        }
    }
}

private suspend fun navigateBasedOnUser(
    userDataStore: UserDataStore,
    navController: NavHostController,
    context: Context
) {
    if (userDataStore.isUserLoggedIn(context) || userDataStore.isUserSkipped(context)) {
        navController.navigate(Screen.Main.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    } else {
        navController.navigate(Screen.OnBoard.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }
}
@Composable
fun AnimatedSplashIconWithText() {
    val logoScale = remember { Animatable(0.5f) }
    val textScale = remember { Animatable(0f) }
    var showText by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Logo animation
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )
        delay(300)
        showText = true

        // Text animation
        textScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color(0xFFF8F7F6)
                /*brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF003c8f), // Lighter blue (bottom)
                        Color(0xFF001c55), // Deep blue (top)
                    )
                )*/
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_trans),
                contentDescription = null,
                modifier = Modifier
                    .size(250.dp)
                    .scale(logoScale.value)
                   /* .shadow(4.dp, shape = RoundedCornerShape(4), clip = true)*/
            )

            if (showText) {
               // Spacer_32dp()

               /* Text(
                    text = stringResource(id = R.string.app_name),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontFamily = Montserrat,
                    modifier = Modifier.scale(textScale.value),
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black,
                            blurRadius = 4f
                        )
                    )
                )*/
            }
        }
    }
}
