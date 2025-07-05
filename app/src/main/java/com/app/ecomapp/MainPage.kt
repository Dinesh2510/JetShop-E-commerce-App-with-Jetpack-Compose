package com.app.ecomapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.*

import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.compose.rememberNavController
import com.app.ecomapp.presentation.screens.auth.AuthViewModel
import com.app.ecomapp.presentation.navigation.AppNavigation
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.profile.AppThemeOption
import com.app.ecomapp.presentation.screens.profile.SettingsViewModel
import com.app.ecomapp.ui.theme.MyAppTheme

import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainPage : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeState by settingsViewModel.themeState.collectAsState()
            val useDarkTheme = themeState.appTheme == AppThemeOption.DARK

            MyAppTheme(themeState = themeState) {
                // Change system bar theme when the theme changes
                ChangeSystemBarsTheme(useDarkTheme = useDarkTheme)
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                   /* val uri: Uri? = intent.data
                    Log.d("TAG_MainPage", "Received URI: $uri")
                    Log.d("TAG_MainPage", "Intent URI: $uri") // Log the URI
                    Log.d("TAG_MainPage", "Intent Action: ${intent.action}") // Log the action type
                    Log.d("TAG_MainPage", "Intent Data String: ${intent.dataString}") // Log the data string

                    uri?.let {
                        val route = it.lastPathSegment
                        Log.d("TAG_MainPage", "Extracted route: $route")

                        route?.let { path ->
                            when (path) {
                                "promotion_screen" -> {
                                    Log.d("TAG_MainPage", "Navigating to promotion_screen")
                                    // Navigate to promotion_screen
                                }
                                "order_details" -> {
                                    val orderId = it.getQueryParameter("orderId") ?: ""
                                    Log.d("TAG_MainPage", "Navigating to order_details with orderId: $orderId")
                                    // Navigate to order_details
                                }
                                else -> {
                                    Log.d("TAG_MainPage", "Unknown deep link: $path")
                                }
                            }
                        }
                    }*/
                    AppNavigation()
                }
            }
        }
    }
    @Composable
    fun ComponentActivity.ChangeSystemBarsTheme(useDarkTheme: Boolean) {
        val barColor = MaterialTheme.colorScheme.surface.toArgb() // or primary, background, etc.

        LaunchedEffect(useDarkTheme) {
            if (useDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.dark(barColor),
                    navigationBarStyle = SystemBarStyle.dark(barColor)
                )
            } else {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.light(barColor, barColor),
                    navigationBarStyle = SystemBarStyle.light(barColor, barColor)
                )
            }
        }
    }

}