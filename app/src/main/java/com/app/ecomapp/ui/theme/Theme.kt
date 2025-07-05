package com.app.ecomapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


import androidx.compose.ui.graphics.Color
import com.app.ecomapp.presentation.screens.profile.AppThemeOption
import com.app.ecomapp.presentation.screens.profile.ThemeState


// Define Light Theme Colors
private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = BlueLight,
    background = LightBackground,
    surface = LightCard,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = LightTextPrimary,
    onSurface = Color.Black
)

// Define Dark Theme Colors
private val DarkColorScheme = darkColorScheme(
    primary = BlueDark,
    secondary = BlueLight,
    background = DarkBackground,
    surface = DarkCard,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = DarkTextPrimary,
    onSurface = Color.White
)

@Composable
fun MyAppTheme(
    themeState: ThemeState,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeState.appTheme) {
        AppThemeOption.LIGHT -> false
        AppThemeOption.DARK -> true
        AppThemeOption.SYSTEM -> isSystemInDarkTheme()
    }

    MaterialTheme(
      //  colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        colorScheme = if (darkTheme) LightColorScheme else LightColorScheme,
        typography = AppTypography, // Apply the custom typography
        shapes = AppShapes,
        content = content
    )
}

/*
@Composable
fun MyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Detect System Theme
    content: @Composable () -> Unit
) {
   // val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val colorScheme =  LightColorScheme
    val view = LocalView.current
    val context = LocalContext.current

    if (!view.isInEditMode) {
        val window = (context as? Activity)?.window
        window?.let {
            WindowCompat.setDecorFitsSystemWindows(it, false)
            it.statusBarColor = colorScheme.primary.toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography, // Apply the custom typography
        shapes = AppShapes,
        content = content
    )
}*/
