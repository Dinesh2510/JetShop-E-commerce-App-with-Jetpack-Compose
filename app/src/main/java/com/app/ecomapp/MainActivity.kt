package com.app.ecomapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.*

import androidx.compose.ui.Modifier

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.app.ecomapp.presentation.components.ConnectivityBanner
import com.app.ecomapp.presentation.navigation.AppNavigation
import com.app.ecomapp.presentation.screens.profile.AppThemeOption
import com.app.ecomapp.presentation.screens.profile.SettingsViewModel
import com.app.ecomapp.ui.theme.MyAppTheme
import com.app.ecomapp.utils.ConnectivityViewModel

import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()
    private val connectivityViewModel: ConnectivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeState by settingsViewModel.themeState.collectAsState()
            val useDarkTheme = themeState.appTheme == AppThemeOption.DARK

            MyAppTheme(themeState = themeState) {
                // Change system bar theme when the theme changes
                ChangeSystemBarsTheme(useDarkTheme = useDarkTheme)
                val isOnline by connectivityViewModel.isOnline.collectAsState()

                MyAppTheme(themeState = themeState) {
                    ChangeSystemBarsTheme(useDarkTheme = useDarkTheme)
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AppNavigation()

                            ConnectivityBanner(
                                isOnline = isOnline,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .navigationBarsPadding()
                                    .padding(bottom = 80.dp)   // sits above your bottom navigation bar
                            )
                        }
                    }
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