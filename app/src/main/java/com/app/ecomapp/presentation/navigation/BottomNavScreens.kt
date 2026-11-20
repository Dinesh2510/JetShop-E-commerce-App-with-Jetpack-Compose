package com.app.ecomapp.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

sealed class BottomNavScreens(
    val route: String,
    val title: String,
    @DrawableRes val iconRes: Int
) {
    object Home : BottomNavScreens("home", "Home", R.drawable.home)
    object Wishlist : BottomNavScreens("wishlist", "Wishlist", R.drawable.heart)
    object Cart : BottomNavScreens("cart", "Cart", R.drawable.cart)
    object Profile : BottomNavScreens("profile", "Profile", R.drawable.avatar)
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentDestination: NavDestination?,
    cartBadgeCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val items = remember {
        listOf(
            BottomNavScreens.Home,
            BottomNavScreens.Wishlist,
            BottomNavScreens.Cart,
            BottomNavScreens.Profile
        )
    }

    val primaryBlue = Color(0xFF1D4ED8)
    val inactiveTint = Color(0xFF94A3B8)

    val activePillBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFEBF3FF),
            Color(0xFFF3F7FF)
        )
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
        color = Color.White,
        shadowElevation = 14.dp,
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFFE2E8F0).copy(alpha = 0.8f), Color.Transparent)
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .height(58.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { screen ->
                val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                // Spring physics for icon bounce
                val iconScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.14f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = 0.62f, // Gentle spring bounce
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "tabBounce"
                )

                // Smooth color fade
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) primaryBlue else inactiveTint,
                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                    label = "colorFade"
                )

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .then(
                            if (isSelected) {
                                Modifier
                                    .background(activePillBrush)
                                    .border(0.8.dp, Color(0xFFDBEAFE), CircleShape)
                            } else {
                                Modifier
                            }
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!isSelected) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        .padding(horizontal = 15.dp, vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        BadgedBox(
                            badge = {
                                if (screen is BottomNavScreens.Cart && cartBadgeCount > 0) {
                                    Badge(
                                        containerColor = Color(0xFFEF4444),
                                        contentColor = Color.White,
                                        modifier = Modifier.offset(x = 4.dp, y = (-2).dp)
                                    ) {
                                        Text(
                                            text = if (cartBadgeCount > 99) "99+" else cartBadgeCount.toString(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = Montserrat
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = screen.iconRes),
                                contentDescription = screen.title,
                                tint = contentColor,
                                modifier = Modifier
                                    .size(21.dp)
                                    .scale(iconScale)
                            )
                        }

                        // Text slides smoothly out beside the icon
                        AnimatedVisibility(
                            visible = isSelected,
                            enter = fadeIn(animationSpec = tween(200, delayMillis = 40)) +
                                    expandHorizontally(
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                            stiffness = Spring.StiffnessMediumLow
                                        ),
                                        expandFrom = Alignment.Start
                                    ),
                            exit = fadeOut(animationSpec = tween(90)) +
                                    shrinkHorizontally(
                                        animationSpec = tween(120),
                                        shrinkTowards = Alignment.Start
                                    )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = screen.title,
                                    fontSize = 12.5.sp,
                                    fontFamily = Montserrat,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryBlue,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}