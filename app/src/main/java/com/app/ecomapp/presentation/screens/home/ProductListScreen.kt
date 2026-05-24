package com.app.ecomapp.presentation.screens.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.home.HomeResponse
import com.app.ecomapp.data.models.home.ProductData
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator
import com.app.ecomapp.presentation.components.LoginPromptDialog
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.wishlist.WishlistViewModel
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.HandleApiState
import com.app.ecomapp.utils.UserDataStore
import com.app.ecomapp.utils.toWishlistProduct



import androidx.compose.animation.Animatable
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
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

import com.app.ecomapp.data.models.home.SliderImageData
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.ui.theme.BackgroundContent
import com.app.ecomapp.ui.theme.BlueDark
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
    var apiData by remember { mutableStateOf<HomeResponse?>(null) } // ✅ Store success data

    LaunchedEffect(Unit) {
        viewModel.getSliderCategoryProducts() // Trigger to fetch category products
        isLoggedIn.value = dataStoreHelper.isUserLoggedIn(context)
    }
    // ✅ API handle Banner ,Brand, Category, Product
    HandleApiState(apiState = viewModel.homeResponse, // ✅ Pass the API state
        showLoader = false, // ✅ Enable/disable loader
        navController = navController, onSuccess = { data ->
            apiData = data
        }){}
    LoginPromptDialog(
        showDialog = showLoginDialog.value,
        onDismiss = { showLoginDialog.value = false },
        onLoginClick = {
            showLoginDialog.value = false
            navController.navigate(Screen.Login.route)
        }
    )
    val productData = viewModel.productList.collectAsLazyPagingItems()

    Scaffold(topBar = {
        ToolbarWithBackButtonAndTitle(title = "Products", onBackClick = {navController.popBackStack()})
    }) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) { // Fix: Remove padding here
            if (productData.loadState.refresh is LoadState.Loading) {
                CenteredCircularProgressIndicator()
            } else if (productData.loadState.refresh is LoadState.Error) {
                val error = (productData.loadState.refresh as LoadState.Error).error
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Error: ${error.localizedMessage}", color = Color.Red, fontFamily = Montserrat)
                        Spacer_8dp()
                        Button(onClick = { productData.refresh() }) {
                            Text("Retry",fontFamily = Montserrat)
                        }
                    }
                }
            } else {
                val quantityMap by
                viewModel.cartQuantities.collectAsState()
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues) // Fix: Apply padding only here
                ) {
                    // ✅ Banner Section
                    item(
                        span = {
                            GridItemSpan(2)
                        }
                    ) {

                        apiData?.sliderImageData?.let { images ->

                            if (images.isNotEmpty()) {

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp)
                                ) {
                                    AutoSlidingBannerHori(
                                        images
                                    )
                                }
                            }
                        }
                    }


                    items(productData.itemCount) { index ->

                        val product =
                            productData[index]

                        product?.let {

                            var isInWishlist by remember {
                                mutableStateOf(false)
                            }

                            LaunchedEffect(
                                product.productId
                            ) {

                                wishlistViewModel
                                    .isProductInWishlist(
                                        product.productId
                                    ) {

                                        isInWishlist = it
                                    }
                            }

                            val wishlistProduct =
                                product.toWishlistProduct()

                            val productQuantity =

                                quantityMap[
                                    product.productId
                                ]

                                    ?: product.user_cart_quantity
                                        ?.toInt()

                                    ?: 0

                            ProductCard(

                                viewModel = viewModel,

                                product = product,

                                productQuantity =
                                    productQuantity,

                                onAddToCart = { productId ->

                                    if (isLoggedIn.value) {

                                        viewModel.updateCartQuantity(

                                            productId,

                                            productQuantity + 1
                                        )

                                        viewModel.addToCart(
                                            productId,
                                            "1"
                                        )

                                    } else {

                                        showLoginDialog.value = true
                                    }
                                },

                                onViewProduct = {
                                        selectedProduct ->

                                    navController.navigate(

                                        "${Screen.ProductDetails.route}/" +
                                                selectedProduct.productId
                                    )
                                },

                                onWishlistToggle = {

                                    if (isInWishlist) {

                                        wishlistViewModel
                                            .removeFromWishlist(
                                                wishlistProduct
                                            )

                                    } else {

                                        wishlistViewModel
                                            .addToWishlist(
                                                wishlistProduct
                                            )
                                    }

                                    isInWishlist =
                                        !isInWishlist
                                },

                                isInWishlist =
                                    isInWishlist,

                                onRemoveFromCart = {
                                        productId ->

                                    viewModel.removeFromCart(
                                        productId
                                    )
                                }
                            )
                        }
                    }

                    // Bottom loader when paginating
                    if (productData.loadState.append is LoadState.Loading) {
                        item(span = { GridItemSpan(2) }) {
                            CenteredCircularProgressIndicator()
                        }
                    }

                    // Pagination error handling
                    if (productData.loadState.append is LoadState.Error) {
                        item(span = { GridItemSpan(2) }) {
                            val error = (productData.loadState.append as LoadState.Error).error
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Error: ${error.localizedMessage}",
                                        color = Color.Red,fontFamily = Montserrat
                                    )
                                    Spacer_8dp()
                                    Button(onClick = { productData.retry() }) {
                                        Text("Retry",fontFamily = Montserrat)
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
fun ProductCardPage(
    product: ProductData,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = product.productImageUrl,
                contentDescription = product.productName,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer_8dp()

            Column(modifier = Modifier.weight(1f)) {
                Text(product.productName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("₹${product.productDiscountPrice}", color = Color.Red, fontSize = 14.sp)
                Text("Stock: ${product.productStockQuantity}", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
@Composable
fun AutoSlidingBannerHori(
    sliderImageData: List<SliderImageData>?
) {
    val featuredBanners =
        sliderImageData?.filter {
            it.isActive == 1
        } ?: emptyList()

    if (featuredBanners.isEmpty()) return

    val pagerState = rememberPagerState(
        pageCount = {
            featuredBanners.size
        }
    )

    val coroutineScope =
        rememberCoroutineScope()

    LaunchedEffect(Unit) {

        while (true) {

            delay(4000)

            coroutineScope.launch {

                val nextPage =
                    (pagerState.currentPage + 1) %
                            pagerState.pageCount

                pagerState.animateScrollToPage(
                    nextPage,
                    animationSpec = tween(800)
                )
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) { page ->

            BannerImageHor(
                featuredBanners[page].imageUrl
            )
        }

        Spacer_8dp()

        DotsIndicatorHori(
            totalDots = featuredBanners.size,
            selectedIndex = pagerState.currentPage,
            dotSize = 8.dp
        )
    }
}
@Composable
fun BannerImageHor(
    imageUrl: String
) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .padding(horizontal = 8.dp),

        shape = RoundedCornerShape(28.dp),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),

        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )

    ) {

        Box {

            /* =========================
               IMAGE
            ========================= */

            AsyncImage(

                model = ImageRequest.Builder(
                    LocalContext.current
                )
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),

                contentDescription = null,

                modifier = Modifier.fillMaxSize(),

                contentScale = ContentScale.Crop
            )

            /* =========================
               PREMIUM OVERLAY
            ========================= */

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(

                        Brush.verticalGradient(

                            colors = listOf(

                                Color.Transparent,

                                Color.Black.copy(
                                    alpha = 0.12f
                                ),

                                Color.Black.copy(
                                    alpha = 0.55f
                                )
                            ),

                            startY = 100f
                        )
                    )
            )

            /* =========================
               GLOW BORDER
            ========================= */

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(

                        width = 1.dp,

                        brush = Brush.linearGradient(

                            colors = listOf(

                                Color.White.copy(
                                    alpha = 0.25f
                                ),

                                Color.Transparent,

                                Color.White.copy(
                                    alpha = 0.10f
                                )
                            )
                        ),

                        shape = RoundedCornerShape(28.dp)
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
