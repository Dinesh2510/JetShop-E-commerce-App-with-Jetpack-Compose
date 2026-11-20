package com.app.ecomapp.presentation.screens.home

import android.app.Activity
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.auth.UserData
import com.app.ecomapp.data.models.details.ProductDetailsResponse
import com.app.ecomapp.data.models.details.Review
import com.app.ecomapp.data.models.details.ReviewsData
import com.app.ecomapp.data.models.home.ProductData
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator
import com.app.ecomapp.presentation.components.CustomOutlinedTextField
import com.app.ecomapp.presentation.components.LoginPromptDialog
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.wishlist.WishlistViewModel
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.Constants.Companion.Ruppes
import com.app.ecomapp.utils.UserDataStore
import com.app.ecomapp.utils.toWishlistProduct

@Composable
fun ProductDetailScreen(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
    productId: String,
    viewModel: HomeViewModel = hiltViewModel(),
    wishlistViewModel: WishlistViewModel = hiltViewModel(),
) {
    Log.d("ProductDetails", "Composable Launched with Product ID: $productId")

    val context = LocalContext.current
    val dataStoreHelper = remember { UserDataStore(context) }
    val user by dataStoreHelper.getUserData.collectAsState(initial = null)
    val productDetailsState by viewModel.productDetails.collectAsState()
    val isLoggedIn = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoggedIn.value = dataStoreHelper.isUserLoggedIn(context)
    }

    ChangeStatusBarColor(Color.Transparent, isDarkIcons = true)

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                Log.d("ProductDetails", "onDestroy() called")
            }
        })
    }

    CallApiForProductDetails(user, dataStoreHelper, productId, viewModel)

    when (productDetailsState) {
        is Resource.Success -> {
            val apiData = (productDetailsState as Resource.Success<ProductDetailsResponse?>).data
            SetDataUI(apiData, navController, wishlistViewModel, viewModel, isLoggedIn)
        }

        is Resource.Error -> {
            val errorMessage = (productDetailsState as Resource.Error).message
            Log.e("ProductDetails", "Error loading product: $errorMessage")
            Toast.makeText(context, errorMessage ?: "Failed to load product", Toast.LENGTH_SHORT).show()
        }

        is Resource.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CenteredCircularProgressIndicator()
            }
        }

        else -> {}
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("ProductDetails", "Composable Disposed")
        }
    }
}

@Composable
fun CallApiForProductDetails(
    user: UserData?,
    dataStoreHelper: UserDataStore,
    productId: String,
    viewModel: HomeViewModel
) {
    val addToCartResponse by viewModel.addToCartResponse.collectAsState()
    val removeToCartResponse by viewModel.removeToCartResponse.collectAsState()

    LaunchedEffect(user?.userId ?: "") {
        if (user?.userId.isNullOrEmpty()) {
            dataStoreHelper.getUserData.collect { userData ->
                viewModel.getProductDetails(userData?.userId ?: "", productId)
            }
        } else {
            viewModel.getProductDetails(user?.userId ?: "", productId)
        }
    }

    LaunchedEffect(addToCartResponse, removeToCartResponse) {
        if (addToCartResponse is Resource.Success || removeToCartResponse is Resource.Success) {
            viewModel.getProductDetails(user?.userId ?: "", productId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetDataUI(
    apiData: ProductDetailsResponse?,
    navController: NavHostController,
    wishlistViewModel: WishlistViewModel,
    viewModel: HomeViewModel,
    isLoggedIn: MutableState<Boolean>,
) {
    val productData = apiData?.product ?: return
    val cartData = apiData.cartData
    val ratingReviewData = apiData.reviewsData ?: ReviewsData()
    val quantityState = remember { mutableIntStateOf(cartData?.quantity ?: 0) }

    val reviewSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showReviewSheet by remember { mutableStateOf(false) }

    val writeReviewSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showWriteReviewSheet by remember { mutableStateOf(false) }

    val addReviewResponse by viewModel.addReview.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(addReviewResponse) {
        if (addReviewResponse is Resource.Success) {
            Toast.makeText(context, "Review added successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        modifier = Modifier.navigationBarsPadding(),
        bottomBar = {
            CheckoutBottomBar(
                navController = navController,
                isLoggedIn = isLoggedIn,
                productData = productData,
                quantityState = quantityState,
                addToCart = { viewModel.addToCart(productData.productId, "1") },
                removeFromCart = { viewModel.removeFromCart(productData.productId) },
                goToCart = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.ProductDetails.route) { inclusive = false }
                        launchSingleTop = true
                        navController.popBackStack()
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Modern Hero Section with Image and Controls
            ProductHeader(
                productData = productData,
                navController = navController,
                wishlistViewModel = wishlistViewModel
            )

            // Content Card with Rounded Top
            Surface(
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 22.dp)
                ) {
                    ProductDetails(productData)

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(20.dp))

                    ReviewSection(
                        isLoggedIn = isLoggedIn,
                        productData = productData,
                        ratingReviewData = ratingReviewData,
                        onViewAllReviewsClick = { showReviewSheet = true },
                        onWriteReviewClick = { showWriteReviewSheet = true }
                    )

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    if (showReviewSheet) {
        ModalBottomSheet(
            onDismissRequest = { showReviewSheet = false },
            sheetState = reviewSheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            ReviewListContent(reviews = ratingReviewData)
        }
    }

    if (showWriteReviewSheet) {
        ModalBottomSheet(
            onDismissRequest = { showWriteReviewSheet = false },
            sheetState = writeReviewSheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            WriteReviewContent(
                onSubmit = { rating, title, description ->
                    viewModel.AddUpdateReview(
                        productData.productId,
                        rating.toFloat(),
                        title,
                        description
                    )
                    showWriteReviewSheet = false
                }
            )
        }
    }
}

@Composable
fun ProductHeader(
    productData: ProductData,
    navController: NavHostController,
    wishlistViewModel: WishlistViewModel
) {
    var isInWishlist by remember { mutableStateOf(false) }
    val wishlistProduct = remember(productData) { productData.toWishlistProduct() }

    LaunchedEffect(productData) {
        wishlistViewModel.isProductInWishlist(productData.productId) {
            isInWishlist = it
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White) // ✅ Changed to pure White
    ) {
        // Hero Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .statusBarsPadding()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(productData.productImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = productData.productName,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentScale = ContentScale.Fit
            )
        }

        // Top Floating Action Row (Back, Rating Tag, Wishlist)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 3.dp,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.size(42.dp)
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Rating Chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = productData.productRating,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Montserrat,
                            color = Color(0xFF0F172A)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Wishlist Floating Button
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.size(42.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (isInWishlist) {
                                wishlistViewModel.removeFromWishlist(wishlistProduct)
                            } else {
                                wishlistViewModel.addToWishlist(wishlistProduct)
                            }
                            isInWishlist = !isInWishlist
                        }
                    ) {
                        Icon(
                            imageVector = if (isInWishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isInWishlist) Color(0xFFEF4444) else Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductDetails(productData: ProductData) {
    val origPrice = productData.productPrice.toDoubleOrNull() ?: 0.0
    val discPrice = productData.productDiscountPrice.toDoubleOrNull() ?: origPrice

    val discountPercent = if (origPrice > discPrice && origPrice > 0) {
        (((origPrice - discPrice) / origPrice) * 100).toInt()
    } else 0

    // Multi-paragraph realistic dummy fallback if description is absent
    val defaultDescription = """
        Experience premium quality with this top-rated product, meticulously designed to blend elegance, performance, and everyday durability. Crafted from high-grade materials, it offers unmatched reliability tailored to your modern lifestyle.

        Engineered for peak efficiency, it comes loaded with state-of-the-art functionality, intuitive usability, and seamless ergonomics. Whether for daily essentials or professional demands, it guarantees a superior user experience every single day.

        Includes official manufacturer warranty, fast dispatch, and hassle-free 30-day replacements. Join thousands of satisfied customers and upgrade your experience today!
    """.trimIndent()

    Column(modifier = Modifier.fillMaxWidth()) {
        // Product Name
        Text(
            text = productData.productName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Montserrat,
            color = Color(0xFF0F172A),
            lineHeight = 26.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Price Row with Discount Chip
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "$Ruppes${productData.productDiscountPrice}",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = Montserrat,
                color = Color(0xFF2563EB)
            )

            if (discountPercent > 0) {
                Text(
                    text = "$Ruppes${productData.productPrice}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Montserrat,
                    color = Color(0xFF94A3B8),
                    textDecoration = TextDecoration.LineThrough
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFECFDF5),
                    border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                ) {
                    Text(
                        text = "$discountPercent% OFF",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF059669),
                        fontFamily = Montserrat,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description Section
        Text(
            text = "Description",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Montserrat,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(8.dp))

        val finalDescription = if (productData.productDescription.isNullOrBlank()) {
            defaultDescription
        } else {
            productData.productDescription
        }

        // Render paragraphs with neat spacing
        finalDescription.split("\n\n").forEach { paragraph ->
            if (paragraph.isNotBlank()) {
                Text(
                    text = paragraph.trim(),
                    fontSize = 13.sp,
                    fontFamily = Montserrat,
                    color = Color(0xFF475569),
                    lineHeight = 21.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun ReviewSection(
    isLoggedIn: MutableState<Boolean>,
    productData: ProductData,
    ratingReviewData: ReviewsData,
    onWriteReviewClick: () -> Unit,
    onViewAllReviewsClick: () -> Unit,
) {
    val totalReviews = ratingReviewData.totalReviewCount.toFloat().coerceAtLeast(1f)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onViewAllReviewsClick() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Ratings & Reviews",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Montserrat,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEFF6FF)
                ) {
                    Text(
                        text = "${ratingReviewData.totalReviewCount}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB),
                        fontFamily = Montserrat,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "View All",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2563EB),
                    fontFamily = Montserrat
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(0.9f)
                ) {
                    Text(
                        text = productData.productRating,
                        fontSize = 42.sp,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )
                    RatingStars(rating = productData.productRating.toFloatOrNull() ?: 0f)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${ratingReviewData.totalReviewCount} verified",
                        fontSize = 11.sp,
                        fontFamily = Montserrat,
                        color = Color(0xFF64748B)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1.3f)) {
                    val fiveStarProgress by animateFloatAsState(
                        targetValue = ratingReviewData.fiveStarReviewCount / totalReviews,
                        animationSpec = tween(600),
                        label = "5Star"
                    )
                    val fourStarProgress by animateFloatAsState(
                        targetValue = ratingReviewData.fourStarReviewCount / totalReviews,
                        animationSpec = tween(600),
                        label = "4Star"
                    )
                    val threeStarProgress by animateFloatAsState(
                        targetValue = ratingReviewData.threeStarReviewCount / totalReviews,
                        animationSpec = tween(600),
                        label = "3Star"
                    )
                    val twoStarProgress by animateFloatAsState(
                        targetValue = ratingReviewData.twoStarReviewCount / totalReviews,
                        animationSpec = tween(600),
                        label = "2Star"
                    )
                    val oneStarProgress by animateFloatAsState(
                        targetValue = ratingReviewData.oneStarReviewCount / totalReviews,
                        animationSpec = tween(600),
                        label = "1Star"
                    )

                    RatingProgressBar(stars = 5, progress = fiveStarProgress)
                    RatingProgressBar(stars = 4, progress = fourStarProgress)
                    RatingProgressBar(stars = 3, progress = threeStarProgress)
                    RatingProgressBar(stars = 2, progress = twoStarProgress)
                    RatingProgressBar(stars = 1, progress = oneStarProgress)
                }
            }
        }

        if (isLoggedIn.value) {
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedButton(
                onClick = onWriteReviewClick,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFDBEAFE)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFFEFF6FF),
                    contentColor = Color(0xFF2563EB)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.RateReview,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Write a Review",
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun RatingProgressBar(stars: Int, progress: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = "$stars★",
            fontSize = 11.sp,
            fontFamily = Montserrat,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(20.dp)
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = Color(0xFFF59E0B),
            trackColor = Color(0xFFE2E8F0),
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun RatingStars(rating: Float) {
    val fullStars = rating.toInt().coerceIn(0, 5)
    val hasHalfStar = (rating % 1) >= 0.5f && fullStars < 5
    val emptyStars = (5 - fullStars - (if (hasHalfStar) 1 else 0)).coerceAtLeast(0)

    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(fullStars) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(15.dp)
            )
        }
        if (hasHalfStar) {
            Icon(
                imageVector = Icons.Filled.StarHalf,
                contentDescription = null,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(15.dp)
            )
        }
        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Outlined.StarBorder,
                contentDescription = null,
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

@Composable
fun CheckoutBottomBar(
    navController: NavHostController,
    isLoggedIn: MutableState<Boolean>,
    productData: ProductData,
    quantityState: MutableState<Int>,
    addToCart: () -> Unit,
    removeFromCart: () -> Unit,
    goToCart: () -> Unit,
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

    Surface(
        color = Color.White,
        shadowElevation = 12.dp,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Price Summary Tag
            Column {
                Text(
                    text = "Total Price",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    fontFamily = Montserrat
                )
                Text(
                    text = "$Ruppes${productData.productDiscountPrice}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A),
                    fontFamily = Montserrat
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Action Buttons
            if (quantityState.value > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Remove / Decrease Button
                    IconButton(
                        onClick = removeFromCart,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFEE2E2))
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Remove",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Go to Cart Main Button
                    Button(
                        onClick = goToCart,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .height(46.dp)
                            .width(160.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "View Cart (${quantityState.value})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Montserrat,
                            color = Color.White
                        )
                    }
                }
            } else {
                Button(
                    onClick = {
                        if (isLoggedIn.value) {
                            addToCart()
                        } else {
                            showLoginDialog.value = true
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add to Cart",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Montserrat,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewListContent(reviews: ReviewsData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Customer Reviews",
            fontWeight = FontWeight.Bold,
            fontFamily = Montserrat,
            fontSize = 18.sp,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (reviews.reviewList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No reviews yet. Be the first to review!",
                    color = Color(0xFF64748B),
                    fontFamily = Montserrat,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(reviews.reviewList.size) { index ->
                    ReviewItem(review = reviews.reviewList[index])
                }
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = review.user_name ?: "Verified Buyer",
                    fontSize = 13.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                RatingStars(rating = review.reviewRating.toFloatOrNull() ?: 5f)
            }

            if (review.reviewTitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = review.reviewTitle,
                    fontFamily = Montserrat,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF334155)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = review.reviewComment,
                fontFamily = Montserrat,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF64748B),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun WriteReviewContent(onSubmit: (Int, String, String) -> Unit) {
    var rating by remember { mutableIntStateOf(5) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Share Your Feedback",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Montserrat,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Interactive Star Selector
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = "$i Star",
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { rating = i }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        CustomOutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = "Review Headline",
            leadingIcon = Icons.AutoMirrored.Filled.Message,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = {
                Text(
                    text = "Write your review...",
                    fontFamily = Montserrat,
                    fontSize = 13.sp
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2563EB),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = Color(0xFFF8FAFC),
                unfocusedContainerColor = Color(0xFFF8FAFC)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            maxLines = 4,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
        )

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = errorMessage,
                color = Color(0xFFDC2626),
                fontSize = 12.sp,
                fontFamily = Montserrat,
                modifier = Modifier.align(Alignment.Start)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                when {
                    rating == 0 -> errorMessage = "Please select a star rating."
                    title.isBlank() -> errorMessage = "Please enter a review headline."
                    description.isBlank() -> errorMessage = "Please write a brief description."
                    else -> {
                        errorMessage = ""
                        onSubmit(rating, title, description)
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "Submit Review",
                fontFamily = Montserrat,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun ChangeStatusBarColor(color: Color, isDarkIcons: Boolean) {
    val activity = LocalContext.current as? Activity ?: return
    val window: Window = activity.window
    val insetsController = WindowInsetsControllerCompat(window, window.decorView)

    SideEffect {
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)
        insetsController.isAppearanceLightNavigationBars = true
        insetsController.isAppearanceLightStatusBars = isDarkIcons
    }
}