package com.app.ecomapp.presentation.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.app.ecomapp.ui.theme.Montserrat
import com.compose.jetshop.R

sealed class BottomNavScreens(val route: String) {
    object Home : BottomNavScreens("home")
    object Cart : BottomNavScreens("cart")
    object Wishlist : BottomNavScreens("wishlist")
    object Profile : BottomNavScreens("profile")

}

@Composable
fun BottomNavigationBar(navController: NavHostController, currentDestination: NavDestination?) {
    val items = listOf(
        BottomNavScreens.Home,
        BottomNavScreens.Cart,
        BottomNavScreens.Wishlist,
        BottomNavScreens.Profile
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) { // ✅ Set background to White
        items.forEach { screen ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

            NavigationBarItem(
                icon = {
                    Image(
                        painter = painterResource(
                            id = when (screen) {
                                BottomNavScreens.Home -> R.drawable.home
                                BottomNavScreens.Cart -> R.drawable.cart
                                BottomNavScreens.Wishlist -> R.drawable.heart
                                BottomNavScreens.Profile -> R.drawable.avatar
                            }
                        ),
                        contentDescription = screen.route,
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(
                            if (isSelected) Color(0xFF1E88E5) else Color.Gray
                        )
                    )
                },
            label = {
                    Text(
                        text = screen.route.replaceFirstChar { it.uppercase() },
                        fontFamily = Montserrat,  // Apply Montserrat font
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFF1E88E5) else Color.Gray // ✅ Selected → Primary Color, Unselected → Gray
                    )
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF1E88E5), // ✅ Selected Icon → Primary Color
                    unselectedIconColor = Color.Gray, // ✅ Unselected Icon → Gray
                    selectedTextColor = Color(0xFF1E88E5), // ✅ Selected Text → Primary Color
                    unselectedTextColor = Color.Gray, // ✅ Unselected Text → Gray
                    indicatorColor = Color.White // ✅ Highlight color for selected item circle color

                )
            )
        }
    }


}


/*package com.app.ecomapp.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.app.ecomapp.ui.theme.Montserrat

sealed class BottomNavScreens(val route: String) {
    object Home : BottomNavScreens("home")
    object Cart : BottomNavScreens("cart")
    object Wishlist : BottomNavScreens("wishlist")
    object Profile : BottomNavScreens("profile")

}

@Composable
fun BottomNavigationBar(navController: NavHostController, currentDestination: NavDestination?) {
    val items = listOf(
        BottomNavScreens.Home,
        BottomNavScreens.Cart,
        BottomNavScreens.Wishlist,
        BottomNavScreens.Profile
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) { // ✅ Set background to White
        items.forEach { screen ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            NavigationBarItem(
                icon = {
                    when (screen) {
                        BottomNavScreens.Home -> {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = screen.route,
                                tint = if (isSelected) Color(0xFF1E88E5) else Color.Gray
                            )
                        }

                        BottomNavScreens.Cart -> {
                            // Wrapping the Cart Icon in a BadgedBox
                            BadgedBox(
                                badge = {
                                    Badge(
                                        content = {
                                            Text(
                                                text = "3", // Replace with dynamic cart count here
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        },
                                        containerColor = Color.Red, // Badge background color
                                        contentColor = Color.White // Badge text color
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = screen.route,
                                    tint = if (isSelected) Color(0xFF1E88E5) else Color.Gray
                                )
                            }
                        }

                        BottomNavScreens.Wishlist -> {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = screen.route,
                                tint = if (isSelected) Color(0xFF1E88E5) else Color.Gray
                            )
                        }

                        BottomNavScreens.Profile -> {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = screen.route,
                                tint = if (isSelected) Color(0xFF1E88E5) else Color.Gray
                            )
                        }
                    }
                },
                label = {
                    Text(
                        text = screen.route.replaceFirstChar { it.uppercase() },
                        fontFamily = Montserrat,  // Apply Montserrat font
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFF1E88E5) else Color.Gray // ✅ Selected → Primary Color, Unselected → Gray
                    )
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF1E88E5), // ✅ Selected Icon → Primary Color
                    unselectedIconColor = Color.Gray, // ✅ Unselected Icon → Gray
                    selectedTextColor = Color(0xFF1E88E5), // ✅ Selected Text → Primary Color
                    unselectedTextColor = Color.Gray, // ✅ Unselected Text → Gray
                    indicatorColor = Color.White // ✅ Highlight color for selected item circle color
                )
            )


        }
    }
}


*/