package com.app.ecomapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.jetshop.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarWithBackButtonAndTitle(
    title: String,
    onBackClick: () -> Unit = {}, // Default empty lambda
    showBackButton: Boolean = true // Control visibility
) {
    CenterAlignedTopAppBar(
        title = {
            TitleSmall(title)
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = White
        )
    )
}

@Composable
fun BackArrow(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = "Arrow Back",
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ToolbarPreview() {
    ToolbarWithBackButtonAndTitle(title = "OTP Verification",
        onBackClick = { /* Handle back click here */ })
}
