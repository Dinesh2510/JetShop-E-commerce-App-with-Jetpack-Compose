package com.app.ecomapp.presentation.screens.home

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.home.BrandData
import com.app.ecomapp.data.models.home.CategoryData
import com.app.ecomapp.data.models.home.CategoryDetailsResponse
import com.app.ecomapp.data.models.home.HomeResponse
import com.app.ecomapp.data.models.home.ProductData
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator
import com.app.ecomapp.presentation.components.LoginPromptDialog
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.wishlist.WishlistViewModel
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.Constants.Companion.Ruppes
import com.app.ecomapp.utils.UserDataStore
import com.app.ecomapp.utils.toWishlistProduct

@Composable
fun CategoryBrandSplitScreen(
    navController: NavController,
    isCategoryScreen: Boolean = true,
    viewModel: HomeViewModel = hiltViewModel(),
    wishlistViewModel: WishlistViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val dataStoreHelper = remember { UserDataStore(context) }
    val isLoggedIn = remember { mutableStateOf(false) }
    val showLoginDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoggedIn.value = dataStoreHelper.isUserLoggedIn(context)
        viewModel.getSliderCategoryProducts()
    }

    val homeResponse by viewModel.homeResponse.collectAsState()
    val selectedId = remember { mutableStateOf<String?>(null) }

    LoginPromptDialog(
        showDialog = showLoginDialog.value,
        onDismiss = { showLoginDialog.value = false },
        onLoginClick = {
            showLoginDialog.value = false
            navController.navigate(Screen.Login.route)
        }
    )

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            ToolbarWithBackButtonAndTitle(
                title = if (isCategoryScreen) "Shop by Category" else "Shop by Brand",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .navigationBarsPadding()
        ) {
            when (homeResponse) {
                is Resource.Success -> {
                    val data = (homeResponse as Resource.Success<HomeResponse?>).data
                    val list = if (isCategoryScreen) data?.categories else data?.brandsData

                    LaunchedEffect(list) {
                        if (!list.isNullOrEmpty() && selectedId.value.isNullOrEmpty()) {
                            val firstId = when (val first = list.first()) {
                                is CategoryData -> first.id.orEmpty()
                                is BrandData -> first.brandId.orEmpty()
                                else -> ""
                            }
                            selectedId.value = firstId

                            if (isCategoryScreen) {
                                viewModel.getListProductsByCategory(firstId)
                            } else {
                                viewModel.getListProductsByBrand(firstId)
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxSize()) {
                        // LEFT SIDE: Side Navigation Rail
                        LazyColumn(
                            modifier = Modifier
                                .width(94.dp)
                                .fillMaxHeight()
                                .background(Color(0xFFF1F5F9))
                        ) {
                            items(list.orEmpty()) { item ->
                                val itemId = when (item) {
                                    is CategoryData -> item.id.orEmpty()
                                    is BrandData -> item.brandId.orEmpty()
                                    else -> ""
                                }

                                val itemName = when (item) {
                                    is CategoryData -> item.name ?: "Category"
                                    is BrandData -> item.brandName ?: "Brand"
                                    else -> ""
                                }

                                val itemLogo = when (item) {
                                    is CategoryData -> item.icon ?: ""
                                    is BrandData -> item.brandLogo ?: ""
                                    else -> ""
                                }

                                val isSelected = selectedId.value == itemId

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (isSelected) Color.White else Color.Transparent)
                                        .clickable {
                                            if (selectedId.value != itemId) {
                                                selectedId.value = itemId
                                                if (isCategoryScreen) {
                                                    viewModel.getListProductsByCategory(itemId)
                                                } else {
                                                    viewModel.getListProductsByBrand(itemId)
                                                }
                                            }
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Left Active Selection Indicator Bar
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .width(4.dp)
                                                .height(36.dp)
                                                .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                                                .background(Color(0xFF2563EB))
                                                .align(Alignment.CenterStart)
                                        )
                                    }

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = if (isSelected) Color(0xFFEFF6FF) else Color.White,
                                            border = BorderStroke(
                                                1.dp,
                                                if (isSelected) Color(0xFFBFDBFE) else Color(0xFFE2E8F0)
                                            ),
                                            shadowElevation = if (isSelected) 1.dp else 0.dp,
                                            modifier = Modifier.size(50.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(LocalContext.current)
                                                        .data(itemLogo)
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = itemName,
                                                    modifier = Modifier.size(28.dp),
                                                    contentScale = ContentScale.Fit
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = itemName,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontFamily = Montserrat,
                                            color = if (isSelected) Color(0xFF2563EB) else Color(0xFF475569),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.Center,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        }

                        // RIGHT SIDE: Product Grid Content
                        val productList by if (isCategoryScreen)
                            viewModel.listOfProductByCategory.collectAsState()
                        else
                            viewModel.listOfProductByBrand.collectAsState()

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(Color(0xFFF8FAFC))
                        ) {
                            when (productList) {
                                is Resource.Success -> {
                                    val productData =
                                        (productList as Resource.Success<CategoryDetailsResponse?>).data?.products

                                    if (!productData.isNullOrEmpty()) {
                                        LazyVerticalGrid(
                                            columns = GridCells.Fixed(2),
                                            contentPadding = PaddingValues(10.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            items(
                                                count = productData.size,
                                                key = { idx -> productData[idx].productId }
                                            ) { idx ->
                                                val product = productData[idx]
                                                var isInWishlist by remember { mutableStateOf(false) }

                                                LaunchedEffect(product.productId) {
                                                    wishlistViewModel.isProductInWishlist(product.productId) {
                                                        isInWishlist = it
                                                    }
                                                }

                                                ProductCardBrandCategory(
                                                    viewModel = viewModel,
                                                    product = product,
                                                    onAddToCart = { productId ->
                                                        if (isLoggedIn.value) {
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
                                                    onRemoveFromCart = { viewModel.removeFromCart(it) }
                                                )
                                            }
                                        }
                                    } else {
                                        EmptyStateView(
                                            title = "No Products",
                                            subtitle = "No products found for this section."
                                        )
                                    }
                                }

                                is Resource.Loading -> {
                                    CenteredCircularProgressIndicator()
                                }

                                is Resource.Error -> {
                                    ErrorStateView(
                                        errorMessage = "Failed to load products.",
                                        onRetry = {
                                            selectedId.value?.let { currentId ->
                                                if (isCategoryScreen) viewModel.getListProductsByCategory(currentId)
                                                else viewModel.getListProductsByBrand(currentId)
                                            }
                                        }
                                    )
                                }

                                else -> {}
                            }
                        }
                    }
                }

                is Resource.Loading -> {
                    CenteredCircularProgressIndicator()
                }

                is Resource.Error -> {
                    ErrorStateView(
                        errorMessage = "Failed to load categories/brands.",
                        onRetry = { viewModel.getSliderCategoryProducts() }
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
fun ProductCardBrandCategory(
    product: ProductData,
    viewModel: HomeViewModel,
    onAddToCart: (String) -> Unit,
    onRemoveFromCart: (String) -> Unit,
    onViewProduct: (ProductData) -> Unit,
    onWishlistToggle: () -> Unit,
    isInWishlist: Boolean,
) {
    val quantityMap by viewModel.cartQuantities.collectAsState()
    val productQuantity = quantityMap[product.productId]
        ?: product.user_cart_quantity?.toIntOrNull()
        ?: 0

    val origPrice = product.productPrice.toDoubleOrNull() ?: 0.0
    val discPrice = product.productDiscountPrice.toDoubleOrNull() ?: origPrice

    val discountPercent = if (origPrice > discPrice && origPrice > 0) {
        (((origPrice - discPrice) / origPrice) * 100).toInt()
    } else 0

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewProduct(product) }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.productImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.productName,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                )

                // Discount Badge
                if (discountPercent > 0) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFECFDF5),
                        border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(2.dp)
                    ) {
                        Text(
                            text = "$discountPercent% OFF",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669),
                            fontFamily = Montserrat,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                // Wishlist Icon Pill
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.92f),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(2.dp)
                        .size(28.dp)
                ) {
                    IconButton(onClick = onWishlistToggle) {
                        Icon(
                            imageVector = if (isInWishlist) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isInWishlist) Color(0xFFEF4444) else Color(0xFF94A3B8),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Product Name
            Text(
                text = product.productName,
                fontSize = 12.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(3.dp))

            // Pricing Row
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
                    fontSize = 13.sp
                )
                if (discountPercent > 0) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$Ruppes${product.productPrice}",
                        fontSize = 10.5.sp,
                        fontFamily = Montserrat,
                        textDecoration = TextDecoration.LineThrough,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Cart Action
            if (productQuantity > 0) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
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
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Text(
                            text = productQuantity.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Montserrat,
                            color = Color(0xFF1D4ED8)
                        )

                        IconButton(
                            onClick = {
                                viewModel.updateCartQuantity(product.productId, productQuantity + 1)
                                onAddToCart(product.productId)
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            } else {
                Button(
                    onClick = {
                        viewModel.updateCartQuantity(product.productId, productQuantity + 1)
                        onAddToCart(product.productId)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                ) {
                    Row(verticalAlignment = CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AddShoppingCart,
                            contentDescription = "Add",
                            modifier = Modifier.size(14.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add",
                            fontSize = 11.5.sp,
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
private fun EmptyStateView(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFF6FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Montserrat,
            color = Color(0xFF0F172A),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = subtitle,
            fontSize = 12.5.sp,
            fontFamily = Montserrat,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun ErrorStateView(errorMessage: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFFFEF2F2)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Something Went Wrong",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Montserrat,
            color = Color(0xFF0F172A),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = errorMessage,
            fontSize = 12.5.sp,
            fontFamily = Montserrat,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
            modifier = Modifier.height(38.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = "Retry",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Montserrat,
                color = Color.White
            )
        }
    }
}