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
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.AppLogger
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.app.ecomapp.presentation.screens.payment.PaymentScreen
import com.app.ecomapp.presentation.screens.wishlist.WishlistViewModel
import com.app.ecomapp.utils.NetworkImage
import com.app.ecomapp.utils.toWishlistProduct
import com.compose.jetshop.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.net.URLEncoder

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
        topBar = {
            ToolbarWithBackButtonAndTitle(
                title = if (isCategoryScreen) "Shop CategoryWise" else "Shop BrandWise",
                onBackClick = { navController.popBackStack() })
        }
    ) { padding ->
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

                Row(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    // LEFT: Category/Brand List
                    LazyColumn(
                        modifier = Modifier
                            .width(90.dp)
                            .fillMaxHeight()
                            .background(Color.White)
                    ) {
                        items(list.orEmpty()) { item ->
                            val itemId = when (item) {
                                is CategoryData -> item.id.orEmpty()
                                is BrandData -> item.brandId.orEmpty()
                                else -> ""
                            }

                            val itemName = when (item) {
                                is CategoryData -> item.name ?: "Unknown Category"
                                is BrandData -> item.brandName ?: "Unknown Brand"
                                else -> "Unknown"
                            }

                            val itemLogo = when (item) {
                                is CategoryData -> item.icon ?: ""
                                is BrandData -> item.brandLogo ?: ""
                                else -> ""
                            }

                            val isSelected = selectedId.value == itemId

                            val bgColor = if (isSelected)
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xffF7A593).copy(alpha = 0.1f),
                                        Color(0xffF8A44C).copy(alpha = 0.7f)
                                    )
                                )
                            else
                                Brush.verticalGradient(
                                    listOf(Color.White, Color.White)
                                )

                            Card(
                                onClick = {
                                    if (selectedId.value != itemId) {
                                        selectedId.value = itemId
                                        if (isCategoryScreen) {
                                            viewModel.getListProductsByCategory(itemId)
                                        } else {
                                            viewModel.getListProductsByBrand(itemId)
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = if (isSelected) BorderStroke(
                                    1.5.dp,
                                    Color(0xffF8A44C)
                                ) else null,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .fillMaxWidth()
                                    .height(80.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(bgColor)
                                        .padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    if (itemLogo.isNotEmpty()) {
                                        AsyncImage(
                                            model = itemLogo,
                                            contentDescription = itemName,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Spacer_4dp()
                                    Text(
                                        text = itemName,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                        fontFamily = Montserrat,
                                        color = Color.Black,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }


                    Spacer_8dp()

                    // RIGHT: Product List
                    val productList by if (isCategoryScreen)
                        viewModel.listOfProductByCategory.collectAsState()
                    else
                        viewModel.listOfProductByBrand.collectAsState()

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    ) {
                        when (productList) {
                            is Resource.Success -> {
                                val productData =
                                    (productList as Resource.Success<CategoryDetailsResponse?>).data?.products
                                if (!productData.isNullOrEmpty()) {
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(2),
                                        contentPadding = PaddingValues(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(productData.size) { product ->
                                            var isInWishlist by remember { mutableStateOf(false) }

                                            LaunchedEffect(productData[product]) {
                                                wishlistViewModel.isProductInWishlist(productData[product].productId) {
                                                    isInWishlist = it
                                                }
                                            }

                                            ProductCardBrandCategory(
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
                                                        wishlistViewModel.removeFromWishlist(
                                                            productData[product].toWishlistProduct()
                                                        )
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
                                    Text(
                                        "No products",
                                        fontFamily = Montserrat,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }

                            is Resource.Loading -> {
                                CenteredCircularProgressIndicator()                            }

                            is Resource.Error -> {
                                Text(
                                    "Error loading products",
                                    fontFamily = Montserrat,
                                    modifier = Modifier.align(Alignment.Center)
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
                Text(
                    "Error loading categories/brands",
                    fontFamily = Montserrat,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            }

            else -> {}
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
    // Quantity state initialized with API value
    val quantity by viewModel.cartQuantities.collectAsState()
    val productQuantity = quantity[product.productId] ?: product.user_cart_quantity?.toInt() ?: 0


    Card(elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E2E2)),
        modifier = Modifier
            .width(180.dp)
            .padding(0.dp)
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
                fontWeight = FontWeight.Bold,
                fontFamily = Montserrat,
                maxLines = 1,
                fontSize = 12.sp,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer_4dp()

            // Price and Discount
            Row(
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "₹${product.productPrice}",
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Montserrat,
                    fontSize = 12.sp,
                    textDecoration = TextDecoration.LineThrough,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = "₹${product.productDiscountPrice}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Montserrat,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer_8dp()

            // Quantity Selector UI
            if (productQuantity > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
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
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
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
/*

val list = listOf(
    Category(
        R.drawable.veges,
        Color(0xff53b175).copy(alpha = 0.1f), "Fresh Fruits & Vegetables",
        Color(0xff53b175).copy(alpha = 0.7f)
    ),
    Category(
        R.drawable.oil,
        Color(0xffF8A44C).copy(.1f), "Cooking Oil & Ghee",
        Color(0xffF8A44C).copy(.7f)
    ),
    Category(
        R.drawable.fish,
        Color(0xffF7A593).copy(.25f),
        "Meat & Fish",
        Color(0xffF7A593).copy(1f)
    ),
    Category(
        R.drawable.snacks,
        Color(0xffD3B0E0).copy(.25f),
        "Bakery & Snacks",
        Color(0xffD3B0E0).copy(1f)
    ),
    Category(
        R.drawable.eggs,
        Color(0xffFDE598).copy(.25f),
        "Dairy & Eggs",
        Color(0xffFDE598).copy(1f)
    ),
    Category(
        R.drawable.beverages,
        Color(0xffB7DFF5).copy(.25f),
        "Beverages",
        Color(0xffB7DFF5).copy(1f)
    ),
    Category(
        R.drawable.veges,
        Color(0xff53b175).copy(alpha = 0.1f), "Fresh Fruits & Vegetables",
        Color(0xff53b175).copy(alpha = 0.7f)
    ),
    Category(
        R.drawable.oil,
        Color(0xffF8A44C).copy(.1f), "Cooking Oil & Ghee",
        Color(0xffF8A44C).copy(.7f)
    ),
    Category(
        R.drawable.fish,
        Color(0xffF7A593).copy(.25f),
        "Meat & Fish",
        Color(0xffF7A593).copy(1f)
    )
)*/
