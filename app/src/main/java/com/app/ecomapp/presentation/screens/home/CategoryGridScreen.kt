package com.app.ecomapp.presentation.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.home.BrandData
import com.app.ecomapp.data.models.home.CategoryData
import com.app.ecomapp.data.models.home.CategoryDetailsResponse
import com.app.ecomapp.data.models.home.HomeResponse
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator
import com.app.ecomapp.presentation.components.LoginPromptDialog
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.wishlist.WishlistViewModel
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.NetworkImage
import com.app.ecomapp.utils.UserDataStore
import com.app.ecomapp.utils.toWishlistProduct

@OptIn(ExperimentalCoilApi::class)
@Composable
fun CategoryGridScreen(
    navController: NavController,
    isCategoryScreen: Boolean = true,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val homeResponse by viewModel.homeResponse.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getSliderCategoryProducts()
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            ToolbarWithBackButtonAndTitle(
                title = if (isCategoryScreen) "Shop by Category" else "Shop by Brand",
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
            when (homeResponse) {
                is Resource.Success -> {
                    val data = (homeResponse as Resource.Success<HomeResponse?>).data
                    val list = if (isCategoryScreen) data?.categories else data?.brandsData

                    if (!list.isNullOrEmpty()) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                count = list.size,
                                key = { index ->
                                    when (val item = list[index]) {
                                        is CategoryData -> "cat_${item.id}"
                                        is BrandData -> "brand_${item.brandId}"
                                        else -> index
                                    }
                                }
                            ) { index ->
                                val item = list[index]
                                val (routeType, intentId) = when (item) {
                                    is CategoryData -> "category" to item.id
                                    is BrandData -> "brand" to item.brandId
                                    else -> null to null
                                }

                                if (routeType != null && intentId != null) {
                                    CategoryCard(item = item) {
                                        navController.navigate("${Screen.CategoryBrandDetails.route}/$routeType/$intentId")
                                    }
                                }
                            }
                        }
                    } else {
                        EmptyStateView(
                            title = if (isCategoryScreen) "No Categories Found" else "No Brands Found",
                            subtitle = "Please check back later or refresh the page."
                        )
                    }
                }

                is Resource.Loading -> {
                    CenteredCircularProgressIndicator()
                }

                is Resource.Error -> {
                    ErrorStateView(
                        errorMessage = "Unable to load data at this time.",
                        onRetry = { viewModel.getSliderCategoryProducts() }
                    )
                }

                else -> {
                    EmptyStateView(
                        title = "No Data Available",
                        subtitle = "There are currently no items to display."
                    )
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

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp, pressedElevation = 4.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .height(145.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Circular container with subtle contrast border
            Surface(
                shape = CircleShape,
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFEFF2F6)),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    NetworkImage(
                        url = icon,
                        modifier = Modifier.size(36.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = name.orEmpty(),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp,
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

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            ToolbarWithBackButtonAndTitle(
                title = if (isCategoryScreen) "Category Products" else "Brand Products",
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
            when (homeResponse) {
                is Resource.Success -> {
                    val productData =
                        (homeResponse as Resource.Success<CategoryDetailsResponse?>).data?.products

                    if (!productData.isNullOrEmpty()) {
                        val quantityMap by viewModel.cartQuantities.collectAsState()

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
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
                                    onViewProduct = {
                                        navController.navigate("${Screen.ProductDetails.route}/${product.productId}")
                                    },
                                    onWishlistToggle = {
                                        if (isInWishlist) {
                                            wishlistViewModel.removeFromWishlist(product.toWishlistProduct())
                                        } else {
                                            wishlistViewModel.addToWishlist(product.toWishlistProduct())
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
                    } else {
                        EmptyStateView(
                            title = "No Products Found",
                            subtitle = "There are no products listed in this collection yet."
                        )
                    }
                }

                is Resource.Loading -> {
                    CenteredCircularProgressIndicator()
                }

                is Resource.Error -> {
                    ErrorStateView(
                        errorMessage = "Failed to load products. Please try again.",
                        onRetry = {
                            if (isCategoryScreen) {
                                viewModel.getListProductsByCategory(id)
                            } else {
                                viewModel.getListProductsByBrand(id)
                            }
                        }
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun EmptyStateView(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFF6FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Montserrat,
            color = Color(0xFF0F172A),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = subtitle,
            fontSize = 13.sp,
            fontFamily = Montserrat,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun ErrorStateView(errorMessage: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFFFEF2F2)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Something Went Wrong",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Montserrat,
            color = Color(0xFF0F172A),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = errorMessage,
            fontSize = 13.sp,
            fontFamily = Montserrat,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Retry",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Montserrat,
                color = Color.White
            )
        }
    }
}