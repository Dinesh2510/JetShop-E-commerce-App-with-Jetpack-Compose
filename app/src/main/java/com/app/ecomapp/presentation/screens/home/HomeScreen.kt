package com.app.ecomapp.presentation.screens.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.home.BrandData
import com.app.ecomapp.data.models.home.CategoryData
import com.app.ecomapp.data.models.home.HomeResponse
import com.app.ecomapp.data.models.home.ProductData
import com.app.ecomapp.data.models.home.SliderImageData
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator
import com.app.ecomapp.presentation.components.LoginPromptDialog
import com.app.ecomapp.presentation.components.Spacer_12dp
import com.app.ecomapp.presentation.components.Spacer_16dp
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.auth.AuthViewModel
import com.app.ecomapp.presentation.screens.wishlist.WishlistViewModel
import com.app.ecomapp.ui.theme.AppMainTypography
import com.app.ecomapp.ui.theme.BackgroundContent
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.Constants.Companion.Ruppes
import com.app.ecomapp.utils.toWishlistProduct
import com.compose.jetshop.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

private const val HOME_LOG_TAG = "HOME_DEBUG"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    wishlistViewModel: WishlistViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val userId by viewModel.userId.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userIsPrimeActive by viewModel.userIsPrimeActive.collectAsState()
    val email by viewModel.userEmail.collectAsState()
    val contact by viewModel.userPhone.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val homeResponseState by viewModel.homeResponse.collectAsState()

    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()

    // Stop refreshing indicator when state finishes loading
    LaunchedEffect(Unit) {
        viewModel.cooldownMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    val isLoggedIn = remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        val loggedIn = !userId.isNullOrBlank()
        isLoggedIn.value = loggedIn
        Log.d(HOME_LOG_TAG, "HomeScreen Auth State Updated -> isLoggedIn=$loggedIn, userId='$userId'")

        if (loggedIn) {
            try {
                authViewModel.sendUserFCM()
            } catch (e: Exception) {
                Log.e(HOME_LOG_TAG, "sendUserFCM failed: ${e.message}")
            }
        }
    }

    LaunchedEffect(homeResponseState) {
        when (val state = homeResponseState) {
            is Resource.Loading -> Log.d(HOME_LOG_TAG, "UI State -> LOADING")
            is Resource.Success -> {
                val d = state.data
                Log.d(
                    HOME_LOG_TAG,
                    "UI State -> SUCCESS (sliders=${d?.sliderImageData?.size}, " +
                            "categories=${d?.categories?.size}, " +
                            "brands=${d?.brandsData?.size}, " +
                            "sections=${d?.sectionData?.size})"
                )
            }
            is Resource.Error -> Log.e(HOME_LOG_TAG, "UI State -> ERROR: ${state.message}")
            null -> Log.d(HOME_LOG_TAG, "UI State -> NULL")
        }
    }

    val showLoginDialog = remember { mutableStateOf(false) }
    var isProfileBannerDismissed by remember { mutableStateOf(false) }

    LoginPromptDialog(
        showDialog = showLoginDialog.value,
        onDismiss = { showLoginDialog.value = false },
        onLoginClick = {
            showLoginDialog.value = false
            navController.navigate(Screen.Login.route)
        }
    )

    AskForPermission()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        MyTopAppBar(
            name = userName ?: "",
            userIsPrimeActive = userIsPrimeActive,
            onSearchClick = { navController.navigate(Screen.SearchScreen.route) },
            onClickPrime = {
                if (isLoggedIn.value) {
                    val intent = Intent(context, PrimePayScreen::class.java).apply {
                        putExtra("user_id", userId)
                        putExtra("appName", "JetShop")
                        putExtra("email", email)
                        putExtra("contact", contact)
                    }
                    context.startActivity(intent)
                } else {
                    showLoginDialog.value = true
                }
            }
        )

        when (val state = homeResponseState) {
            is Resource.Loading, null -> {
                if (!isRefreshing) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CenteredCircularProgressIndicator()
                    }
                }
            }

            is Resource.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = state.message ?: "Failed to load products",
                            fontFamily = Montserrat,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )
                        Spacer_12dp()
                        Button(
                            onClick = {
                                Log.d(HOME_LOG_TAG, "Retry clicked by user")
                                viewModel.getSliderCategoryProducts()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                        ) {
                            Text("Retry", fontFamily = Montserrat)
                        }
                    }
                }
            }

            is Resource.Success -> {
                val apiData = state.data

                val hasNoContent = apiData == null || (
                        apiData.sliderImageData.isNullOrEmpty() &&
                                apiData.categories.isNullOrEmpty() &&
                                apiData.brandsData.isNullOrEmpty() &&
                                apiData.sectionData.isNullOrEmpty()
                        )

                if (hasNoContent) {
                    Log.w(HOME_LOG_TAG, "API returned Success, but all lists are null or empty!")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Nothing to show right now", fontFamily = Montserrat)
                            Spacer_12dp()
                            Button(
                                onClick = { viewModel.getSliderCategoryProducts() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Text("Refresh", fontFamily = Montserrat)
                            }
                        }
                    }
                } else {
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = {
                            viewModel.getSliderCategoryProducts(isPullToRefresh = true)
                        },
                        state = pullToRefreshState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        indicator = {
                            CustomHomePullToRefreshIndicator(
                                state = pullToRefreshState,
                                isRefreshing = isRefreshing,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 10.dp)
                            )
                        }
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Complete Profile Alert Banner
                            if (isLoggedIn.value && !isProfileBannerDismissed && userProfile != null) {
                                val isFirstNameMissing = userProfile?.firstName.isNullOrBlank()
                                val isLastNameMissing = userProfile?.lastName.isNullOrBlank()
                                val isPhoneMissing = userProfile?.phone.isNullOrBlank()
                                val isImageMissing = userProfile?.profileImage.isNullOrBlank()
                                val isAddressMissing = userProfile?.defaultAddress == null

                                val isProfileIncomplete = isFirstNameMissing || isLastNameMissing ||
                                        isPhoneMissing || isImageMissing || isAddressMissing

                                if (isProfileIncomplete) {
                                    item(key = "complete_profile_banner") {
                                        val (message, targetRoute) = when {
                                            isAddressMissing -> "Add delivery address for faster checkout" to Screen.UpdateProfile.route
                                            isPhoneMissing -> "Add your phone number for order updates" to Screen.UpdateProfile.route
                                            isImageMissing -> "Add a profile picture to complete your account" to Screen.UpdateProfile.route
                                            else -> "Complete your profile details for faster checkout" to Screen.UpdateProfile.route
                                        }

                                        CompleteProfileBanner(
                                            missingFieldsText = message,
                                            onCompleteClick = { navController.navigate(targetRoute) },
                                            onDismiss = { isProfileBannerDismissed = true }
                                        )
                                    }
                                }
                            }

                            // Sliders
                            apiData?.sliderImageData?.let { images ->
                                if (images.isNotEmpty()) {
                                    item(key = "home_sliders") {
                                        AutoSlidingBannerVertical(images.take(10))
                                    }
                                }
                            }

                            // Categories
                            apiData?.categories?.let { categories ->
                                if (categories.isNotEmpty()) {
                                    item(key = "home_categories") {
                                        CategoriesListing(navController, categories) {
                                            navController.navigate(Screen.CategoryBrandSplit.createRoute("category"))
                                        }
                                    }
                                }
                            }

                            // Brands
                            apiData?.brandsData?.let { brands ->
                                if (brands.isNotEmpty()) {
                                    item(key = "home_brands") {
                                        BrandListing(navController, brands) {
                                            navController.navigate(Screen.CategoryBrandSplit.createRoute("brand"))
                                        }
                                    }
                                }
                            }

                            // Product Sections
                            apiData?.sectionData?.forEach { section ->
                                item(key = "header_${section.sectionName}") {
                                    SectionHeader(
                                        title = section.sectionName,
                                        onSeeAllClick = {
                                            navController.navigate(Screen.AllProductList.route)
                                        }
                                    )
                                }
                                item(key = "products_${section.sectionName}") {
                                    ProductListing(
                                        productData = section.productData,
                                        viewModel = viewModel,
                                        wishlistViewModel = wishlistViewModel,
                                        isLoggedIn = isLoggedIn,
                                        navController = navController
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CUSTOM BRANDED PULL TO REFRESH INDICATOR
// -------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomHomePullToRefreshIndicator(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier
) {
    val distanceFraction = state.distanceFraction.coerceIn(0f, 1.5f)
    val isVisible = isRefreshing || distanceFraction > 0.1f

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(200)) + scaleIn(spring(dampingRatio = 0.6f)),
        exit = fadeOut(tween(250)) + scaleOut(targetScale = 0.7f),
        modifier = modifier
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse_radar")

        val spinAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(900, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "spin_angle"
        )

        val shimmerTranslate by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 300f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer_badge"
        )

        val animatedScale by animateFloatAsState(
            targetValue = if (distanceFraction >= 1f || isRefreshing) 1.05f else 0.9f + (distanceFraction * 0.1f),
            animationSpec = spring(dampingRatio = 0.5f),
            label = "scale_anim"
        )

        val isTriggered = distanceFraction >= 1f && !isRefreshing

        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF0F172A),
            shadowElevation = 12.dp,
            border = BorderStroke(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = if (isTriggered || isRefreshing) {
                        listOf(Color(0xFF38BDF8), Color(0xFF2563EB), Color(0xFF818CF8))
                    } else {
                        listOf(Color(0xFF334155), Color(0xFF1E293B))
                    }
                )
            ),
            modifier = Modifier.graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(28.dp)) {
                        val strokeWidth = 2.8.dp.toPx()

                        drawCircle(
                            color = Color(0xFF1E293B),
                            style = Stroke(width = strokeWidth)
                        )

                        if (isRefreshing) {
                            drawArc(
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        Color(0xFF2563EB).copy(alpha = 0.1f),
                                        Color(0xFF38BDF8),
                                        Color(0xFF60A5FA)
                                    )
                                ),
                                startAngle = spinAngle,
                                sweepAngle = 270f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        } else {
                            drawArc(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF38BDF8), Color(0xFF2563EB))
                                ),
                                startAngle = -90f,
                                sweepAngle = (distanceFraction * 360f).coerceIn(0f, 360f),
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }

                    if (isRefreshing) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8))
                        )
                    } else if (isTriggered) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(15.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.ShoppingBag,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier
                                .size(14.dp)
                                .rotate(distanceFraction * 25f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                AnimatedContent(
                    targetState = when {
                        isRefreshing -> "Refreshing Products..."
                        isTriggered -> "Release to Refresh"
                        else -> "Pull Down to Refresh"
                    },
                    transitionSpec = {
                        fadeIn(tween(180)) togetherWith fadeOut(tween(180))
                    },
                    label = "text_morph"
                ) { statusText ->
                    val textBrush = if (isRefreshing || isTriggered) {
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFE2E8F0),
                                Color(0xFF38BDF8),
                                Color(0xFFE2E8F0)
                            ),
                            start = Offset.Zero,
                            end = Offset(x = shimmerTranslate, y = 0f)
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8))
                        )
                    }

                    Text(
                        text = statusText,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        style = TextStyle(brush = textBrush)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// EXISTING SECTIONS & COMPONENTS
// -------------------------------------------------------------

@Composable
fun CompleteProfileBanner(
    missingFieldsText: String,
    onCompleteClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onCompleteClick() },
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFEFF6FF),
        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
        shadowElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFDBEAFE),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ManageAccounts,
                        contentDescription = "Profile",
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Complete Your Profile",
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = missingFieldsText,
                    fontFamily = Montserrat,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF2563EB)
            ) {
                Text(
                    text = "Complete",
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(26.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    name: String,
    userIsPrimeActive: String?,
    onSearchClick: () -> Unit,
    onClickPrime: () -> Unit
) {
    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()

    Surface(
        color = Color.White,
        shadowElevation = 0.5.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = CenterVertically
        ) {
            AnimatedGreeting(name, true)

            Row(verticalAlignment = CenterVertically) {
                if (userIsPrimeActive == "1") {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip { Text("You're a Prime Member!", style = AppMainTypography.seeAllText) }
                        },
                        state = tooltipState
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.premium),
                            contentDescription = "Prime Member",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    scope.launch {
                                        tooltipState.show()
                                    }
                                }
                        )
                    }
                } else {
                    ShimmerPrimeBadge { onClickPrime() }
                }

                Spacer_12dp()

                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.size(36.dp)
                ) {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAllClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Montserrat,
            color = Color(0xFF0F172A)
        )
        Text(
            text = "See All",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2563EB),
            fontFamily = Montserrat,
            modifier = Modifier.clickable { onSeeAllClick() }
        )
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ProductListing(
    productData: List<ProductData>,
    viewModel: HomeViewModel,
    wishlistViewModel: WishlistViewModel,
    isLoggedIn: MutableState<Boolean>,
    navController: NavHostController,
) {
    val showLoginDialog = remember { mutableStateOf(false) }

    LoginPromptDialog(
        showDialog = showLoginDialog.value,
        onDismiss = { showLoginDialog.value = false },
        onLoginClick = {
            showLoginDialog.value = false
            navController.navigate(Screen.Login.route)
        }
    )

    val quantityMap by viewModel.cartQuantities.collectAsState()

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(
            count = productData.size,
            key = { index -> productData[index].productId }
        ) { index ->
            val product = productData[index]
            var isInWishlist by remember { mutableStateOf(false) }

            LaunchedEffect(product.productId) {
                wishlistViewModel.isProductInWishlist(product.productId) {
                    isInWishlist = it
                }
            }

            val wishlistProduct = product.toWishlistProduct()
            val productQuantity = quantityMap[product.productId]
                ?: product.user_cart_quantity?.toIntOrNull()
                ?: 0

            ProductCard(
                viewModel = viewModel,
                product = product,
                productQuantity = productQuantity,
                onAddToCart = { productId ->
                    if (isLoggedIn.value) {
                        viewModel.updateCartQuantity(productId, productQuantity + 1)
                        viewModel.addToCart(productId, "1")
                    } else {
                        showLoginDialog.value = true
                    }
                },
                onViewProduct = { selectedProduct ->
                    navController.navigate("${Screen.ProductDetails.route}/${selectedProduct.productId}")
                },
                onWishlistToggle = {
                    if (isInWishlist) {
                        wishlistViewModel.removeFromWishlist(wishlistProduct)
                    } else {
                        wishlistViewModel.addToWishlist(wishlistProduct)
                    }
                    isInWishlist = !isInWishlist
                },
                isInWishlist = isInWishlist,
                onRemoveFromCart = { productId ->
                    viewModel.removeFromCart(productId)
                }
            )
        }
    }
}

@Composable
fun ProductCard(
    product: ProductData,
    productQuantity: Int,
    viewModel: HomeViewModel,
    onAddToCart: (String) -> Unit,
    onRemoveFromCart: (String) -> Unit,
    onViewProduct: (ProductData) -> Unit,
    onWishlistToggle: () -> Unit,
    isInWishlist: Boolean,
) {
    val origPrice = product.productPrice.toDoubleOrNull() ?: 0.0
    val discPrice = product.productDiscountPrice.toDoubleOrNull() ?: origPrice

    val discountPercent = if (origPrice > discPrice && origPrice > 0) {
        (((origPrice - discPrice) / origPrice) * 100).toInt()
    } else 0

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .width(175.dp)
            .clickable { onViewProduct(product) }
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                AsyncImage(
                    model = product.productImageUrl,
                    contentDescription = product.productName,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                )

                if (discountPercent > 0) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFECFDF5),
                        border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "$discountPercent% OFF",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669),
                            fontFamily = Montserrat,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.92f),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .size(32.dp)
                ) {
                    IconButton(onClick = onWishlistToggle) {
                        Icon(
                            imageVector = if (isInWishlist) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isInWishlist) Color(0xFFEF4444) else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.productName,
                fontSize = 13.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "$Ruppes${product.productDiscountPrice}",
                    color = Color(0xFF2563EB),
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
                if (discountPercent > 0) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$Ruppes${product.productPrice}",
                        fontSize = 11.sp,
                        fontFamily = Montserrat,
                        textDecoration = TextDecoration.LineThrough,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (productQuantity > 0) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.updateCartQuantity(product.productId, productQuantity - 1)
                                onRemoveFromCart(product.productId)
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(15.dp)
                            )
                        }

                        Text(
                            text = productQuantity.toString(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Montserrat,
                            color = Color(0xFF1D4ED8)
                        )

                        IconButton(
                            onClick = {
                                viewModel.updateCartQuantity(product.productId, productQuantity + 1)
                                onAddToCart(product.productId)
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            } else {
                Button(
                    onClick = { onAddToCart(product.productId) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                ) {
                    Row(verticalAlignment = CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Add to Cart",
                            modifier = Modifier.size(15.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Montserrat,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoriesListing(
    navController: NavController,
    categories: List<CategoryData>?,
    navigateToCategories: () -> Unit,
) {
    SectionHeader(title = "Category", onSeeAllClick = navigateToCategories)
    Spacer_8dp()
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(
            count = categories?.size ?: 0,
            key = { index -> categories!![index].id },
            contentType = { index -> categories!![index].name }
        ) { index ->
            CommonCircleItem(item = ItemType.Category(categories!![index])) {
                navController.navigate(Screen.CategoryBrandDetails.route + "/category/${categories[index].id}")
            }
        }
    }
}

@Composable
fun BrandListing(
    navController: NavController,
    brandData: List<BrandData>,
    navigateToBrand: () -> Unit,
) {
    SectionHeader(title = "Featured Brands", onSeeAllClick = navigateToBrand)
    Spacer_8dp()
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(
            count = brandData.size,
            key = { index -> brandData[index].brandId },
            contentType = { index -> brandData[index].brandName }
        ) { index ->
            CommonCircleItem(item = ItemType.Brand(brandData[index])) {
                navController.navigate(Screen.CategoryBrandDetails.route + "/brand/${brandData[index].brandId}")
            }
        }
    }
}

@Composable
fun CommonCircleItem(
    item: ItemType,
    onClick: () -> Unit
) {
    val (imageUrl, label) = when (item) {
        is ItemType.Brand -> item.brandData.brandLogo to item.brandData.brandName
        is ItemType.Category -> item.categoryData.icon to item.categoryData.name
    }

    Column(
        modifier = Modifier
            .width(72.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 2.dp,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.size(62.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = label,
                    modifier = Modifier.size(34.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer_8dp()

        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = Montserrat,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF334155),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

sealed class ItemType {
    data class Brand(val brandData: BrandData) : ItemType()
    data class Category(val categoryData: CategoryData) : ItemType()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoSlidingBannerVertical(
    sliderImageData: List<SliderImageData>?
) {
    val context = LocalContext.current

    val featuredBanners = remember(sliderImageData) {
        val activeTwos = sliderImageData?.filter { it.isActive == 2 } ?: emptyList()
        activeTwos.ifEmpty { sliderImageData ?: emptyList() }
    }

    if (featuredBanners.isEmpty()) return

    val startIndex = Int.MAX_VALUE / 2

    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { Int.MAX_VALUE }
    )

    val pageColors = remember { mutableStateMapOf<Int, Color>() }
    val actualIndex = pagerState.currentPage % featuredBanners.size
    val currentTargetColor = pageColors[actualIndex] ?: BackgroundContent

    val animatedBackgroundColor by animateColorAsState(
        targetValue = currentTargetColor,
        animationSpec = tween(800),
        label = "bgColor"
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            try {
                pagerState.animateScrollToPage(
                    page = pagerState.currentPage + 1,
                    animationSpec = tween(
                        durationMillis = 1000,
                        easing = FastOutSlowInEasing
                    )
                )
            } catch (_: Exception) {}
        }
    }

    LaunchedEffect(featuredBanners) {
        withContext(Dispatchers.IO) {
            featuredBanners.forEachIndexed { index, item ->
                try {
                    val loader = ImageLoader(context)
                    val request = ImageRequest.Builder(context)
                        .data(item.imageUrl)
                        .allowHardware(false)
                        .build()

                    val result = loader.execute(request)
                    val bitmap = (result.drawable as? BitmapDrawable)?.bitmap

                    bitmap?.let {
                        val palette = Palette.from(it).generate()
                        val extractedColor = palette.vibrantSwatch?.rgb ?: palette.dominantSwatch?.rgb
                        extractedColor?.let { color ->
                            pageColors[index] = Color(color)
                        }
                    }
                } catch (_: Exception) {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        animatedBackgroundColor.copy(alpha = 0.7f),
                        BackgroundContent
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer_8dp()

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp),
                contentPadding = PaddingValues(start = 50.dp, end = 50.dp),
                pageSpacing = 16.dp
            ) { page ->
                val actualPage = page % featuredBanners.size
                BannerImageVer(featuredBanners[actualPage].imageUrl)
            }

            Spacer_8dp()

            DotsIndicator(
                totalDots = featuredBanners.size,
                selectedIndex = actualIndex,
                dotSize = 8.dp
            )

            Spacer_16dp()
        }
    }
}

@Composable
fun BannerImageVer(imageUrl: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.1f),
                                Color.Black.copy(alpha = 0.45f)
                            ),
                            startY = 300f
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.35f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.15f)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
            )
        }
    }
}

@Composable
fun DotsIndicator(
    modifier: Modifier = Modifier,
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color = Color(0xFF2563EB),
    unSelectedColor: Color = Color(0xFFE2E8F0),
    dotSize: Dp = 6.dp,
    activeDotWidth: Dp = 22.dp,
    spacing: Dp = 6.dp
) {
    Row(
        modifier = modifier,
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally)
    ) {
        for (index in 0 until totalDots) {
            val isSelected = index == selectedIndex

            val targetWidth by animateDpAsState(
                targetValue = if (isSelected) activeDotWidth else dotSize,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "dotWidth"
            )

            val animatedColor by animateColorAsState(
                targetValue = if (isSelected) selectedColor else unSelectedColor,
                animationSpec = tween(durationMillis = 250),
                label = "dotColor"
            )

            Box(
                modifier = Modifier
                    .height(dotSize)
                    .width(targetWidth)
                    .clip(CircleShape)
                    .background(animatedColor)
            )
        }
    }
}

@Composable
fun ShimmerPrimeBadge(onClick: () -> Unit) {
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.5f),
        Color.White.copy(alpha = 0.95f),
        Color.White.copy(alpha = 0.5f)
    )

    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 250f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = 0f)
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF2563EB),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = "Prime",
            style = TextStyle(
                brush = shimmerBrush,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Montserrat
            ),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

fun getGreetingMessage(name: String?): List<String> {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val timeGreeting = when (hour) {
        in 5..11 -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        in 17..21 -> "Good Evening"
        else -> "Welcome back"
    }

    val firstName = name?.trim()
        ?.split("\\s+".toRegex())
        ?.firstOrNull()
        ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    return if (!firstName.isNullOrBlank()) {
        listOf(
            "$timeGreeting, $firstName 👋",
            "Welcome to JetShop",
            "Find your best deals today"
        )
    } else {
        listOf(
            "$timeGreeting 👋",
            "Welcome to JetShop",
            "Start shopping today"
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedGreeting(name: String, moveUp: Boolean) {
    val greetings = getGreetingMessage(name)
    var currentIndex by remember { mutableStateOf(0) }

    val slideDirection = if (moveUp) {
        AnimatedContentTransitionScope.SlideDirection.Up
    } else {
        AnimatedContentTransitionScope.SlideDirection.Down
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentIndex = (currentIndex + 1) % greetings.size
        }
    }

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = CenterVertically,
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(data = R.drawable.logo_trans)
                    .apply { crossfade(true) }
                    .build()
            ),
            modifier = Modifier
                .clip(CircleShape)
                .size(34.dp),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Spacer_8dp()

        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = {
                slideIntoContainer(slideDirection, tween(600)) + fadeIn(
                    animationSpec = tween(600)
                ) togetherWith slideOutOfContainer(slideDirection, tween(600)) + fadeOut(
                    animationSpec = tween(600)
                )
            },
            label = "greetingAnimation"
        ) { index ->
            Text(
                text = greetings[index],
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Montserrat,
                color = Color(0xFF0F172A)
            )
        }
    }
}

@Composable
fun AskForPermission() {
    val context = LocalContext.current

    val showDialog = remember { mutableStateOf(false) }
    val permissionDeniedMessage = remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val deniedPermissions = permissions.filterValues { !it }.keys
            if (deniedPermissions.isNotEmpty()) {
                permissionDeniedMessage.value = buildString {
                    append("The following permissions are required:\n")
                    deniedPermissions.forEach { permission ->
                        append("• ")
                        append(
                            when (permission) {
                                Manifest.permission.ACCESS_FINE_LOCATION -> "Location - To access your location"
                                Manifest.permission.POST_NOTIFICATIONS -> "Notifications - To send you notifications"
                                else -> "Unknown permission"
                            }
                        )
                        append("\n")
                    }
                }
                showDialog.value = true
            }
        }
    )

    val locationPermissionStatus = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    )

    val notificationPermissionStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
    } else {
        PackageManager.PERMISSION_GRANTED
    }

    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>().apply {
            if (locationPermissionStatus != PackageManager.PERMISSION_GRANTED) {
                add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && notificationPermissionStatus != PackageManager.PERMISSION_GRANTED) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (permissionsToRequest.isNotEmpty()) {
            launcher.launch(permissionsToRequest.toTypedArray())
        }
    }

    if (showDialog.value) {
        PermissionDeniedDialog(
            message = permissionDeniedMessage.value,
            onDismiss = { showDialog.value = false }
        )
    }
}

@Composable
fun PermissionDeniedDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        modifier = Modifier.padding(20.dp),
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Permission Denied", style = AppMainTypography.appTitle)
        },
        text = {
            Text(text = message, style = AppMainTypography.bodyText)
        },
        confirmButton = {
            Text(
                text = "OK",
                style = AppMainTypography.caption,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(15.dp)
                    .clickable { onDismiss() }
            )
        }
    )
}