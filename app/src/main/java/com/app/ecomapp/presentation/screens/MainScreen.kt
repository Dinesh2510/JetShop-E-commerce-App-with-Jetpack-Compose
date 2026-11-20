package com.app.ecomapp.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.ecomapp.presentation.components.LoginPromptDialog
import com.app.ecomapp.presentation.navigation.BottomNavScreens
import com.app.ecomapp.presentation.navigation.BottomNavigationBar
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.cart.CartScreen
import com.app.ecomapp.presentation.screens.home.HomeScreen
import com.app.ecomapp.presentation.screens.profile.ProfileScreen
import com.app.ecomapp.presentation.screens.wishlist.WishlistScreen
import com.app.ecomapp.utils.UserDataStore

@Composable
fun MainScreen(navController: NavHostController) {
    val context = LocalContext.current
    val dataStoreHelper = remember { UserDataStore(context) }
    val isLoggedIn = remember { mutableStateOf(false) }
    val showLoginDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoggedIn.value = dataStoreHelper.isUserLoggedIn(context)
    }

    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        bottomBar = {
            BottomNavigationBar(
                navController = bottomNavController,
                currentDestination = currentDestination
            )
        }
    ) { paddingValues ->
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // Automatically prevents overlap on Cart, Home, and Wishlist
            navController = bottomNavController,
            startDestination = BottomNavScreens.Home.route
        ) {
            composable(BottomNavScreens.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(BottomNavScreens.Wishlist.route) {
                WishlistScreen(navHostController = navController)
            }
            composable(BottomNavScreens.Cart.route) {
                CartScreen(navController = navController)
            }
            composable(BottomNavScreens.Profile.route) {
                ProfileScreen(navController = navController)
            }
        }
    }

    LoginPromptDialog(
        showDialog = showLoginDialog.value,
        onDismiss = { showLoginDialog.value = false },
        onLoginClick = {
            showLoginDialog.value = false
            navController.navigate(Screen.Login.route)
        }
    )
}