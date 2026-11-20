package com.app.ecomapp.presentation.screens.home

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.ecomapp.data.models.home.HomeResponse
import com.app.ecomapp.data.models.home.SliderImageData
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator
import com.app.ecomapp.presentation.components.LoginPromptDialog
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.wishlist.WishlistViewModel
import com.app.ecomapp.ui.theme.BlueDark
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.HandleApiState
import com.app.ecomapp.utils.UserDataStore
import com.app.ecomapp.utils.toWishlistProduct
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProductListScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    wishlistViewModel: WishlistViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val dataStoreHelper = remember { UserDataStore(context) }
    val isLoggedIn = remember { mutableStateOf(false) }
    val showLoginDialog = remember { mutableStateOf(false) }
    var apiData by remember { mutableStateOf<HomeResponse?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getSliderCategoryProducts()
        isLoggedIn.value = dataStoreHelper.isUserLoggedIn(context)
    }

    HandleApiState(
        apiState = viewModel.homeResponse,
        showLoader = false,
        navController = navController,
        onRetry = { viewModel.getSliderCategoryProducts() },   // the function that loads this screen's data
        onSuccess = { data -> apiData = data }
    ) {}

    LoginPromptDialog(
        showDialog = showLoginDialog.value,
        onDismiss = { showLoginDialog.value = false },
        onLoginClick = {
            showLoginDialog.value = false
            navController.navigate(Screen.Login.route)
        }
    )

    val productData = viewModel.productList.collectAsLazyPagingItems()
    val quantityMap by viewModel.cartQuantities.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            ToolbarWithBackButtonAndTitle(
                title = "All Products",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .navigationBarsPadding()
        ) {
            when {
                productData.loadState.refresh is LoadState.Loading -> {
                    CenteredCircularProgressIndicator()
                }

                productData.loadState.refresh is LoadState.Error -> {
                    val error = (productData.loadState.refresh as LoadState.Error).error
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFFEF2F2),
                                    modifier = Modifier.size(60.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.ErrorOutline,
                                            contentDescription = null,
                                            tint = Color(0xFFDC2626),
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "Failed to load products",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Montserrat,
                                    color = Color(0xFF0F172A)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = error.localizedMessage ?: "Something went wrong. Please check your internet connection.",
                                    fontSize = 12.sp,
                                    fontFamily = Montserrat,
                                    color = Color(0xFF64748B),
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                Button(
                                    onClick = { productData.refresh() },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .height(44.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Retry",
                                        fontSize = 13.sp,
                                        fontFamily = Montserrat,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Header Horizontal Banner Slider
                        item(span = { GridItemSpan(2) }) {
                            apiData?.sliderImageData?.let { images ->
                                val activeBanners = images.filter { it.isActive == 1 }
                                if (activeBanners.isNotEmpty()) {
                                    AutoSlidingBannerHori(activeBanners)
                                }
                            }
                        }

                        // Product Cards
                        items(
                            count = productData.itemCount,
                            key = { index -> productData[index]?.productId ?: index }
                        ) { index ->
                            val product = productData[index]

                            product?.let { currentProduct ->
                                var isInWishlist by remember { mutableStateOf(false) }

                                LaunchedEffect(currentProduct.productId) {
                                    wishlistViewModel.isProductInWishlist(currentProduct.productId) {
                                        isInWishlist = it
                                    }
                                }

                                val wishlistProduct = currentProduct.toWishlistProduct()
                                val productQuantity = quantityMap[currentProduct.productId]
                                    ?: currentProduct.user_cart_quantity?.toIntOrNull()
                                    ?: 0

                                ProductCard(
                                    viewModel = viewModel,
                                    product = currentProduct,
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

                        // Bottom Pagination Loader
                        if (productData.loadState.append is LoadState.Loading) {
                            item(span = { GridItemSpan(2) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF2563EB),
                                        strokeWidth = 2.5.dp,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        // Bottom Pagination Error
                        if (productData.loadState.append is LoadState.Error) {
                            item(span = { GridItemSpan(2) }) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Couldn't load more products",
                                            fontSize = 12.sp,
                                            fontFamily = Montserrat,
                                            color = Color(0xFFDC2626),
                                            fontWeight = FontWeight.Medium
                                        )

                                        Button(
                                            onClick = { productData.retry() },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Text(
                                                text = "Retry",
                                                fontSize = 12.sp,
                                                fontFamily = Montserrat,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
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
    }
}

@Composable
fun AutoSlidingBannerHori(
    sliderImageData: List<SliderImageData>?
) {
    val featuredBanners = sliderImageData?.filter { it.isActive == 1 } ?: emptyList()
    if (featuredBanners.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { featuredBanners.size })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            coroutineScope.launch {
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(nextPage, animationSpec = tween(800))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) { page ->
            BannerImageHor(featuredBanners[page].imageUrl)
        }

        Spacer(modifier = Modifier.height(8.dp))

        DotsIndicatorHori(
            totalDots = featuredBanners.size,
            selectedIndex = pagerState.currentPage,
            dotSize = 8.dp
        )
    }
}

@Composable
fun BannerImageHor(imageUrl: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                                Color.Black.copy(alpha = 0.08f),
                                Color.Black.copy(alpha = 0.40f)
                            ),
                            startY = 60f
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
                        shape = RoundedCornerShape(18.dp)
                    )
            )
        }
    }
}

@Composable
fun DotsIndicatorHori(
    modifier: Modifier = Modifier,
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color = Color(0xFF2563EB),
    unSelectedColor: Color = Color(0xFFCBD5E1),
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

            Dot(size = size, color = color.value, shape = RoundedCornerShape(16.dp))
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
            .height(6.dp)
            .width(size)
            .clip(shape)
            .background(color)
    )
}