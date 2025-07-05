package com.app.ecomapp.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
    val lastClickedTab = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoggedIn.value = dataStoreHelper.isUserLoggedIn(context)
    }

    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = bottomNavController, currentDestination)
        }
    ) { paddingValues ->
        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = bottomNavController,
            startDestination = BottomNavScreens.Home.route
        ) {
            composable(BottomNavScreens.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(BottomNavScreens.Cart.route) {
                CartScreen(navController = navController)
            }
          /*  composable(BottomNavScreens.Cart.route) {
                if (isLoggedIn.value) {
                    CartScreen(navController = navController)
                } else {
                    if (lastClickedTab.value != BottomNavScreens.Cart.route) {
                        // ✅ Show dialog only on first click
                        showLoginDialog.value = true
                        lastClickedTab.value = BottomNavScreens.Cart.route
                    }

                    // ✅ Stay on Home screen instead of navigating to Cart
                    bottomNavController.navigate(BottomNavScreens.Home.route) {
                        popUpTo(BottomNavScreens.Home.route) { inclusive = true }
                    }
                }
            }*/

            composable(BottomNavScreens.Wishlist.route) {
              //  lastClickedTab.value = BottomNavScreens.Wishlist.route
                WishlistScreen(navController)
            }

            composable(BottomNavScreens.Profile.route) {
              //  lastClickedTab.value = BottomNavScreens.Profile.route
                ProfileScreen(navController = navController)
            }
        }
    }

    LoginPromptDialog(
        showDialog = showLoginDialog.value,
        onDismiss = {
            showLoginDialog.value = false
            lastClickedTab.value = null // Reset so next Cart click shows alert
        },
        onLoginClick = {
            showLoginDialog.value = false
            lastClickedTab.value = null // Reset to allow showing on next attempt
            navController.navigate(Screen.Login.route)
        }
    )
}
