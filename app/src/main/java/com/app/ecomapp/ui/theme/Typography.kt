package com.app.ecomapp.ui.theme

import com.compose.jetshop.R


import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp



// Define Montserrat Font Family
val Montserrat = FontFamily(
    Font(R.font.montserrat_light, FontWeight.Light),
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold),
    Font(R.font.montserrat_bold, FontWeight.Bold)
)

val AppTypography = Typography(
    // ✅ App Title (Toolbar or Screen Title)
    titleLarge = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = Montserrat,
        lineHeight = 32.sp
    ),

    headlineSmall = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = Montserrat,
        lineHeight = 28.sp
    ),

    // ✅ Section Header
    titleMedium = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = Montserrat,
        lineHeight = 26.sp
    ),

    titleSmall = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = Montserrat,
        lineHeight = 24.sp
    ),

    // ✅ Body Text
    bodyMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = Montserrat,
        lineHeight = 24.sp
    ),

    bodySmall = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = Montserrat,
        lineHeight = 20.sp
    ),

    // ✅ Caption / Label
    labelSmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = Montserrat,
        lineHeight = 16.sp
    )
)

