package com.app.ecomapp.presentation.components


import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.compose.jetshop.R
import com.app.ecomapp.presentation.components.BlurView
import com.app.ecomapp.presentation.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun BlurView() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA)), // Light background
        contentAlignment = Alignment.Center
    ) {
        var sizeImage by remember { mutableStateOf(IntSize.Zero) }

        val gradient = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.Black
            ),
            startY = sizeImage.height.toFloat() / 1.1f,
            endY = sizeImage.height.toFloat()
        )

        Box {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .blur(8.dp)
                    .onGloballyPositioned { sizeImage = it.size },
                painter = painterResource(id = R.drawable.onboard_img),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(gradient)
            )
        }
    }
}

