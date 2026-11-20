package com.app.ecomapp.presentation.screens.home// TODO: this file is for alternate code for homepage if in case face any issue while fatching api call

/*
 val homeResponse = viewModel.homeResponse.collectAsState().value // ✅ Get API state

// ✅ Show Loader if API is in loading state
if (homeResponse is Resource.Loading) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator() // ✅ Loader
    }
} else {
    Scaffold(
        topBar = {
            userName?.let {
                MyTopAppBar(
                    it,
                    onSearchClick = {},
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
        ) {
            // ✅ API data available, show content
            if (homeResponse is Resource.Success) {
                val data = homeResponse.data // ✅ Extract successful data
                AutoSlidingBanner(data?.sliderImageData)
                Spacer_16dp()
                CategoriesListing(data?.categories)
                Spacer_16dp()
            } else if (homeResponse is Resource.Error) {
                Text(
                    text = "Error: ${homeResponse.message}",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
*/


//-----------------------------------------------------------------------------------------------------

/*
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoSlidingBanner( sliderImageData: List<SliderImageData>?) {

    val pagerState = rememberPagerState(pageCount = { sliderImageData?.size!! })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000) // Delay before auto-slide
            coroutineScope.launch {
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(
                    nextPage,
                    animationSpec = tween(800)
                ) // Smooth scroll
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp)) // Space between pager & dots
        HorizontalPager(state = pagerState) { page ->
            Image(
                painter = rememberAsyncImagePainter(sliderImageData[page]),
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.height(8.dp)) // Space between pager & dots

        sliderImageData?.size?.let {
            DotsIndicator(
                totalDots = it,
                selectedIndex = pagerState.currentPage,
                dotSize = 8.dp
            )
        }


        */
/*
                Card(
                    shape = RoundedCornerShape(9),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xff53b175).copy(alpha = 0.1f), // Set your card background color
                        contentColor = Color.White // Set text or icon color inside the card
                    ),
                    modifier = Modifier
                        .height(190.dp)
                        .padding(8.dp),
                    border = BorderStroke(1.dp, Color(0xff53b175).copy(alpha = 0.7f))
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter("it"),
                            contentDescription = null,
                            modifier = Modifier
                                .height(75.dp)
                                .width(112.dp),
                            contentScale = ContentScale.Crop,
                        )
                        Spacer(modifier = Modifier.fillMaxHeight(0.2f))
                        Text(
                            text = "text", softWrap = true,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }*//*

    }
}
*/

//=====================================================================
/*
@OptIn(ExperimentalCoilApi::class)
@Composable
fun ProductListing(sectionData: List<SectionData>?) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        sectionData?.size?.let {
            items(it) { index ->
                Text(
                    text = sectionData[index].sectionName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    sectionData[index].productData?.size?.let { productIndex ->
                        items(productIndex) { product ->
                            FoodItemComposable(sectionData[index].productData[product],
                                addingToCart = { id -> },
                                viewProduct = { })
                        }
                    }
                }
            }
        }
    }
}*/

/**  BELOW CODE FOR DARK N LIGHT MODE STILL IN PROGRESS  */
/*


import androidx.compose.ui.graphics.Color

// Primary Colors
val BluePrimary = Color(0xFF1E88E5) // Main Blue (Used in Register Screen)
val BlueDark = Color(0xFF1565C0) // Darker Shade of Blue
val BlueLight = Color(0xFF64B5F6) // Lighter Shade of Blue

// Background Colors
val LightBackground = Color(0xFFF7F7F7) // Light mode background
val DarkBackground = Color(0xFF121212) // Dark mode background

// Text Colors
val LightTextPrimary = Color(0xFF212121) // Primary text color in light mode
val DarkTextPrimary = Color(0xFFFFFFFF) // Primary text color in dark mode

// Error Colors
val ErrorColor = Color(0xFFB00020) // Standard Material error color
val ErrorContainer = Color(0xFFEF9A9A) // Light error background
val OnErrorContainer = Color(0xFFB00020) // Text color for error containers

// Outline Colors
val LightOutline = Color(0xFFE0E0E0) // Light outline (subtle borders)
val DarkOutline = Color(0xFF3A3A3A) // Dark outline

// Surface Variant (Used for backgrounds, cards, or surfaces that are muted)
val LightSurfaceVariant = Color(0xFFE0E0E0)
val DarkSurfaceVariant = Color(0xFF424242)

// Scrim Color (for overlays, dark background for modals)
val ScrimColor = Color(0x99000000) // Semi-transparent black

// On-Colors
val OnPrimary = Color.White
val OnSecondary = Color.Black
val OnBackground = LightTextPrimary
val OnSurface = Color.Black


// Button Colors
val ButtonLight = Color(0xFF1E88E5) // Primary Blue Button for Light Mode
val ButtonDark = Color(0xFF0D47A1) // Darker Blue for Dark Mode

// Card Colors
val LightCard = Color(0xFFFFFFFF) // White Card Background
val DarkCard = Color(0xFF1E1E1E) // Dark Card Background

val DarkBackgroundMuted = Color(0xFF1E1E1E) // A lighter, muted dark background (not too deep black)
val DarkSurfaceMuted = Color(0xFF2A2A2A) // A surface color that's darker than the background but still not pure black
val DarkTextPrimaryMuted = Color(0xFFE0E0E0) // Lighter text for dark mode
val DarkOnSurfaceMuted = Color(0xFFB0B0B0) // Lighter color for text on surfaces

val white = Color(0XFFFFFFFF)
val background = Color(0XFFFFFFFF)
val lightbox = Color(0XFFF3F3F3)
val lightSilver = Color(209,215,224)

val cottonBall = Color(241, 244, 253)
val titleTextColor = Color(0xff1d2635)
val subTitleTextColor = Color(0xff797878)

val skyBlue = Color(0xff1790C7)
val lightBlue = Color(0xff5C3BFF)


val orange = Color(0xffE65829)
val red = Color(0xffFF3737)
val lightorange = Color(0xffFFE9DF)

val lightGrey = Color(0xffE1E2E4)
val grey = Color(0xffA1A3A6)
val darkgrey = Color(0xff747F8F)
val lightgraybg=Color(0xffF8F8F8)
val lightsilverbox = Color(0xffF6F6F6)

val iconColor = Color(0xffa8a09b)
val yellowColor = Color(0xfffbba01)

val black = Color(0xff20262C)
val lightblack = Color(0xff5F5F60)
val lightgrey = Color(0xFFE0E0E0)

val YellowMain = Color(0xFFF0B428)
val DarkBlue = Color(0xFF181B37)
val MainWhiteColor = Color(0xFFF4F4F4)
val GrayColor = Color(0xFFBBB2A7)

val PrimaryColor = Color(0xFFFF4747)
val PrimaryVariantColor = Color(0xFFCA3D49)
val AccentColor = Color(0xFFC62F79)
val BackgroundContent = Color(0xFFf6f6f6)
val lightSurface =  Color(0xFFf5f5f5)


val GreenLight = Color(0xff53b175).copy(alpha = 0.1f)
val GreenDark = Color(0xff53b175).copy(alpha = 0.7f)

val OrangeLight = Color(0xffF8A44C).copy(alpha = 0.1f)
val OrangeDark = Color(0xffF8A44C).copy(alpha = 0.7f)

val PinkLight = Color(0xffF7A593).copy(alpha = 0.25f)
val PinkDark = Color(0xffF7A593)

val PurpleLight = Color(0xffD3B0E0).copy(alpha = 0.25f)
val PurpleDark = Color(0xffD3B0E0)

val YellowLight = Color(0xffFDE598).copy(alpha = 0.25f)
val YellowDark = Color(0xffFDE598)

val BlueLightNew = Color(0xffB7DFF5).copy(alpha = 0.25f)
val BlueDarkNew = Color(0xffB7DFF5)


*/
/*UI Element | Light Mode | Dark Mode
Card Background | MaterialTheme.colorScheme.surface | MaterialTheme.colorScheme.surface
Text (Primary Text) | MaterialTheme.colorScheme.onSurface | MaterialTheme.colorScheme.onSurface
Text (Secondary Text) | MaterialTheme.colorScheme.onSurfaceVariant | MaterialTheme.colorScheme.onSurfaceVariant
Background | MaterialTheme.colorScheme.background | MaterialTheme.colorScheme.background
Surface Background | MaterialTheme.colorScheme.surface | MaterialTheme.colorScheme.surface
Button Background | MaterialTheme.colorScheme.primary | MaterialTheme.colorScheme.primary
Button Text | MaterialTheme.colorScheme.onPrimary | MaterialTheme.colorScheme.onPrimary
Error Text | MaterialTheme.colorScheme.error | MaterialTheme.colorScheme.error
Error Background | MaterialTheme.colorScheme.errorContainer | MaterialTheme.colorScheme.errorContainer
TextField Background | MaterialTheme.colorScheme.surfaceVariant | MaterialTheme.colorScheme.surfaceVariant
Snackbar Background | MaterialTheme.colorScheme.surface | MaterialTheme.colorScheme.surface
Icon Type | Use this color | Why
Primary Icons | MaterialTheme.colorScheme.onBackground | High contrast against background, readable
Icons on Card/Surface | MaterialTheme.colorScheme.onSurface | High contrast against surface elements (like cards or dialogs)
Secondary Icons | MaterialTheme.colorScheme.onSurfaceVariant | Slightly muted, good for less prominent icons
Icons on Primary Btn | MaterialTheme.colorScheme.onPrimary | Used for icons inside buttons with primary background

*//*








package com.app.ecomapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf


import androidx.compose.ui.graphics.Color
import com.app.ecomapp.presentation.screens.profile.AppThemeOption
import com.app.ecomapp.presentation.screens.profile.ThemeState
// Light Color Scheme (Material 3 based)
private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = OnPrimary,
    primaryContainer = BlueLight,
    onPrimaryContainer = Color.Black, // Black text on primary container

    secondary = BlueLight,
    onSecondary = OnSecondary,
    secondaryContainer = BlueLight.copy(alpha = 0.1f),
    onSecondaryContainer = Color.Black,

    tertiary = BlueLight,
    onTertiary = Color.Black,
    tertiaryContainer = BlueLight.copy(alpha = 0.1f),
    onTertiaryContainer = Color.Black,

    background = LightBackground,
    onBackground = OnBackground,

    surface = LightCard,
    onSurface = OnSurface,
    surfaceVariant =  Color.Black,
    onSurfaceVariant = LightSurfaceVariant,

    error = ErrorColor,
    onError = Color.White,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,

    outline = LightOutline,
    outlineVariant = LightSurfaceVariant,
    scrim = ScrimColor
)
private val DarkColorScheme = darkColorScheme(
    primary = BlueDark, // Keep your original primary blue color
    onPrimary = Color.White,
    primaryContainer = BlueDark.copy(alpha = 0.2f),
    onPrimaryContainer = Color.White,

    secondary = BlueLight,
    onSecondary = Color.Black,
    secondaryContainer = BlueDark.copy(alpha = 0.1f),
    onSecondaryContainer = Color.White,

    tertiary = BlueLight,
    onTertiary = Color.White,
    tertiaryContainer = BlueDark.copy(alpha = 0.1f),
    onTertiaryContainer = Color.White,

    background = DarkBackgroundMuted, // Muted background instead of pure black
    onBackground = DarkTextPrimaryMuted, // Lighter text for better contrast

    surface = DarkSurfaceMuted, // Muted surface color
    onSurface = DarkOnSurfaceMuted, // Lighter text on surface

    surfaceVariant = DarkTextPrimary, // Slightly lighter surface color
    onSurfaceVariant = Color(0xFF2A2A2A), // Muted secondary text

    error = ErrorColor,
    onError = Color.White,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,

    outline = DarkOutline,
    outlineVariant = DarkSurfaceVariant,
    scrim = ScrimColor
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
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = AppTypography, // Apply the custom typography
        shapes = AppShapes,
        content = content
    )
}

*/
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

