package com.app.ecomapp.presentation.screens.auth.onBoard


import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.compose.jetshop.R
import com.app.ecomapp.presentation.components.AppButton
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.ui.theme.AppTypography
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.app.ecomapp.presentation.components.Spacer_4dp
import com.app.ecomapp.ui.theme.AppMainTypography
import com.app.ecomapp.ui.theme.BlueDark
import com.app.ecomapp.ui.theme.DarkBlue
import com.app.ecomapp.ui.theme.GreenDark
import com.app.ecomapp.ui.theme.PinkDark
import com.app.ecomapp.utils.CommonFunction.LottieAnimationView
import com.app.ecomapp.utils.UserDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun OnBoardingScreen(navController: NavHostController) {
    var backPressedOnce by remember { mutableStateOf(false) }
    var lastBackPressTime by remember { mutableStateOf(0L) }
    val context = LocalContext.current
    val dataStoreHelper = remember { UserDataStore(context) }

    // Handle back press to detect double press within 2 seconds
    BackHandler {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastBackPressTime < 2000) {
            android.os.Process.killProcess(android.os.Process.myPid())
        } else {
            backPressedOnce = true
            lastBackPressTime = currentTime
            Toast.makeText(navController.context, "Press back again to exit", Toast.LENGTH_SHORT)
                .show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(30.dp)
    ) {
        // Skip button at the top right
        TextButton(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    dataStoreHelper.saveSkipState(context)
                }
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.OnBoard.route) {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier.align(Alignment.TopEnd) // Positioned at top right
        ) {
            Text("Skip", color = BlueDark, style = AppMainTypography.labelLarge)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 50.sp,
                    color = MaterialTheme.colorScheme.primary,
                    style = AppMainTypography.appTitle
                )
                Spacer_4dp()
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color =Color(0xFFFF6E7F),
                            )
                        ) {
                            append("\nShop Smart, Live Better! \uD83D\uDE80\uD83D\uDECD\uFE0F\n\n")
                        }
                        append("Let's Shop!")
                    },
                    color =  Color(0xFF00C9A7),
                    style = AppMainTypography.subHeader,
                    textAlign = TextAlign.Center,
                )

                LottieAnimationView(animationResId = R.raw.oboard,)

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppButton(
                    text = "Sign In",
                    onClick = { navController.navigate(Screen.Login.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                )

                OutlinedButton(
                    onClick = { navController.navigate(Screen.Register.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        containerColor = Color.Transparent
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8)
                ) {
                    Text(
                        text = "Sign Up",
                        style = AppTypography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

