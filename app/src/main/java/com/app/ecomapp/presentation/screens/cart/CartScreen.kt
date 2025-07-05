package com.app.ecomapp.presentation.screens.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.compose.jetshop.R
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.cart.CartResponse
import com.app.ecomapp.data.models.home.ProductData
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator

import com.app.ecomapp.presentation.components.LoginRequiredScreen
import com.app.ecomapp.presentation.components.Spacer_12dp
import com.app.ecomapp.presentation.components.Spacer_4dp
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.home.HomeViewModel
import com.app.ecomapp.ui.theme.AppMainTypography
import com.app.ecomapp.ui.theme.BlueDark
import com.app.ecomapp.ui.theme.DarkBlue
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.ui.theme.ProductTypography
import com.app.ecomapp.ui.theme.black
import com.app.ecomapp.utils.CommonFunction.LottieAnimationView
import com.app.ecomapp.utils.Constants.Companion.Ruppes
import com.app.ecomapp.utils.UserDataStore

@Composable
fun CartScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val dataStoreHelper = remember { UserDataStore(context) }
    val isLoggedIn = remember { mutableStateOf(false) }
    val cartResponse by viewModel.cartResponse.collectAsState()

    LaunchedEffect(Unit) {
        isLoggedIn.value = dataStoreHelper.isUserLoggedIn(context)
    }
    if (isLoggedIn.value) {
        Scaffold(
            topBar = { MyTopAppBar("My Cart") },
            bottomBar = { ButtonLayout(cartResponse, navController) }
        ) { padding ->
            when (cartResponse) {
                is Resource.Loading -> {
                    CenteredCircularProgressIndicator()
                }

                is Resource.Success -> {
                    val apiData = (cartResponse as Resource.Success).data
                    if (apiData?.cartItems?.isNotEmpty() == true) {
                        LazyColumn(modifier = Modifier.padding(padding)) {
                            items(apiData.cartItems.size) { index ->
                                CartItemRow(
                                    apiData.cartItems[index],
                                    onRemoveFromCart = { prodId -> viewModel.removeFromCart(prodId) },
                                    onAddToCart = { prodId -> viewModel.addToCart(prodId, "1") },
                                    onDeleteCart = { cartID -> viewModel.deleteFromCart(cartID) },
                                )
                            }
                        }
                    } else {
                        LottieAnimationView(
                            animationResId = R.raw.empty_cart,
                            text = "Your Cart is Empty",
                        )

                    }
                }

                is Resource.Error -> {
                    LottieAnimationView(
                        animationResId = R.raw.empty_cart,
                        text =  (cartResponse as Resource.Error).message ?: "Something went wrong",)

                }
            }
        }
    } else {
        LoginRequiredScreen(navController)
    }

}

@Composable
fun CenteredCircularProgressIndicator() {
    TODO("Not yet implemented")
}

// Cart Item Row
@Composable
fun CartItemRow(
    product: ProductData,
    onAddToCart: (productId: String) -> Unit,
    onRemoveFromCart: (productId: String) -> Unit,
    onDeleteCart: (cartId: String) -> Unit
) {
    var quantity by remember { mutableIntStateOf((product.quantity).toInt()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors =  CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ Product Image
            Image(
                painter = rememberImagePainter(data = product.productImageUrl),
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer_12dp()

            // ✅ Product Details
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = product.productName,
                    color = black,
                    style = ProductTypography.prodTitleMedium,
                    maxLines = 2,
                )
                Spacer_4dp()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Star Icon for rating
                    Icon(
                        imageVector = Icons.Filled.Star, // Star icon
                        contentDescription = "Rating Star",
                        tint = Color(0xFFFFD700), // Golden color for the star
                        modifier = Modifier.size(15.dp)
                    )

                    // Product rating text
                    Text(
                        text = product.productRating,
                        style = ProductTypography.prodRating,
                        color = DarkBlue,
                        modifier = Modifier.padding(start = 4.dp) // Add some space between the star and the rating text
                    )
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "Rs.${product.productDiscountPrice}",
                        style = ProductTypography.prodPriceBold,
                        color = DarkBlue,
                    )
                    Text(
                        text = "Rs.${product.productPrice}",
                        style = ProductTypography.prodDiscountPrice,
                        color = DarkBlue,
                    )
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // ✅ Quantity Controls (Above)
                    if (quantity > 0) {
                        Row(
                            modifier = Modifier
                                .height(36.dp)
                                .background(
                                    Color(0xffF8A44C).copy(alpha = 0.2f), RoundedCornerShape(6.dp)
                                )
                                .border(1.dp, Color(0xFF00C853), RoundedCornerShape(6.dp)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // ➖ Decrease Quantity
                            IconButton(
                                onClick = {
                                    if (quantity > 1) {
                                        quantity--
                                        onRemoveFromCart(product.productId)
                                    } else {
                                        quantity = 0
                                        onRemoveFromCart(product.productId) // Remove from cart
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Decrease",
                                    tint = Color(0xFF00C853)
                                )
                            }

                            // 🔢 Quantity Text
                            Text(
                                text = quantity.toString(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Montserrat,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            // ➕ Increase Quantity
                            IconButton(
                                onClick = {
                                    quantity++
                                    onAddToCart(product.productId)
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Increase",
                                    tint = Color(0xFF00C853)
                                )
                            }
                        }
                    }

                    Spacer_4dp()

                    // 🗑️ Remove Item Button (Below)
                    IconButton(
                        onClick = { onDeleteCart(product.cart_id) },
                        modifier = Modifier.size(25.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.delete), // your custom icon
                            contentDescription = "Remove",
                            tint = Color.Red // keep original icon color
                        )
                    }

                }
            }



        }
    }
}



@Composable
fun MyTopAppBar(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        // Title centered
        Text(
            text = title,
            style = AppMainTypography.subHeader,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )


    }
}

@Composable
fun ButtonLayout(cartResponse: Resource<CartResponse?>, navController: NavController) {
    var totalPrice by remember { mutableStateOf("") }

    if (cartResponse is Resource.Success) {
        val apiData = cartResponse.data
        totalPrice = apiData?.totalCartPrice ?: "0" // Calculate total price

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp) // Adjust padding as necessary
        ) {
            // Left Button with border and price text
            Button(
                onClick = {  },
                modifier = Modifier
                    .weight(1f) // Equal weight for both buttons
                    .border(2.dp, BlueDark, RoundedCornerShape(0.dp)) // Blue border
                    .padding(0.dp)
                    .heightIn(min = 48.dp), // Set a minimum height for equal height buttons
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent) // Transparent background
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = Ruppes +totalPrice, // Total price text
                        style = AppMainTypography.labelLarge,
                        color = Color.Black
                    )
                }
            }


            // Right Button with full background color
            Button(
                onClick = {navController.navigate(Screen.CheckoutScreen.route) },
                modifier = Modifier
                    .weight(1f) // Equal weight for both buttons
                    .padding(0.dp)
                    .heightIn(min = 48.dp), // Set a minimum height for equal height buttons
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BlueDark) // Primary color background
            ) {
                Text(
                    text = "Checkout", // Continue text
                    style = AppMainTypography.labelLarge,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


/* // ✅ Call API only once when screen is composed
LaunchedEffect(Unit) {
    viewModel.getMyCartList() // 🔥 Call API when screen loads
}
HandleApiState(apiState = viewModel.cartResponse,
    showLoader = false,
    navController = navController,
    onSuccess = { data ->
        apiData = data // ✅ Store the received data
        Log.d("CartScreen", "Data received: $data")
    }) {

    // ✅ Get the latest API state
    val cartResponse = viewModel.cartResponse.collectAsState().value

    Scaffold(topBar = {
        MyTopAppBar("My Cart")
    }) { padding ->
        when {
            // ✅ Show Loader while API is loading
            cartResponse is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // ✅ Show Cart Data if Available
            apiData != null && apiData!!.cartItems.isNotEmpty() -> {
                LazyColumn(modifier = Modifier.padding(padding)) {
                    items(apiData!!.cartItems.size) { index ->
                        CartItemRow(apiData!!.cartItems[index])
                    }
                }
            }

            // ✅ Show Empty State if No Data or API Error
            else -> {
                EmptyView("Your cart is empty", R.drawable.empty_cart)
            }
        }
    }

}
}*/