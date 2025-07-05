package com.app.ecomapp.presentation.screens.cart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.ecomapp.presentation.components.SuccessScreen
import com.app.ecomapp.presentation.screens.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HandleActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            val startDestination = if (intent.getStringExtra("navigateTo") == "SuccessScreen") {
                "success"
            } else {
                "home"
            }

            NavHost(navController, startDestination = startDestination) {
                composable("home") { MainScreen(navController) }
                composable("success") { SuccessScreen(
                    navController,
                    paymentId = ""
                ) }
            }
        }
    }
}