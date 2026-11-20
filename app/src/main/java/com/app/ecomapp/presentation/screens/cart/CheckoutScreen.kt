package com.app.ecomapp.presentation.screens.cart

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
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
import com.app.ecomapp.presentation.components.Spacer_16dp
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.order.OrderViewModel
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.CommonFunction.LottieAnimationView
import com.app.ecomapp.utils.Constants.Companion.Ruppes
import com.app.ecomapp.utils.UserDataStore
import com.compose.jetshop.R
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavHostController,
    cartViewModel: CartViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val orderState by cartViewModel.orderDetails.collectAsState()
    val dataStoreHelper = remember { UserDataStore(context) }
    val userId by dataStoreHelper.getValue(UserDataStore.USER_ID).collectAsState(initial = "")
    val products = remember { mutableStateListOf<Product>() }
    val isLoading = orderViewModel.isLoading

    val paymentMethods = remember {
        listOf(
            PaymentMethod("COD", "Cash On Delivery", R.drawable.cod),
            PaymentMethod("RAZORPAY", "Razorpay / Online", R.drawable.razor_pay_icon),
        )
    }
    var selectedPaymentMethod by remember { mutableStateOf(paymentMethods.first().id) }

    LaunchedEffect(Unit) {
        cartViewModel.fetchOrderDetails("")
    }

    LaunchedEffect(orderState) {
        when (orderState) {
            is Resource.Success -> {
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
                }
            }
            is Resource.Error -> {
                Toast.makeText(context, (orderState as Resource.Error).message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            ToolbarWithBackButtonAndTitle(
                title = "Order Checkout",
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            // Sticky Bottom Action Bar
            val currentDetails = (orderState as? Resource.Success)?.data
            if (currentDetails != null && currentDetails.cartItems.isNotEmpty()) {
                CheckoutBottomBar(
                    totalAmount = currentDetails.finalTotal,
                    isLoading = isLoading,
                    isAddressAvailable = currentDetails.shippingAddress != null,
                    onPlaceOrder = {
                        val shipping = currentDetails.shippingAddress
                        if (shipping == null) {
                            Toast.makeText(context, "Please select a delivery address", Toast.LENGTH_SHORT).show()
                            navController.navigate(Screen.Address.route)
                            return@CheckoutBottomBar
                        }

                        val cartItems = products.map {
                            CartItemJson(
                                productId = it.product_id,
                                quantity = it.quantity.toIntOrNull() ?: 1,
                                quantityAmountPrice = it.quantity_amount_price.replace(",", ""),
                                taxAmount = it.tax_amount.replace(",", ""),
                                quantityAmountPriceWithTax = it.quantity_amount_price_with_tax.replace(",", "")
                            )
                        }

                        val orderRequest = userId?.let { uid ->
                            OrderRequest(
                                userId = uid,
                                subtotal = currentDetails.subtotal.replace(",", "").toDoubleOrNull() ?: 0.0,
                                discount = currentDetails.discount.replace(",", "").toDoubleOrNull() ?: 0.0,
                                tax = currentDetails.tax.replace(",", "").toDoubleOrNull() ?: 0.0,
                                deliveryCharges = currentDetails.deliveryCharges.replace(",", "").toDoubleOrNull() ?: 0.0,
                                finalTotal = currentDetails.finalTotal.replace(",", "").toDoubleOrNull() ?: 0.0,
                                cartItems = cartItems,
                                paymentMethod = selectedPaymentMethod,
                                shippingAddressId = shipping.id
                            )
                        }

                        if (orderRequest != null) {
                            orderViewModel.placeOrder(
                                orderRequest,
                                onResult = { success, message, response ->
                                    if (success) {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        navController.navigate("orderScreen/${response?.orderId}")
                                    } else {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        when (val state = orderState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CenteredCircularProgressIndicator()
                }
            }

            is Resource.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Failed to load checkout details:\n${state.message}",
                        color = Color(0xFFEF4444),
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }

            is Resource.Success -> {
                val orderDetails = state.data

                if (orderDetails == null || orderDetails.cartItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        LottieAnimationView(
                            animationResId = R.raw.empty_cart,
                            text = "Your cart is empty"
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Section 1: Delivery Address
                        item {
                            DeliveryAddressCard(
                                address = orderDetails.shippingAddress,
                                onChangeAddress = { navController.navigate(Screen.Address.route) }
                            )
                        }

                        // Section 2: Items in Cart Header
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingBag,
                                        contentDescription = null,
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Order Items (${orderDetails.cartItems.size})",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = Montserrat,
                                        color = Color(0xFF0F172A)
                                    )
                                }
                            }
                        }

                        // Product List
                        items(orderDetails.cartItems) { item ->
                            ModernOrderProductCard(product = item)
                        }

                        // Section 3: Bill Details (Receipt Style)
                        item {
                            OrderSummaryCard(orderDetails = orderDetails)
                        }

                        // Section 4: Security Guarantee Badge
                        item {
                            SafetyGuaranteeBadge()
                        }
                    }
                }
            }
            null -> {}
        }
    }
}

// -------------------------------------------------------------
// SUB-COMPONENTS
// -------------------------------------------------------------

@Composable
fun DeliveryAddressCard(
    address: com.app.ecomapp.data.models.checkout.ShippingAddress?,
    onChangeAddress: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Delivering To",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Montserrat,
                        color = Color(0xFF0F172A)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFDBEAFE)),
                    modifier = Modifier.clickable { onChangeAddress() }
                ) {
                    Text(
                        text = if (address != null) "Change" else "+ Add",
                        color = Color(0xFF2563EB),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        fontFamily = Montserrat,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer_8dp()

            if (address != null) {
                Column(modifier = Modifier.padding(start = 42.dp)) {
                    Text(
                        text = address.address.trim(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF334155),
                        fontFamily = Montserrat,
                        lineHeight = 19.sp
                    )
                    Text(
                        text = "${address.city}, ${address.state} - ${address.zipCode}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A),
                        fontFamily = Montserrat
                    )
                    if (!address.googleAddress.isNullOrBlank()) {
                        Text(
                            text = address.googleAddress,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontFamily = Montserrat,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            } else {
                Surface(
                    color = Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .clickable { onChangeAddress() }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "No address selected. Tap here to select or add one.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFB45309),
                            fontFamily = Montserrat
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernOrderProductCard(product: CartItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Product Thumbnail
                Image(
                    painter = rememberAsyncImagePainter(model = product.productImageUrl),
                    contentDescription = product.productName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF8FAFC))
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Product Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = product.productName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A),
                        fontFamily = Montserrat,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Rating Pill (if present)
                    if (!product.productRating.isNullOrEmpty() && product.productRating != "0") {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = product.productRating,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                fontFamily = Montserrat
                            )
                        }
                    }

                    // Price + Quantity Tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$Ruppes${product.productDiscountPrice}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                fontFamily = Montserrat
                            )
                            if (product.productPrice != product.productDiscountPrice) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$Ruppes${product.productPrice}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8),
                                    textDecoration = TextDecoration.LineThrough,
                                    fontFamily = Montserrat
                                )
                            }
                        }

                        // Quantity Badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = "Qty: ${product.quantity}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569),
                                fontFamily = Montserrat,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                thickness = 0.8.dp,
                color = Color(0xFFF1F5F9)
            )

            // Subtotal Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Item Total (incl. tax)",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    fontFamily = Montserrat
                )
                Text(
                    text = "$Ruppes${product.quantityAmountPriceWithTax}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2563EB),
                    fontFamily = Montserrat
                )
            }
        }
    }
}

@Composable
fun OrderSummaryCard(orderDetails: OrderDetailsResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Price Details",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    fontFamily = Montserrat
                )
            }

            Spacer_16dp()

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ModernSummaryRow(label = "Cart Subtotal", value = "$Ruppes${orderDetails.subtotal}")

                // Discount (Green)
                if (orderDetails.discount.toDoubleOrNull() ?: 0.0 > 0.0) {
                    ModernSummaryRow(
                        label = "Coupon Discount",
                        value = "- $Ruppes${orderDetails.discount}",
                        valueColor = Color(0xFF10B981)
                    )
                }

                ModernSummaryRow(label = "Estimated Taxes", value = "$Ruppes${orderDetails.tax}")

                val deliveryCharge = orderDetails.deliveryCharges.toDoubleOrNull() ?: 0.0
                ModernSummaryRow(
                    label = "Delivery Fee",
                    value = if (deliveryCharge <= 0.0) "FREE" else "$Ruppes${orderDetails.deliveryCharges}",
                    valueColor = if (deliveryCharge <= 0.0) Color(0xFF10B981) else Color(0xFF0F172A)
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                // Final Total Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Payable",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            fontFamily = Montserrat
                        )
                        Text(
                            text = "Including all taxes",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontFamily = Montserrat
                        )
                    }

                    Text(
                        text = "$Ruppes${orderDetails.finalTotal}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2563EB),
                        fontFamily = Montserrat
                    )
                }
            }
        }
    }
}
data class PaymentMethod(val id: String, val name: String, val imageRes: Int)

@Composable
fun ModernSummaryRow(
    label: String,
    value: String,
    valueColor: Color = Color(0xFF334155)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color(0xFF64748B),
            fontFamily = Montserrat
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
            fontFamily = Montserrat
        )
    }
}

@Composable
fun SafetyGuaranteeBadge() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF1F5F9),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = Color(0xFF059669),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Safe & Secure Payments • 100% Authentic Products",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF475569),
                fontFamily = Montserrat
            )
        }
    }
}

@Composable
fun CheckoutBottomBar(
    totalAmount: String,
    isLoading: Boolean,
    isAddressAvailable: Boolean,
    onPlaceOrder: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 10.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Grand Total",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    fontFamily = Montserrat
                )
                Text(
                    text = "$Ruppes$totalAmount",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A),
                    fontFamily = Montserrat
                )
            }

            Button(
                onClick = onPlaceOrder,
                enabled = !isLoading && isAddressAvailable,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB),
                    disabledContainerColor = Color(0xFF94A3B8)
                ),
                modifier = Modifier
                    .width(180.dp)
                    .height(48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isAddressAvailable) "Place Order" else "Add Address",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Montserrat
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}

// Kept for backward compatibility with other screens (e.g., PaymentScreen, CartScreen)
@Composable
fun OrderSummaryRow(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            fontSize = 14.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
            fontFamily = Montserrat,
            color = if (bold) Color(0xFF0F172A) else Color(0xFF64748B)
        )
        Text(
            text = "$Ruppes$value",
            fontSize = 14.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
            fontFamily = Montserrat,
            color = if (bold) Color(0xFF2563EB) else Color(0xFF1E293B)
        )
    }
}

// Kept for backward compatibility with other screens
@Composable
fun OrderSummaryTitleRow(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            fontSize = 14.sp,
            fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal,
            fontFamily = Montserrat,
            color = Color(0xFF64748B)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal,
            fontFamily = Montserrat,
            color = Color(0xFF0F172A)
        )
    }
}