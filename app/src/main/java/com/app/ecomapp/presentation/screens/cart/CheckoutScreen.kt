package com.app.ecomapp.presentation.screens.cart

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.checkout.CartItem
import com.app.ecomapp.data.models.checkout.OrderDetailsResponse
import com.app.ecomapp.data.models.placeOrder.CartItemJson
import com.app.ecomapp.data.models.placeOrder.OrderRequest
import com.app.ecomapp.data.models.placeOrder.Product
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator

import com.app.ecomapp.presentation.components.LoadingButton
import com.app.ecomapp.presentation.components.Spacer_4dp
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.order.OrderViewModel
import com.app.ecomapp.ui.theme.AppMainTypography
import com.app.ecomapp.ui.theme.DarkBlue
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.ui.theme.ProductTypography
import com.app.ecomapp.ui.theme.black
import com.app.ecomapp.utils.CommonFunction.LottieAnimationView
import com.app.ecomapp.utils.Constants.Companion.Ruppes
import com.app.ecomapp.utils.IncludeApp
import com.app.ecomapp.utils.UserDataStore
import com.compose.jetshop.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CheckoutScreen(
    navController: NavHostController,
    cartViewModel: CartViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel(),
) {
    var context = LocalContext.current
    val orderState by cartViewModel.orderDetails.collectAsState()
    var couponCode by remember { mutableStateOf("") }
    var appliedCoupon by remember { mutableStateOf<String?>(null) }
    var isCouponApplied by remember { mutableStateOf(false) } // To track if coupon is applied
    val dataStoreHelper = remember { UserDataStore(context) }
    val userId by dataStoreHelper.getValue(UserDataStore.USER_ID).collectAsState(initial = "")
    val products = remember { mutableStateListOf<Product>() }
    var isLoading = orderViewModel.isLoading
    val isLoadingPlaceOrder = remember { mutableStateOf(false) }

    val paymentMethods = listOf(
        PaymentMethod("COD", "COD", R.drawable.cod),
        PaymentMethod("RAZORPAY", "RazorPay", R.drawable.razor_pay_icon),
    )

    var selectedPaymentMethod by remember { mutableStateOf(paymentMethods.first().id) }

    // Update products list when order details change
    LaunchedEffect(orderState) {
        when (orderState) {
            is Resource.Loading -> {
                isLoadingPlaceOrder.value = true
            }
            is Resource.Success -> {
                isLoadingPlaceOrder.value = false // Stop loading
                val orderDetails = (orderState as Resource.Success<OrderDetailsResponse>).data
                orderDetails?.let { details ->
                    products.clear()
                    products.addAll(details.cartItems.map { cartItem ->
                        Product(
                            product_id = cartItem.productId,
                            quantity = cartItem.quantity.toString(),
                            quantity_amount_price = cartItem.quantityAmountPrice,
                            tax_amount = cartItem.taxAmount,
                            quantity_amount_price_with_tax = cartItem.quantityAmountPriceWithTax
                        )
                    })

                    // Handle coupon applied status
                    if (orderDetails.couponApplied == 1) {
                        Toast.makeText(context, "Coupon applied successfully!", Toast.LENGTH_SHORT)
                            .show()
                        appliedCoupon = couponCode
                        isCouponApplied = true
                    } else if (orderDetails.couponApplied == 0) {
                        isCouponApplied = false
                    }
                }
            }
            is Resource.Error -> {
                isLoadingPlaceOrder.value = false // Stop loading on error
                Toast.makeText(context, (orderState as Resource.Error).message, Toast.LENGTH_SHORT).show()
            }

            null -> {}
        }
    }

    LaunchedEffect(Unit) {
        cartViewModel.fetchOrderDetails("")  // Initial fetch with empty coupon code
    }

    // IGNORE THIS :-Observe orderState changes and show a toast message based on coupon applied status
    LaunchedEffect(orderState) {
        if (orderState is Resource.Success) {
            val orderDetails = (orderState as Resource.Success<OrderDetailsResponse>).data
            if (orderDetails?.couponApplied == 1) {
                // Coupon was applied successfully
                Toast.makeText(context, "Coupon applied successfully!", Toast.LENGTH_SHORT).show()
                appliedCoupon = couponCode
                isCouponApplied = true
            } else if (orderDetails?.couponApplied == 0) {
                // Failed to apply coupon
                //Toast.makeText(context, "Failed to apply coupon.", Toast.LENGTH_SHORT).show()
                isCouponApplied = false
            }
        }
    }

    Scaffold(
        topBar = {
            ToolbarWithBackButtonAndTitle(
                title = "Checkout Details",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when (orderState) {
                is Resource.Loading -> {
                    item {
                        CenteredCircularProgressIndicator()
                    }
                }

                is Resource.Success -> {
                    val orderDetails = (orderState as Resource.Success<OrderDetailsResponse>).data

                    // Cart Items here get all Product Listing
                    if (orderDetails?.cartItems?.isNotEmpty() == true) {
                        items(orderDetails.cartItems.size) { index ->
                            OrderRow(orderDetails.cartItems[index])
                        }
                    } else {
                        item {
                            LottieAnimationView(animationResId = R.raw.empty_cart, text = "Your cart is empty")
                        }
                    }

                    // Address Section
                    item {
                        Spacer(Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Delivery Address",
                                    style = AppMainTypography.subHeader,
                                )
                                Spacer(Modifier.height(8.dp))
                                if (orderDetails.shippingAddress != null) {
                                    Text(
                                        text = buildAnnotatedString {
                                            append("${orderDetails.shippingAddress.address}, ")
                                            append("${orderDetails.shippingAddress.city}, ")
                                            append("${orderDetails.shippingAddress.state}, ")
                                            append("${orderDetails.shippingAddress.country} - ")
                                            append(orderDetails.shippingAddress.zipCode)
                                            append("\n${orderDetails.shippingAddress.googleAddress}")
                                        },
                                        style = AppMainTypography.bodyText,
                                    )
                                } else {
                                    Text(
                                        text = "Please add address to checkout...",
                                        style = AppMainTypography.bodyText,
                                        color = Color.Gray
                                    )
                                }

                                Log.d(
                                    "CheckOutScreen",
                                    "OrderScreen: " + orderDetails.shippingAddress
                                )
                                Spacer_8dp()
                                Text(
                                    "📍 Change Address",
                                    style = AppMainTypography.bodyText,
                                    modifier = Modifier.clickable { navController.navigate(Screen.Address.route)},
                                    color = Color.Blue, fontFamily = Montserrat
                                )
                            }
                        }
                    }

                    // Coupon Code Section
                    /* item {
                         Spacer(Modifier.height(16.dp))
                         Card(
                             modifier = Modifier.fillMaxWidth(),
                             elevation = CardDefaults.cardElevation(8.dp)
                         ) {
                             Column(modifier = Modifier.padding(16.dp)) {
                                 Row(
                                     modifier = Modifier.fillMaxWidth(),
                                     verticalAlignment = Alignment.CenterVertically
                                 ) {
                                     OutlinedTextField(
                                         value = couponCode,
                                         onValueChange = { couponCode = it },
                                         label = {
                                             Text(
                                                 "Enter Coupon Code",
                                                 fontFamily = Montserrat
                                             )
                                         },
                                         modifier = Modifier.weight(1f)
                                     )
                                     Spacer(modifier = Modifier.width(8.dp))
                                     Button(onClick = {
                                         // Call API only when the coupon code is different from the previous one
                                         if (couponCode.isNotEmpty() && couponCode != appliedCoupon) {
                                             cartViewModel.fetchOrderDetails(couponCode) // Fetch with the entered coupon code
                                         }
                                     }) {
                                         Text("Apply", fontFamily = Montserrat)
                                     }
                                 }
                                 appliedCoupon?.let {
                                     Text(
                                         "Applied Coupon: $it ✅",
                                         color = Color.Black,
                                         fontFamily = Montserrat
                                     )
                                 }
                             }
                         }
                     }

                     // Payment Methods Section
                     item {
                         Spacer(Modifier.height(16.dp))

                         Card(
                             modifier = Modifier.fillMaxWidth(),
                             elevation = CardDefaults.cardElevation(8.dp)
                         ) {
                             Column(modifier = Modifier.padding(16.dp)) {
                                 Text(
                                     text = "Payment Method",
                                     fontWeight = FontWeight.Bold,
                                     fontSize = 18.sp,
                                     fontFamily = Montserrat
                                 )
                                 Spacer(Modifier.height(8.dp))

                                 FlowRow(
                                     Modifier
                                         .fillMaxWidth(1f)
                                         .padding(top = 10.dp, bottom = 10.dp)
                                         .wrapContentHeight(align = Alignment.Top),
                                     horizontalArrangement = Arrangement.spacedBy(10.dp),
                                     verticalArrangement = Arrangement.spacedBy(20.dp),
                                     maxItemsInEachRow = 3,
                                 ) {
                                     paymentMethods.forEach { method ->
                                         Row(
                                             modifier = Modifier
                                                 .clickable { selectedPaymentMethod = method.id }
                                                 .background(if (selectedPaymentMethod == method.id) Color.LightGray else Color.Transparent)
                                                 .border(
                                                     width = 2.dp,
                                                     color = if (selectedPaymentMethod == method.id) Color.Blue else Color.Transparent,
                                                     shape = RoundedCornerShape(8.dp)
                                                 )
                                                 .padding(12.dp),
                                             verticalAlignment = Alignment.CenterVertically
                                         ) {
                                             Image(
                                                 painter = painterResource(id = method.imageRes),
                                                 contentDescription = method.name,
                                                 modifier = Modifier.size(24.dp)
                                             )
                                             Spacer(Modifier.width(8.dp))
                                             Text(
                                                 text = method.name,
                                                 fontSize = 16.sp,
                                                 fontWeight = FontWeight.Medium,
                                                 fontFamily = Montserrat
                                             )
                                         }
                                     }
                                 }
                             }
                         }
                     }*/

                    // Order Summary Section
                    item {
                        Spacer(Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Order Summary",
                                    style = AppMainTypography.subHeader,
                                )
                                Spacer(Modifier.height(8.dp))
                                OrderSummaryRow("Subtotal", orderDetails.subtotal)
                                OrderSummaryRow("Discount", orderDetails.discount)
                                OrderSummaryRow("Tax", orderDetails.tax)
                                OrderSummaryRow("Delivery Charges", orderDetails.deliveryCharges)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                OrderSummaryRow("Final Total", orderDetails.finalTotal, bold = true)
                            }
                        }
                    }

                    // Place Order Button
                    item {
                        /*if (isLoading) {
                            CenteredCircularProgressIndicator()
                        }*/
                        Spacer(Modifier.height(24.dp))
                        if (orderDetails.shippingAddress != null) {
                        LoadingButton(
                            onClick = {
                                val cartItems = products.map {
                                    CartItemJson(
                                        productId = it.product_id,
                                        quantity = it.quantity.toInt(),
                                        quantityAmountPrice = it.quantity_amount_price.replace(
                                            ",",
                                            ""
                                        ).toString(),  // Convert Double to String
                                        taxAmount = it.tax_amount.replace(",", "")
                                            .toString(),              // Convert Double to String
                                        quantityAmountPriceWithTax = (it.quantity_amount_price_with_tax).replace(
                                            ",",
                                            ""
                                        ).toString() // Calculate total and convert to String
                                    )
                                }
                                val orderRequest = userId?.let {
                                    OrderRequest(
                                        userId = it,
                                        subtotal = orderDetails.subtotal.replace(",", "")
                                            .toDouble(),
                                        discount = orderDetails.discount.replace(",", "")
                                            .toDouble(),
                                        tax = orderDetails.tax.replace(",", "").toDouble(),
                                        deliveryCharges = orderDetails.deliveryCharges.replace(
                                            ",",
                                            ""
                                        ).toDouble(),
                                        finalTotal = orderDetails.finalTotal.replace(",", "")
                                            .toDouble(),
                                        cartItems = cartItems,
                                        paymentMethod = selectedPaymentMethod,
                                        shippingAddressId = orderDetails.shippingAddress!!.id
                                    )
                                }
                                Log.d("OrderRequest", orderRequest.toString())
                                if (orderRequest != null) {
                                    orderViewModel.placeOrder(
                                        orderRequest,
                                        onResult = { success, message, response ->
                                            if (success) {
                                                Toast.makeText(context, message, Toast.LENGTH_SHORT)
                                                    .show()
                                                Log.d("Order", "Order ID: ${response?.orderId}")
                                                navController.navigate("orderScreen/${response?.orderId}")
                                            } else {
                                                Toast.makeText(context, message, Toast.LENGTH_SHORT)
                                                    .show()
                                            }
                                        }
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            text = "Place Order",
                            isLoading = isLoading)}
                    }
                }

                is Resource.Error -> {
                    item {
                        Text(
                            "Error: ${(orderState as Resource.Error).message}",
                            fontFamily = Montserrat
                        )
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
fun OrderRow(product: CartItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ✅ Product Image
                Image(
                    painter = rememberAsyncImagePainter(model = product.productImageUrl),
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                // ✅ Product Details
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    // Product Name
                    Text(
                        text = product.productName,
                        color = black,
                        maxLines = 2,
                        style = ProductTypography.prodTitleMedium
                    )
                    Spacer_8dp()
                    // Product Rating with Star Icon
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating Star",
                            tint = Color(0xFFFFD700), // Golden color
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = product.productRating,
                            style = ProductTypography.prodRating,
                            color = DarkBlue,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
            Spacer_4dp()
            IncludeApp().CustomDivider()
            Spacer_4dp()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Rs.${product.productPrice}",
                    style = ProductTypography.prodDiscountPrice,
                    color = DarkBlue
                )
                Text(
                    text = "Rs.${product.productDiscountPrice}",
                    style = ProductTypography.prodPriceBold,
                    color = DarkBlue
                )

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Quantity: ${product.quantity}",
                    style = ProductTypography.productQuantity,
                    color = Color.Black
                )
                Text(
                    text = "Subtotal: Rs.${product.quantityAmountPrice}",
                    style = ProductTypography.prodPriceBold,
                    color = Color.Black
                )

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "",
                    fontSize = 14.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = "Total (incl. tax): Rs.${product.quantityAmountPriceWithTax}",
                    style = ProductTypography.prodPriceBold,
                    color = DarkBlue
                )

            }

        }
    }
}


data class PaymentMethod(val id: String, val name: String, val imageRes: Int)

@Composable
fun OrderSummaryRow(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "$label:",
            fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Medium,
            fontFamily = Montserrat
        )
        Text(
            Ruppes + value,
            fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Medium,
            fontFamily = Montserrat
        )
    }
}

@Composable
fun OrderSummaryTitleRow(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "$label:",
            fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal,
            fontFamily = Montserrat
        )
        Text(
            value,
            fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal,
            fontFamily = Montserrat
        )
    }
}
