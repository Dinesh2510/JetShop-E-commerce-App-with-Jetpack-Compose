package com.app.ecomapp.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.compose.jetshop.R
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.cart.CartResponse
import com.app.ecomapp.data.models.home.ProductData

import com.app.ecomapp.presentation.components.LoginPromptDialog
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.home.HomeViewModel
import com.app.ecomapp.ui.theme.BlueDark
import com.app.ecomapp.ui.theme.DarkBlue
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.ui.theme.black
import com.app.ecomapp.utils.Constants.Companion.Ruppes
import com.app.ecomapp.utils.IncludeApp
import com.app.ecomapp.utils.UserDataStore

@Composable
fun LoginRequiredScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        contentAlignment = Alignment.Center // Centers the content in the screen
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp) // Increased height for Chrome padding
                .padding(horizontal = 16.dp),
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.Gray),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(Color.White),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "",
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )
                    Spacer_8dp()
                    Text(
                        text = "Sign in to explore and enjoy all the features of our app.",
                        fontWeight = FontWeight.Medium, textAlign = TextAlign.Center,
                        fontFamily = Montserrat,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer_12dp()
                    IncludeApp().CustomDivider()
                    Spacer_8dp()
                    Button(
                        onClick = {
                            navController.navigate(Screen.Login.route)
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Login Now",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Montserrat
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Login Required",
                    color = Color(0xFF0086F9),
                    fontSize = 22.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
