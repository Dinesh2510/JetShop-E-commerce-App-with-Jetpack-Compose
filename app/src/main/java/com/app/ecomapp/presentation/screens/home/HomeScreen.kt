package com.app.ecomapp.presentation.screens.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.home.CategoryData
import com.app.ecomapp.data.models.home.HomeResponse
import com.app.ecomapp.data.models.home.SliderImageData
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.ui.theme.BackgroundContent
import com.app.ecomapp.ui.theme.BlueDark
import com.app.ecomapp.ui.theme.DarkBlue
import com.app.ecomapp.ui.theme.MainWhiteColor
import com.app.ecomapp.utils.HandleApiState
import com.app.ecomapp.utils.UserDataStore
import kotlinx.coroutines.delay

import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import com.app.ecomapp.data.models.home.BrandData
import com.app.ecomapp.data.models.home.CategoryDetailsResponse
import com.app.ecomapp.data.models.home.ProductData
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator
import com.app.ecomapp.presentation.components.LoginPromptDialog
import com.app.ecomapp.presentation.components.Spacer_12dp
import com.app.ecomapp.presentation.components.Spacer_4dp
import com.app.ecomapp.presentation.components.TitleSmall
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.auth.AuthViewModel
import com.app.ecomapp.presentation.screens.wishlist.WishlistViewModel
import com.app.ecomapp.ui.theme.AppMainTypography
import com.app.ecomapp.ui.theme.ProductTypography
import com.app.ecomapp.utils.NetworkImage
import com.app.ecomapp.utils.toWishlistProduct
import com.compose.jetshop.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    wishlistViewModel: WishlistViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val dataStoreHelper = remember { UserDataStore(context) }
    val isLoggedIn = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isLoggedIn.value = dataStoreHelper.isUserLoggedIn(context)
    }

    // ✅ Get use name
    val userName by viewModel.userName.collectAsState()
    val userIsPrimeActive by viewModel.userIsPrimeActive.collectAsState()
    val userId by viewModel.userId.collectAsState()
    val email by viewModel.userEmail.collectAsState()
    val contact by viewModel.userPhone.collectAsState()
    var apiData by remember { mutableStateOf<HomeResponse?>(null) } // ✅ Store success data
    val currentApiData by rememberUpdatedState(apiData)

  /*
  SplashScreen Data u can get by this below way
   LaunchedEffect(Unit) {
        val appInfo = dataStoreHelper.getAppInfoObject()
        val emuail = appInfo?.supportEmail
        val phone = appInfo?.contactNumber
        Log.d("AppInfo", "Email: $emuail | Phone: $phone")
    }*/
    // ✅ Use LaunchedEffect for triggering actions when screen is visited
    LaunchedEffect(navController.currentBackStackEntry) {
        // When entering HomeScreen, reset cart and fetch slider category products
        viewModel.resetCart()
        viewModel.getSliderCategoryProducts() // Trigger to fetch category products
    }

    // ✅ API handle Banner ,Brand, Category, Product
    HandleApiState(apiState = viewModel.homeResponse, // ✅ Pass the API state
        showLoader = false, // ✅ Enable/disable loader
        navController = navController, onSuccess = { data ->
            apiData = data
        }) {
        val homeResponse = viewModel.homeResponse.collectAsState().value // ✅ Get API state
        // ✅ Show Loader if API is in loading state
        if (homeResponse is Resource.Loading) {
            CenteredCircularProgressIndicator()
        } else {
            Scaffold(
                topBar = {
                    userName?.let {
                        MyTopAppBar(
                            it,
                            userIsPrimeActive,
                            onSearchClick = { navController.navigate(Screen.SearchScreen.route) },
                            onClickPrime = {

                                val intent =
                                    Intent(context, PrimePayScreen::class.java).apply {
                                        putExtra("user_id", userId)
                                        putExtra("appName", "JetShop")
                                        putExtra("email", email)
                                        putExtra("contact", contact)
                                    }
                                // context.startActivity(intent)
                                context.startActivity(intent)
                            },
                        )
                    }
                },
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MainWhiteColor)
                        .padding(
                            top = padding.calculateTopPadding(),
                            start = padding.calculateStartPadding(LayoutDirection.Ltr),
                            end = padding.calculateEndPadding(LayoutDirection.Ltr),
                            bottom = 0.dp // Remove bottom padding
                        ), verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CoroutineScope(Dispatchers.IO).launch {
                        authViewModel.sendUserFCM()
                    }
                    // ✅ Banner Section
                    item {
                        currentApiData?.sliderImageData?.let { images ->
                            if (images.isNotEmpty()) {
                                AutoSlidingBanner(images.take(5))
                            }
                        }
                    }

                    // ✅ Categories Listing
                    item {
                        AskForPermission()
                        currentApiData?.categories?.let { categories ->
                            CategoriesListing(
                                navController,
                                categories
                            ) {
                                navController.navigate(Screen.CategoryBrandSplit.createRoute("category"))
                                //   navController.navigate(Screen.CategoryGridScreen.route + "/category")
                            }
                        }
                    }
                    // ✅ Brand Listing
                    item {
                        currentApiData?.brandsData?.let { brand ->
                            BrandListing(navController, brand) {
                                //navController.navigate(Screen.CategoryGridScreen.route + "/brand")
                                navController.navigate(Screen.CategoryBrandSplit.createRoute("brand"))

                            }
                        }
                    }

                    // ✅ Product Section
                    currentApiData?.sectionData?.forEach { section ->
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    section.sectionName,
                                    style = AppMainTypography.subHeader
                                )
                                Text("see all",
                                    style = AppMainTypography.seeAllText,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.clickable {
                                        navController.navigate(Screen.AllProductList.route)
                                    })
                            }
                        }
                        item {
                            ProductListing(
                                section.productData,
                                viewModel,
                                wishlistViewModel,
                                isLoggedIn,
                                navController
                            )
                        }
                    }
                }
            }
        }
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
    val userId by viewModel.userId.collectAsState()

    Log.d("TAG_userId", "ProductListing: " + userId)
// Show the login prompt when needed
    LoginPromptDialog(showDialog = showLoginDialog.value,
        onDismiss = { showLoginDialog.value = false },
        onLoginClick = {
            showLoginDialog.value = false
            navController.navigate(Screen.Login.route)
        })
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        contentPadding = PaddingValues(horizontal = 0.dp)
    ) {
        productData.size?.let { productIndex ->
            items(productIndex) { product ->
                var isInWishlist by remember { mutableStateOf(false) }

                LaunchedEffect(product) {
                    wishlistViewModel.isProductInWishlist(productData[product].productId) {
                        isInWishlist = it
                    }
                }
                val wishlistProduct = productData[product].toWishlistProduct()

                ProductCard(viewModel = viewModel,
                    product = productData[product],
                    onAddToCart = { productId ->
                        if (isLoggedIn.value) {
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
                        isInWishlist = !isInWishlist // Toggle UI state
                    },
                    isInWishlist = isInWishlist,
                    onRemoveFromCart = { produdtId ->
                        viewModel.removeFromCart(produdtId)
                    })
            }


        }
    }
}


@Composable
fun ProductCard(
    product: ProductData,
    viewModel: HomeViewModel,
    onAddToCart: (String) -> Unit,
    onRemoveFromCart: (String) -> Unit,
    onViewProduct: (ProductData) -> Unit,
    onWishlistToggle: () -> Unit,
    isInWishlist: Boolean,
) {
    // Quantity state initialized with API value
    val quantity by viewModel.cartQuantities.collectAsState()
    val productQuantity = quantity[product.productId] ?: product.user_cart_quantity?.toInt() ?: 0


    Card(elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E2E2)),
        modifier = Modifier
            .width(180.dp)
            .padding(8.dp)
            .clickable { onViewProduct(product) }) {
        Column(
            modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = product.productImageUrl,
                    contentDescription = product.productName,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = onWishlistToggle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(Color.White, CircleShape)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isInWishlist) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (isInWishlist) Color.Red else Color.Gray
                    )
                }
            }

            Spacer_8dp()

            // Product Name
            Text(
                text = product.productName,
                style = ProductTypography.prodTitleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer_12dp()

            // Price and Discount
            Row(
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "₹${product.productDiscountPrice}",
                    color = MaterialTheme.colorScheme.primary,
                    style = ProductTypography.prodPriceBold,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = "₹${product.productPrice}",
                    style = ProductTypography.prodDiscountPrice,
                    color = Color.Gray)
            }

            Spacer_8dp()

            // Quantity Selector UI
            if (productQuantity > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp)
                        .border(1.dp, Color(0xFF00C853), RoundedCornerShape(6.dp)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = CenterVertically
                ) {
                    // Decrease (-) Button
                    IconButton(onClick = {
                        viewModel.updateCartQuantity(product.productId, productQuantity - 1)
                        onRemoveFromCart(product.productId)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease",
                            tint = Color(0xFF00C853)
                        )
                    }

                    // Quantity Text
                    Text(
                        text = productQuantity.toString(),
                        style = ProductTypography.productQuantity,
                    )

                    // Increase (+) Button
                    IconButton(onClick = {
                        viewModel.updateCartQuantity(product.productId, productQuantity + 1)
                        onAddToCart(product.productId)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = Color(0xFF00C853)
                        )
                    }
                }
            } else {
                // Add to Cart Button
                Button(
                    onClick = {
                        viewModel.updateCartQuantity(product.productId, productQuantity + 1)
                        onAddToCart(product.productId)
                    },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00C853), // Green
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = "Add to Cart",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer_4dp()
                    //Text(text = "Add to Cart", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Category",
            style = AppMainTypography.subHeader,
        )
        Text("see all",
            style = AppMainTypography.seeAllText,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                navigateToCategories()
            })
    }
    Spacer_8dp()
    LazyRow(
        modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(8.dp)
    ) {

        items(count = categories?.size ?: 0,
            key = { index -> categories!![index].id }, // Unique key for each item
            contentType = { index -> categories!![index].name } // Optional content type
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Brands",
            style = AppMainTypography.subHeader,
        )
        Text("see all",
            style = AppMainTypography.seeAllText,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                navigateToBrand()
            })
    }
    Spacer_8dp()
    LazyRow(
        modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(8.dp)
    ) {

        items(count = brandData?.size ?: 0,
            key = { index -> brandData!![index].brandId }, // Unique key for each item
            contentType = { index -> brandData!![index].brandName } // Optional content type
        ) { index ->
            CommonCircleItem(item = ItemType.Brand(brandData!![index])) {
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
            .padding(0.dp)
            .width(72.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = Color(0xFFE0E0E0),
                    shape = CircleShape
                )
                .background(Color(0xFFF6F6F6)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer_8dp()

        Text(
            text = label,
            style = ProductTypography.labelSmall,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}


sealed class ItemType {
    data class Brand(val brandData: BrandData) : ItemType()
    data class Category(val categoryData: CategoryData) : ItemType()
}

@Composable
fun AutoSlidingBanner(sliderImageData: List<SliderImageData>?) {
    if (sliderImageData.isNullOrEmpty()) return // ✅ Exit if null or empty

    val pagerState = rememberPagerState(pageCount = { sliderImageData.size })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000) // Delay before auto-slide
            coroutineScope.launch {
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(
                    nextPage, animationSpec = tween(800)
                ) // Smooth scroll
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer_8dp() // Space between pager & dots
        HorizontalPager(state = pagerState) { page ->
            BannerImage(sliderImageData[page].imageUrl)
        }

        Spacer_8dp() // Space between pager & dots

        sliderImageData.size.let {
            DotsIndicator(
                totalDots = it, selectedIndex = pagerState.currentPage, dotSize = 8.dp
            )
        }
    }
}


@Composable
fun BannerImage(it: String) {
    Box(modifier = Modifier.padding(horizontal = 8.dp)) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = BackgroundContent, // Set your card background color
                contentColor = Color.White // Set text or icon color inside the card
            ),
            modifier = Modifier
                .height(190.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
    }
}


@Composable
fun DotsIndicator(
    modifier: Modifier = Modifier,
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unSelectedColor: Color = BlueDark,
    dotSize: Dp = 8.dp,
) {

    Row(
        modifier = modifier,
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        for (index in 0 until totalDots) {

            val color by remember(selectedIndex) { derivedStateOf { Animatable(unSelectedColor) } }
            val size by animateDpAsState(if (index == selectedIndex) 20.dp else dotSize)


            LaunchedEffect(selectedIndex) {


                color.animateTo(
                    if (index == selectedIndex) selectedColor else unSelectedColor,
                    animationSpec = tween(300)
                )

            }

            Row(
                modifier = Modifier,
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Dot(size = size, color = color.value, shape = RoundedCornerShape(16.dp))
            }
        }
    }


}

@Composable
private fun Dot(
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    size: Dp = 8.dp,
    color: Color,
) {
    Box(
        modifier = modifier
            .padding(horizontal = 3.dp)
            .height(8.dp)
            .width(size)
            .clip(shape)
            .background(color)
    )
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

    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Greeting
            AnimatedGreeting(name, true)

            // Right side: Prime or Not + Search
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (userIsPrimeActive == "1") {
                    // ✅ Use TooltipBox from Material3
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip { Text("You're a Prime Member!",style = AppMainTypography.seeAllText,) }
                        },
                        state = tooltipState
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.premium),
                            contentDescription = "Prime Member",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    scope.launch {
                                        tooltipState.show()
                                    }
                                }
                        )
                    }
                } else {
                    // ✅ Show upgrade button
                    ShimmerPrimeBadge { onClickPrime() }
                }

                Spacer_12dp()

                IconButton(onClick = onSearchClick) {
                    Icon(
                        modifier = Modifier
                            .size(55.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MainWhiteColor)
                            .padding(8.dp),
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = DarkBlue
                    )
                }
            }
        }

    }
}


@Composable
fun ShimmerPrimeBadge(onClick: () -> Unit) {
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.6f),
        Color.White.copy(alpha = 0.9f),
        Color.White.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val shimmerBrush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = 0f)
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF03A9F4)) // Solid sky blue background
            .clickable {
                Log.d("ShimmerPrimeBadge", "✅ Prime clicked")
                onClick()
            }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = "Prime",
            style = TextStyle(
                brush = shimmerBrush,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedGreeting(name: String, moveUp: Boolean) {
    val greetings = listOf(
        "Hi, $name", "JetShop"
    )

    var currentIndex by remember { mutableStateOf(0) }

    // Determine slide direction
    val slideDirection = if (moveUp) {
        AnimatedContentTransitionScope.SlideDirection.Up
    } else {
        AnimatedContentTransitionScope.SlideDirection.Down
    }

    // Start the animation loop
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000) // Show the greeting for a while before changing
            currentIndex = (currentIndex + 1) % greetings.size
        }
    }

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(painter = rememberAsyncImagePainter(ImageRequest.Builder(LocalContext.current)
            .data(data = R.drawable.logo_trans).apply { crossfade(true) }
            .build()),
            modifier = Modifier
                .clip(CircleShape)
                .size(35.dp),
            contentDescription = null,
            contentScale = ContentScale.Crop)

        Spacer_8dp()
        val animationTime = 800

        AnimatedContent(
            targetState = currentIndex, transitionSpec = {
                slideIntoContainer(slideDirection, tween(animationTime)) + fadeIn(
                    animationSpec = tween(animationTime)
                ) togetherWith slideOutOfContainer(slideDirection, tween(animationTime)) + fadeOut(
                    animationSpec = tween(animationTime)
                )
            }, label = ""
        ) { index ->
            Text(
                greetings[index],
                style = AppMainTypography.sectionHeader,
            )
        }
    }
}

/*
* https://github.com/pedrotlf/JetpackComposeAnimationSample?tab=readme-ov-file*/
@Composable
fun AnimatedGreeting(name: String) {
    val greetings = listOf(
        "Hi, $name", "JetShop"
    )

    var currentIndex by remember { mutableStateOf(0) }
    val offsetY = remember { androidx.compose.animation.core.Animatable(0f) }

    // Start the animation
    LaunchedEffect(Unit) {
        while (true) {
            // Animate up
            offsetY.animateTo(-50f, animationSpec = tween(durationMillis = 500))
            currentIndex = (currentIndex + 1) % greetings.size

            // Animate down
            offsetY.animateTo(0f, animationSpec = tween(durationMillis = 500))
            delay(5000) // Show the greeting for a while before changing
        }
    }

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.offset(y = offsetY.value.dp) // Convert Float to Dp
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(data = R.drawable.logo_trans)
                    .apply(block = fun ImageRequest.Builder.() {
                        crossfade(true)
                    }).build()
            ),
            modifier = Modifier
                .clip(CircleShape)
                .size(35.dp),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Spacer_8dp()
        TitleSmall(text = greetings[currentIndex])
    }
}

@Composable
fun AskForPermission() {
    val context = LocalContext.current

    // State to manage dialog visibility
    val showDialog = remember { mutableStateOf(false) }
    val permissionDeniedMessage = remember { mutableStateOf("") }

    // Permission request launcher for location and notification permissions
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions(),
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
            })

    // Check if permissions are already granted
    val locationPermissionStatus = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    )

    val notificationPermissionStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
    } else {
        PackageManager.PERMISSION_GRANTED // Don't need this permission on older versions
    }

    // Request permissions if not already granted
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

    // Show dialog if permissions are denied
    if (showDialog.value) {
        PermissionDeniedDialog(message = permissionDeniedMessage.value,
            onDismiss = { showDialog.value = false })
    }
}

@Composable
fun PermissionDeniedDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(modifier = Modifier.padding(20.dp), onDismissRequest = { onDismiss() }, title = {
        Text(
            text = "Permission Denied", style = AppMainTypography.appTitle,
        )
    }, text = {
        Text(
            text = message, style = AppMainTypography.bodyText
        )
    }, confirmButton = {
        Text(text = "OK",
            style = AppMainTypography.caption,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(15.dp)
                .clickable { onDismiss() })
    })
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CategoryGridScreen(
    navController: NavController,
    isCategoryScreen: Boolean = true, // true for categories, false for brands
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val homeResponse by viewModel.homeResponse.collectAsState()
    Log.d("HomeScreen", "CategoryGridScreen: $homeResponse")

    LaunchedEffect(Unit) {
        viewModel.getSliderCategoryProducts()
        Log.d("CategoryGridScreen", "HomeResponse: $homeResponse")
    }
    Scaffold(topBar = {
        ToolbarWithBackButtonAndTitle(
            title = if (isCategoryScreen) "Shop CategoryWise" else "Shop BrandWise",
            onBackClick = { navController.popBackStack() })
    }) { paddingValues ->
        when (homeResponse) {
            is Resource.Success -> {
                val data = (homeResponse as Resource.Success<HomeResponse?>)?.data
                val list = if (isCategoryScreen) data?.categories else data?.brandsData

                list?.let { items ->
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = paddingValues
                    ) {
                        items(items.size) { index ->
                            val item = items[index]

                            val (routeType, intentId) = when (item) {
                                is CategoryData -> "category" to item.id
                                is BrandData -> "brand" to item.brandId
                                else -> null to null
                            }

                            if (routeType != null && intentId != null) {
                                CategoryCard(item) {
                                    navController.navigate(Screen.CategoryBrandDetails.route + "/$routeType/$intentId")
                                }
                            }
                        }

                    }
                }

            }

            is Resource.Loading -> {
                CenteredCircularProgressIndicator()
            }

            is Resource.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Failed to load data",style = AppMainTypography.bodyText)
                }
            }

            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No data available",style = AppMainTypography.bodyText)
                }
            }
        }
    }
}

@ExperimentalCoilApi
@Composable
fun CategoryCard(item: Any, onClick: () -> Unit) {

    val icon = if (item is CategoryData) item.icon else (item as BrandData).brandLogo
    val name = if (item is CategoryData) item.name else (item as BrandData).brandName

    val backgroundColor =
        if (item is CategoryData) Color(0xffD3B0E0).copy(.25f) else Color(0xff53b175).copy(.1f)
    val borderColor =
        if (item is CategoryData) Color(0xffD3B0E0).copy(1f) else Color(0xff53b175).copy(0.7f)

    Card(
        onClick = { onClick() },
        shape = RoundedCornerShape(8),
        colors = CardDefaults.cardColors(backgroundColor),
        modifier = Modifier
            .height(190.dp)
            .padding(8.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            NetworkImage(
                url = icon,
                modifier = Modifier
                    .height(75.dp)
                    .width(112.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.fillMaxHeight(0.2f))
            Text(
                text = name, softWrap = true,
                textAlign = TextAlign.Center,
                style = ProductTypography.categoryName,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}


@Composable
fun CategoryBrandDetails(
    navController: NavController,
    type: String,
    id: String,
    viewModel: HomeViewModel = hiltViewModel(),
    wishlistViewModel: WishlistViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val dataStoreHelper = remember { UserDataStore(context) }
    val isLoggedIn = remember { mutableStateOf(false) }
    val showLoginDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoggedIn.value = dataStoreHelper.isUserLoggedIn(context)
    }

    LoginPromptDialog(
        showDialog = showLoginDialog.value,
        onDismiss = { showLoginDialog.value = false },
        onLoginClick = {
            showLoginDialog.value = false
            navController.navigate(Screen.Login.route)
        }
    )

    val isCategoryScreen = type == "category"
    val homeResponse by if (isCategoryScreen)
        viewModel.listOfProductByCategory.collectAsState()
    else
        viewModel.listOfProductByBrand.collectAsState()

    LaunchedEffect(id) {
        if (isCategoryScreen) {
            viewModel.getListProductsByCategory(id)
        } else {
            viewModel.getListProductsByBrand(id)
        }
    }

    Scaffold(topBar = {
        ToolbarWithBackButtonAndTitle(
            title = if (isCategoryScreen) "Shop CategoryWise" else "Shop BrandWise"
        , onBackClick = {navController.popBackStack()})
    }) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (homeResponse) {
                is Resource.Success -> {
                    val productData =
                        (homeResponse as Resource.Success<CategoryDetailsResponse?>)?.data?.products

                    if (!productData.isNullOrEmpty()) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(productData.size) { product ->
                                var isInWishlist by remember { mutableStateOf(false) }

                                LaunchedEffect(productData[product].productId) {
                                    wishlistViewModel.isProductInWishlist(productData[product].productId) {
                                        isInWishlist = it
                                    }
                                }

                                ProductCard(
                                    viewModel = viewModel,
                                    product = productData[product],
                                    onAddToCart = { productId ->
                                        if (isLoggedIn.value) {
                                            viewModel.addToCart(productId, "1")
                                        } else {
                                            showLoginDialog.value = true
                                        }
                                    },
                                    onViewProduct = {
                                        navController.navigate("${Screen.ProductDetails.route}/${productData[product].productId}")
                                    },
                                    onWishlistToggle = {
                                        if (isInWishlist) {
                                            wishlistViewModel.removeFromWishlist(productData[product].toWishlistProduct())
                                        } else {
                                            wishlistViewModel.addToWishlist(productData[product].toWishlistProduct())
                                        }
                                        isInWishlist = !isInWishlist
                                    },
                                    isInWishlist = isInWishlist,
                                    onRemoveFromCart = { viewModel.removeFromCart(it) }
                                )
                            }
                        }
                    } else {
                        Text("No products available", modifier = Modifier.align(Alignment.Center),style = AppMainTypography.bodyText)
                    }
                }

                is Resource.Loading -> {
                    CenteredCircularProgressIndicator()
                }

                is Resource.Error -> {
                    Text("Failed to load data", modifier = Modifier.align(Alignment.Center),style = AppMainTypography.bodyText)
                }
            }
        }
    }
}

