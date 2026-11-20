package com.app.ecomapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object AppMainTypography {

    // ✅ App Title (20–24sp)
    val appTitle = TextStyle(
        fontSize = 22.sp, // You can adjust to 20 or 24 if needed
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold
    )

    // ✅ Section Header (16–18sp)
    val sectionHeader = TextStyle(
        fontSize = 17.sp,
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold
    )

    // ✅ Section Header (16–18sp)
    val subHeader = TextStyle(
        fontSize = 18.sp,
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold
    )

    // ✅ Body Text (14–16sp)
    val bodyText = TextStyle(
        fontSize = 15.sp,
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal
    )

    // ✅ Caption / Label (12–13sp)
    val caption = TextStyle(
        fontSize = 12.sp,
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal
    )
    val seeAllText = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = Montserrat)
    val  labelLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = Montserrat
    )

}
/*✅ Global Font Sizes (Material 3 Style Guide)
Type	Style Name (Compose)	Recommended Size (sp)
App Title	headlineSmall / titleLarge	20–24sp
Section Header	titleMedium / titleSmall	16–18sp
Body Text	bodyMedium / bodySmall	14–16sp
Caption / Label	labelSmall	12–13sp
*/