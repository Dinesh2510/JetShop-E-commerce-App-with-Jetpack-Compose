package com.app.ecomapp.presentation.screens.home

import android.app.Activity
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.auth.UserData
import com.app.ecomapp.data.models.details.ProductDetailsResponse
import com.app.ecomapp.data.models.details.ReviewsData
import com.app.ecomapp.data.models.home.ProductData
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator
import com.app.ecomapp.presentation.components.CustomOutlinedTextField
import com.app.ecomapp.presentation.components.LoginPromptDialog
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.wishlist.WishlistViewModel
import com.app.ecomapp.ui.theme.AppMainTypography
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.ui.theme.ProductTypography
import com.app.ecomapp.utils.Constants.Companion.Ruppes
import com.app.ecomapp.utils.UserDataStore
import com.app.ecomapp.utils.toWishlistProduct
import com.compose.jetshop.R


@Composable
fun ProductDetailScreen(
    navController: NavHostController,
    backStackEntry: NavBackStackEntry,
    productId:String,
    viewModel: HomeViewModel = hiltViewModel(),
    wishlistViewModel: WishlistViewModel = hiltViewModel(),
) {
  //  val productId = backStackEntry.arguments?.getString("product_id") ?: ""
    Log.d("ProductDetails", "Composable Launched with Product ID: $productId")

    val context = LocalContext.current
    val dataStoreHelper = remember { UserDataStore(context) } // Instantiate DataStoreHelper
    val user by dataStoreHelper.getUserData.collectAsState(initial = null)
    val productDetailsState by viewModel.productDetails.collectAsState()
    val isLoggedIn = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isLoggedIn.value = dataStoreHelper.isUserLoggedIn(context)
    }
    Log.d("TAG_productId", "ProductDetailScreen: $productId")
    ChangeStatusBarColor(Color(0xfff2f3f2), isDarkIcons = true) // Change to Purple

// Observe lifecycle events
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                Log.d("ProductDetails", "onDestroy() called")

            }

            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                Log.d("ProductDetails", "onCreate() called")
            }

            override fun onStart(owner: LifecycleOwner) {
                Log.d("ProductDetails", "onStart() called")
            }

            override fun onResume(owner: LifecycleOwner) {
                Log.d("ProductDetails", "onResume() called")
            }

            override fun onPause(owner: LifecycleOwner) {
                Log.d("ProductDetails", "onPause() called")
            }

            override fun onStop(owner: LifecycleOwner) {
                Log.d("ProductDetails", "onStop() called")
            }
        })
    }

    CallApiForProductDetails(user, dataStoreHelper, productId, viewModel)
    SideEffect {
        Log.d("ProductDetails", "UI Recomposition occurred")
    }

    when (productDetailsState) {
        is Resource.Success -> {
            val apiData = (productDetailsState as Resource.Success<ProductDetailsResponse?>).data
            Log.d("ProductDetails", "Product data loaded successfully")
            SetDataUI(apiData, navController, wishlistViewModel, viewModel, isLoggedIn)
        }

        is Resource.Error -> {
            val errorMessage = (productDetailsState as Resource.Error).message
            Log.e("ProductDetails", "Error loading product: $errorMessage")
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }

        is Resource.Loading -> {
            Log.d("ProductDetails", "Loading product details...")
            CenteredCircularProgressIndicator()
        }

        else -> {
            Log.w("ProductDetails", "Unexpected state encountered")
        }
    }

    // Cleanup when composable is removed
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
    // Collecting the addToCartResponse state from the ViewModel
    val addToCartResponse by viewModel.addToCartResponse.collectAsState()
    val removeToCartResponse by viewModel.removeToCartResponse.collectAsState()

    // First API Call when the user comes to this page (only once)
    LaunchedEffect(user?.userId ?: "") {
        if (user?.userId.isNullOrEmpty()) {
            // If user_id is null, fetch the user data from the data store
            dataStoreHelper.getUserData.collect { userData ->
                // Log the product details fetching
                Log.d("ProductDetails", "Fetching product details for Product ID: $productId")
                // Fetch product details using the user_id from the data store
                viewModel.getProductDetails(userData?.userId ?: "", productId)
            }
        } else {
            // If user_id is available, fetch the product details directly
            viewModel.getProductDetails(user?.userId ?: "", productId)
        }
    }

    LaunchedEffect(addToCartResponse, removeToCartResponse) {
        if (addToCartResponse is Resource.Success || removeToCartResponse is Resource.Success) {
            // Log when either add or remove is successful
            Log.d(
                "AddRemoveToCart",
                "Cart updated successfully, refetching product details for Product ID: $productId"
            )
            // Fetch the product details again after a successful add or remove response
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
    val productData = apiData!!.product
    val cartData = apiData.cartData
    val ratingReviewData = apiData.reviewsData
    var quantityState = remember { mutableStateOf(cartData?.quantity ?: 0) }

    // Bottom Sheet State for Review Listing
    val reviewSheetState = rememberModalBottomSheetState()
    var showReviewSheet by remember { mutableStateOf(false) }

    // Bottom Sheet State for Writing Review
    val writeReviewSheetState = rememberModalBottomSheetState()
    var showWriteReviewSheet by remember { mutableStateOf(false) }
    val addReviewResponse by viewModel.addReview.collectAsState()
    if (addReviewResponse is Resource.Success) {
        Toast.makeText(LocalContext.current, "Review added successfully", Toast.LENGTH_SHORT)
            .show()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 30.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            ProductHeader(productData, navController)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    ),
                horizontalAlignment = Alignment.Start,

                ) {
                ProductDetails(productData, quantityState, wishlistViewModel)
                Spacer(modifier = Modifier.height(20.dp))

                // Rating & Review Section
                ReviewSection(
                    isLoggedIn,
                    productData = productData,
                    ratingReviewData = ratingReviewData,
                    onViewAllReviewsClick = {
                        showReviewSheet = true
                    },  // ✅ Open Review Listing Bottom Sheet
                    onWriteReviewClick = {
                        showWriteReviewSheet = true
                    } // ✅ Open Write Review Bottom Sheet
                )

                Spacer(modifier = Modifier.height(100.dp))

                // Related Products Section
                // RelatedProductsSection()
            }
        }

        CheckoutButton(
            navController = navController,
            isLoggedIn = isLoggedIn,
            modifier = Modifier.align(Alignment.BottomCenter),
            quantityState = quantityState,
            addTocart = { viewModel.addToCart(productData.productId, "1") },
            removeFromCart = { viewModel.removeFromCart(productData.productId) },
            GoToCart = {
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.ProductDetails.route) { inclusive = false }
                    launchSingleTop = true
                    navController.popBackStack()
                }
            }
        )
    }

    // ✅ Bottom Sheet for Review Listing
    if (showReviewSheet) {
        ModalBottomSheet(
            onDismissRequest = { showReviewSheet = false },
            sheetState = reviewSheetState
        ) {
            ReviewListContent(ratingReviewData)
        }
    }

    // ✅ Bottom Sheet for Writing a Review
    if (showWriteReviewSheet) {
        ModalBottomSheet(
            onDismissRequest = { showWriteReviewSheet = false },
            sheetState = writeReviewSheetState
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
fun ReviewListContent(reviews: ReviewsData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Reviews", fontWeight = FontWeight.Bold,
            fontFamily = Montserrat, fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(reviews.reviewList.size) { review ->
                ReviewItem(reviews.reviewList[review])
            }
        }
    }
}

@Composable
fun ReviewItem(review: com.app.ecomapp.data.models.details.Review) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // User name with fallback for anonymous
            Text(
                text = review.user_name ?: "Anonymous",
                fontSize = 18.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Rating display with stars (can replace with a custom RatingBar if needed)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Rating:",
                    fontFamily = Montserrat,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                // Represent the rating visually (stars can be dynamic based on reviewRating)
                RatingStars(rating = review.reviewRating.toFloatOrNull() ?: 0.0f)
            }

            // Review title
            Text(
                text = review.reviewTitle,
                fontFamily = Montserrat,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Review comment
            Text(
                text = review.reviewComment,
                fontFamily = Montserrat,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Divider to separate reviews
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

// Custom composable for rating stars (for a more professional look)
@Composable
fun RatingStars(rating: Float) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        // Display full stars
        repeat(rating.toInt()) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Star",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Display half star if needed
        if (rating % 1 >= 0.5) {
            Icon(
                imageVector = Icons.Default.StarHalf,
                contentDescription = "Half Star",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        // Display empty stars
        repeat(5 - rating.toInt() - if (rating % 1 >= 0.5) 1 else 0) {
            Icon(
                imageVector = Icons.Default.StarBorder,
                contentDescription = "Empty Star",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}


@Composable
fun WriteReviewContent(onSubmit: (Int, String, String) -> Unit) {
    var rating by remember { mutableStateOf(0) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Write a Review",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Montserrat,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Star Rating
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = "$i Star",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { rating = i }
                )
            }
        }

        CustomOutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = "Title",
            leadingIcon = Icons.AutoMirrored.Filled.Message,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Description Input
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
        )

        // Error Message
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        Button(
            onClick = {
                // Validation
                if (rating == 0) {
                    errorMessage = "Rating is required."
                } else if (title.isEmpty()) {
                    errorMessage = "Title is required."
                } else if (description.isEmpty()) {
                    errorMessage = "Description is required."
                } else {
                    errorMessage = "" // Clear the error message
                    onSubmit(rating, title, description)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Submit Review")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Composable
fun ProductHeader(
    productData: ProductData,
    navController: NavHostController,
) {
    //curve for header card
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xfff2f3f2),
                //  shape = RoundedCornerShape(bottomStartPercent = 17, bottomEndPercent = 17)
            )
    ) {
        Spacer(modifier = Modifier.height(26.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, top = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .background(color = Color.White, shape = CircleShape)
                    .clip(CircleShape)

            ) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = null, tint = Color.Black)

            }
            Row(
                modifier = Modifier
                    .width(70.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(3.dp)
                    .clip(RoundedCornerShape(8.dp)),
                horizontalArrangement = Arrangement.spacedBy(
                    4.dp,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = productData.productRating,
                    style = AppMainTypography.subHeader,
                    color = Color.Black
                )
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFD700), // Align the icon to the center horizontally
                )
            }}

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = productData.productImageUrl,
                contentDescription = productData.productName,
                modifier = Modifier.size(400.dp, 250.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun ProductDetails(
    productData: ProductData, quantityState: MutableState<Int>,
    wishlistViewModel: WishlistViewModel,
) {
    var isInWishlist by remember { mutableStateOf(false) }

    LaunchedEffect(productData) {
        wishlistViewModel.isProductInWishlist(productData.productId) {
            isInWishlist = it
        }
    }
    val wishlistProduct = productData.toWishlistProduct()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = productData.productName,
                style = ProductTypography.prodTitleMedium,
                modifier = Modifier.weight(1f)
            )
            Column {
                IconButton(
                    onClick = {
                        if (isInWishlist) {
                            wishlistViewModel.removeFromWishlist(wishlistProduct)
                        } else {
                            wishlistViewModel.addToWishlist(wishlistProduct)
                        }
                        isInWishlist = !isInWishlist // Toggle UI state
                    }) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        imageVector = if (isInWishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isInWishlist) Color.Red else Color.Gray
                    )
                }
            }

        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = Ruppes + productData.productDiscountPrice,
            style = ProductTypography.prodPriceBold,
        )
        Text(
            text = Ruppes + productData.productDiscountPrice,
            style = ProductTypography.prodDiscountPrice,
        )
    }
    val loremText =
        LoremIpsum(50).values // Generates 5 paragraphs of Lorem Ipsum text
    Text(
        text = productData.productDescription.ifEmpty { loremText.first() },
        style = ProductTypography.prodDescription,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(16.dp)
    )

    /*Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        QuantitySelector(quantityState.value, onQuantityChanged = { quantityState.value = it })
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Rating",
                tint = Color(0xFFFFD700)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = productData.productRating,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }*/
}

@Composable
fun ReviewSection(
    isLoggedIn: MutableState<Boolean>,
    productData: ProductData,
    ratingReviewData: ReviewsData,
    onWriteReviewClick: () -> Unit,
    onViewAllReviewsClick: () -> Unit,
) {
    val totalReviews =
        ratingReviewData.totalReviewCount.toFloat().coerceAtLeast(1f) // Avoid division by zero

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onViewAllReviewsClick() },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Ratings & Reviews",
                style = AppMainTypography.subHeader,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Blue
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = productData.productRating,
                    fontSize = 50.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${ratingReviewData.totalReviewCount} reviews",
                    style = AppMainTypography.subHeader,
                )
            }
            Column {
                // Animated progress values
                val fiveStarProgress by animateFloatAsState(
                    targetValue = ratingReviewData.fiveStarReviewCount / totalReviews,
                    animationSpec = tween(1000)
                )
                val fourStarProgress by animateFloatAsState(
                    targetValue = ratingReviewData.fourStarReviewCount / totalReviews,
                    animationSpec = tween(1000)
                )
                val threeStarProgress by animateFloatAsState(
                    targetValue = ratingReviewData.threeStarReviewCount / totalReviews,
                    animationSpec = tween(1000)
                )
                val twoStarProgress by animateFloatAsState(
                    targetValue = ratingReviewData.twoStarReviewCount / totalReviews,
                    animationSpec = tween(1000)
                )
                val oneStarProgress by animateFloatAsState(
                    targetValue = ratingReviewData.oneStarReviewCount / totalReviews,
                    animationSpec = tween(1000)
                )

                // Progress bars
                RatingProgressBar(stars = 5, progress = fiveStarProgress)
                RatingProgressBar(stars = 4, progress = fourStarProgress)
                RatingProgressBar(stars = 3, progress = threeStarProgress)
                RatingProgressBar(stars = 2, progress = twoStarProgress)
                RatingProgressBar(stars = 1, progress = oneStarProgress)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (isLoggedIn.value) {
            Button(
                onClick = onWriteReviewClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Write a Review", fontFamily = Montserrat, fontWeight = FontWeight.SemiBold)
            }
        }

    }
}

@Composable
fun RatingProgressBar(stars: Int, progress: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("$stars★", fontSize = 14.sp, modifier = Modifier.width(24.dp), fontFamily = Montserrat)
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .width(180.dp)
                .height(8.dp),
        )
    }
}


@Composable
fun RelatedProductsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Related Products", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        LazyRow {
            items(10) { index -> // Mock Data
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color.Gray)
                        .padding(8.dp)
                ) {
                    Text("Product $index", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CheckoutButton(
    navController: NavHostController,
    isLoggedIn: MutableState<Boolean>,
    modifier: Modifier,
    quantityState: MutableState<Int>,
    addTocart: () -> Unit,
    removeFromCart: () -> Unit,
    GoToCart: () -> Unit,
) {
    val showLoginDialog = remember { mutableStateOf(false) }

// Show the login prompt when needed
    LoginPromptDialog(
        showDialog = showLoginDialog.value,
        onDismiss = { showLoginDialog.value = false },
        onLoginClick = {
            showLoginDialog.value = false
            navController.navigate(Screen.Login.route)
        }
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp).padding(WindowInsets.navigationBars.asPaddingValues()),
        contentAlignment = Alignment.Center
    ) {
        if (quantityState.value > 0) {
            // Show two buttons: "Remove from Cart" and "Cart"
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // "Remove from Cart" button
                Button(
                    onClick = {
                        removeFromCart()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(55.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove from Cart",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp)) // Spacer between buttons

                // "Cart" button
                Button(
                    onClick = {
                        GoToCart()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(55.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                ) {
                    Text(
                        "Cart",
                        fontSize = 18.sp,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            // Show "Add to Cart" button when quantity is 0
            Button(
                onClick = {
                    if (isLoggedIn.value) {
                        addTocart()
                    } else {
                        showLoginDialog.value = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
            ) {
                Text(
                    "Add to Cart",
                    fontSize = 18.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// Quantity Selector Component
@Composable
fun QuantitySelector(quantity: Int, onQuantityChanged: (Int) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { if (quantity > 1) onQuantityChanged(quantity - 1) },
            modifier = Modifier.border(
                shape = RoundedCornerShape(20),
                width = 2.dp,
                color = Color(0xFFB3B3B3)
            )
        ) {
            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease Quantity")
        }
        Text(
            text = quantity.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Montserrat,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        IconButton(
            onClick = { onQuantityChanged(quantity + 1) }, modifier = Modifier.border(
                shape = RoundedCornerShape(20),
                width = 2.dp,
                color = Color(0xFFB3B3B3)
            )
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase Quantity")
        }
    }
}


@Composable
fun ChangeStatusBarColor(color: Color, isDarkIcons: Boolean) {
    val activity = LocalContext.current as Activity
    val window: Window = activity.window
    val insetsController = WindowInsetsControllerCompat(window, window.decorView)

    SideEffect {
        /*  window.statusBarColor = android.graphics.Color.parseColor(color.toString())
          insetsController.isAppearanceLightStatusBars = isDarkIcons
  */
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)
        insetsController.isAppearanceLightNavigationBars = true
        insetsController.isAppearanceLightStatusBars = isDarkIcons


    }
}