package com.app.ecomapp.presentation.screens.home

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
import com.app.ecomapp.data.models.home.ProductData
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator
import com.app.ecomapp.presentation.components.LoginPromptDialog
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.wishlist.WishlistViewModel
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.UserDataStore
import com.app.ecomapp.utils.toWishlistProduct

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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues) // Fix: Apply padding only here
                ) {
                    items(productData.itemCount) { index ->
                        val product = productData[index]
                        product?.let {
                            var isInWishlist by remember { mutableStateOf(false) }

                            LaunchedEffect(product) {
                                wishlistViewModel.isProductInWishlist(product.productId) {
                                    isInWishlist = it
                                }
                            }
                            val wishlistProduct = product.toWishlistProduct()

                            ProductCard(
                                viewModel = viewModel,
                                product = it,
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
                                    isInWishlist = !isInWishlist
                                },
                                isInWishlist = isInWishlist,
                                onRemoveFromCart = { productId ->
                                    viewModel.removeFromCart(productId)
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

