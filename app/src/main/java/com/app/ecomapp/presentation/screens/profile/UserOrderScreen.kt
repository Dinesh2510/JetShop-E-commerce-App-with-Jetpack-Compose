package com.app.ecomapp.presentation.screens.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.order.UserOrder
import com.app.ecomapp.data.models.order.UserOrderResponse
import com.app.ecomapp.presentation.components.Spacer_12dp
import com.app.ecomapp.presentation.components.Spacer_16dp
import com.app.ecomapp.presentation.components.Spacer_4dp
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.presentation.screens.order.OrderViewModel
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.ui.theme.ProductTypography
import com.app.ecomapp.ui.theme.ProductTypography.productQuantity
import com.app.ecomapp.utils.CommonFunction.LottieAnimationView
import com.app.ecomapp.utils.HandleApiState
import com.app.ecomapp.utils.IncludeApp
import com.app.ecomapp.utils.UserDataStore
import com.compose.jetshop.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserOrderScreen(
    navController: NavController,
    viewModel: OrderViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val userDataStore = UserDataStore(context)
    val user by userDataStore.getUserData.collectAsState(initial = null)
    val cancelState by viewModel.cancelOrderState.collectAsState()
    val orderList = remember { mutableStateOf<UserOrderResponse?>(null) }

    println("Composable entered composition")

    // LaunchedEffect runs only when user is not null
    LaunchedEffect(user) {
        user?.let {
            println("LaunchedEffect triggered for userId: ${it.userId}")
            viewModel.getUserOrders(it.userId)
        }
    }

    // Scaffold - provides structure for your UI
    Scaffold(
        topBar = {
            ToolbarWithBackButtonAndTitle("My Orders",
                onBackClick = { navController.popBackStack() })
        },
        content = { paddingValues -> // Pass paddingValues to content to account for top bar spacing
            // Handle API state and show orders
            HandleApiState(
                apiState = viewModel.userOrdersState,
                showLoader = true,
                navController = navController,
                onSuccess = { data -> orderList.value = data },
            ) {
                orderList.value?.let { data ->
                    if (data.success) {
                        LazyColumn(
                            contentPadding = paddingValues, // Adding padding from Scaffold
                        ) {
                            items(data.userOrders.size) { orderIndex ->
                                user?.let {
                                    OrderItemNew(
                                        data.userOrders[orderIndex],
                                        viewModel,
                                        it.userId!!
                                    )
                                }
                            }
                        }
                    } else {
                        LottieAnimationView(animationResId = R.raw.empty_box, text = "Place the order now!")
                    }
                }
            }
        }
    )

    // Handle cancel order API response
    LaunchedEffect(cancelState) {
        if (cancelState is Resource.Success) {
            user?.let {
                viewModel.getUserOrders(it.userId) // Refresh orders on cancel success
            }
        } else if (cancelState is Resource.Error) {
            Toast.makeText(
                context, (cancelState as Resource.Error).message, Toast.LENGTH_SHORT
            ).show()
        }
    }
}

// TODO: download pdf receipt
@Composable
fun OrderItemNew(order: UserOrder, viewModel: OrderViewModel, userId: String) {
    var selectedOrder by remember { mutableStateOf<UserOrder?>(null) }

    // Convert date format
    val createdDate = remember(order.createdAt) {
        formatDate(order.createdAt)
    }
    val expected_delivery_date = remember(order.expected_delivery_date) {
        formatDate(order.expected_delivery_date)
    }

    // Colors for different statuses
    val statusColor = when (order.status) {
        "Pending" -> Color(0xFF00BCD4)
        "Processing" -> Color.Blue
        "Shipped" -> Color.Green
        "Delivered" -> Color.Green
        "Cancelled" -> Color.Red
        "Returned" -> Color.Gray
        else -> Color.Gray
    }

    val paymentStatusColor = when (order.paymentStatus) {
        "Pending" -> Color(0xFF00BCD4)
        "Completed" -> Color.Green
        "Failed" -> Color.Red
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { selectedOrder = order },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(Color.White) // White background for the popup
    ) {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Order ID: ${order.orderId}", style = ProductTypography.prodPriceBold) // 16sp
                Spacer_8dp()
                Text(
                    "Amount Paid : ₹${order.totalAmount}",
                    style = ProductTypography.productQuantity
                ) // 16sp Bold
                Spacer_4dp()
                Text("Ordered on $createdDate", style = ProductTypography.productQuantity) // 14sp
                Spacer_4dp()
                Text(
                    "Expected Delivery:  $expected_delivery_date",
                    style = ProductTypography.productQuantity
                ) // 14sp
                Spacer_4dp()
                Text(
                    "Order Status: ${order.status}",
                    color = statusColor,
                    style = ProductTypography.productQuantity // 14sp Bold
                )
                Spacer_4dp()
                Text(
                    text = "Payment Status: ${if (order.paymentStatus == "COD_CONFIRMED") "COD" else order.paymentStatus}",
                    color = paymentStatusColor,
                    style = ProductTypography.productQuantity // 14sp Bold
                )

                Spacer(modifier = Modifier.height(8.dp))
                if (order.paymentDetails != null) {
                    if (order.paymentDetails.paymentMethod != "COD") {
                        Text(
                            text = "Note:- Please complete the payment for order #${order.orderId} within 1 hour to avoid cancellation.",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            style = ProductTypography.labelSmall // 14sp Bold
                        )
                    }
                    //“Your order #1234 has been cancelled because payment wasn't completed within 1 hour.”
                }
            }

            // Conditionally show Cancel button only if order is 'Pending' or 'Processing'
            if (order.status == "Pending" || order.status == "Processing") {
                Button(
                    onClick = {
                        viewModel.cancelOrder(userId = userId, orderId = order.orderId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 0.dp)
                ) {
                    Text("Cancel Order", color = Color.White, style = productQuantity)
                }

            }
        }
    }
    if (selectedOrder != null) {
        OrderDetailsPopup(order = selectedOrder!!, onClose = { selectedOrder = null })
    }
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        //   val outputFormat = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString // Return original if parsing fails
    }
}


@Composable
fun StepProgressBar(currentStep: Int, statuses: List<String>) {
    // Create a list of the possible status steps, assuming the list from the server
    Log.d("TAG__", "StepProgressBa0r: $currentStep")
    Log.d("TAG__", "StepProgressBar1: $currentStep")
    val possibleSteps =
        listOf("Pending", "Processing", "Shipped", "Delivered", "Cancelled", "Returned")
    Log.d("TAG__", "StepProgressBar2: $possibleSteps")

    // Filter out "Returned" if not in the statuses
    val filteredSteps = possibleSteps.filter { it in statuses }
    Log.d("TAG__", "StepProgressBar3: $filteredSteps")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        filteredSteps.forEachIndexed { index, step ->
            // Step Indicator (Circle)
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (index < currentStep) Color(0xFFFF6600) else Color.LightGray)
            )

            // Horizontal Line (ONLY if not the last step)
            if (index < filteredSteps.lastIndex) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(if (index < currentStep - 1) Color(0xFFFF6600) else Color.LightGray)
                )
            }
        }
    }

    // Step Text Labels (Separate Row for proper alignment)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        filteredSteps.forEachIndexed { index, step ->
            Text(
                text = step,
                fontSize = 12.sp,
                fontWeight = if (index < currentStep) FontWeight.Bold else FontWeight.Normal,
                color = if (index < currentStep) Color(0xFFFF6600) else Color.LightGray,
                fontFamily = Montserrat // Add fontFamily here as well
            )
        }
    }
}

@Composable
fun OrderDetailsPopup(order: UserOrder, onClose: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Show the popup after a small delay
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    val statusHistory =
        listOf("Pending", "Processing", "Shipped", "Delivered") // This list comes from the server

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f),
        exit = fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier
                    .fillMaxSize() // Full screen
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(Color.White) // White background for the popup
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Close Button
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                visible = false
                                delay(300) // Delay to match exit animation
                                onClose()
                            }
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Black
                        )
                    }

                    // Order Summary Title
                    Text(
                        text = "Order Summary",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp),
                        fontFamily = Montserrat
                    )

                    // StepProgressBar
                    StepProgressBar(
                        currentStep = statusHistory.indexOf("Processing") + 1, // The current step (index + 1)
                        statuses = statusHistory
                    )

                    // Divider after the progress bar
                    IncludeApp().CustomDivider(Modifier.padding(vertical = 16.dp))
// 🧾 Order ID and Details
                    Text("Order ID: ${order.orderId}", style = ProductTypography.prodTitleMedium)
                    Spacer_4dp()
                    Text("Status: ${order.status}", style = ProductTypography.prodSubtitle)
                    Spacer_4dp()
                    Text(
                        "Payment Status: ${order.paymentStatus}",
                        style = ProductTypography.prodSubtitle
                    )
                    Spacer_4dp()
                    Text(
                        "Total Amount: ₹${order.totalAmount}",
                        style = ProductTypography.prodPriceBold
                    )
                    Spacer_4dp()
                    Text("Created At: ${order.createdAt}", style = ProductTypography.prodSubtitle)
                    Spacer_16dp()

// Divider after basic order details
                    IncludeApp().CustomDivider(Modifier.padding(vertical = 8.dp))
                    Spacer_16dp()

// 📦 Additional Order Info (subtotal, discount, etc.)
                    Text("Subtotal: ₹${order.subtotal}", style = ProductTypography.prodSubtitle)
                    Spacer_4dp()
                    Text(
                        "Discount: ₹${order.discountValue}",
                        style = ProductTypography.prodDiscountPrice
                    )
                    Spacer_4dp()
                    Text(
                        "Coupon Code: ${order.couponCode ?: "N/A"}",
                        style = ProductTypography.prodSubtitle
                    )
                    Spacer_4dp()
                    Text("Tax: ₹${order.taxAmount}", style = ProductTypography.prodSubtitle)
                    Spacer_4dp()
                    Text(
                        "Delivery Charges: ₹${order.deliveryCharges}",
                        style = ProductTypography.prodSubtitle
                    )
                    Spacer_4dp()
                    Text(
                        "Total Amount: ₹${order.totalAmount}",
                        style = ProductTypography.prodPriceBold
                    )
                    Spacer_16dp()

// Divider after additional order info
                    IncludeApp().CustomDivider(Modifier.padding(vertical = 8.dp))
                    Spacer_16dp()

// 🛍️ Product Details
                    Text("Products:", style = ProductTypography.prodTitleMedium)
                    Spacer_8dp()

                    order.items.forEachIndexed { index, item ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(
                                "${index + 1}) ${item.productName}",
                                style = ProductTypography.prodTitleMedium
                            )
                            Spacer_4dp()
                            Text("Price: ₹${item.price}", style = ProductTypography.prodPriceBold)
                            Spacer_4dp()
                            Text(
                                "Quantity: ${item.quantity}",
                                style = ProductTypography.productQuantity
                            )
                            Spacer_4dp()
                            Text("Tax: ₹${item.tax}", style = ProductTypography.prodSubtitle)
                            Spacer_4dp()
                            Text(
                                "Total Price: ₹${item.totalPrice}",
                                style = ProductTypography.prodPriceBold
                            )
                        }
                        Spacer_12dp()
                    }

                    Spacer_16dp()
                    IncludeApp().CustomDivider(Modifier.padding(vertical = 8.dp))
                    Spacer_16dp()

// 🚚 Shipping Details
                    Text("Shipping Address:", style = ProductTypography.prodTitleMedium)
                    Spacer_8dp()
                    Text(
                        "Address: ${order.shippingDetails.address}",
                        style = ProductTypography.prodDescription
                    )
                    Spacer_4dp()
                    Text(
                        "City: ${order.shippingDetails.city}",
                        style = ProductTypography.prodDescription
                    )
                    Spacer_4dp()
                    Text(
                        "State: ${order.shippingDetails.state}",
                        style = ProductTypography.prodDescription
                    )
                    Spacer_4dp()
                    Text(
                        "Postal Code: ${order.shippingDetails.postalCode}",
                        style = ProductTypography.prodDescription
                    )
                    Spacer_4dp()
                    Text(
                        "Country: ${order.shippingDetails.country}",
                        style = ProductTypography.prodDescription
                    )
                    Spacer_4dp()
                    Text(
                        "Google address: ${order.shippingDetails.googleAddress}",
                        style = ProductTypography.prodDescription
                    )
                    Spacer_16dp()

                    IncludeApp().CustomDivider(Modifier.padding(vertical = 8.dp))
                    Spacer_16dp()

// 💳 Payment Details
                    Text("Payment Details:", style = ProductTypography.prodTitleMedium)
                    Spacer_8dp()

                    if (order.paymentDetails != null) {
                        Text(
                            "Payment Method: ${order.paymentDetails.paymentMethod}",
                            style = ProductTypography.prodSubtitle
                        )
                        Spacer_4dp()
                        Text(
                            "Amount: ₹${order.paymentDetails.amount}",
                            style = ProductTypography.prodPriceBold
                        )
                        Spacer_4dp()
                        Text(
                            "Transaction Status: ${order.paymentDetails.transactionStatus}",
                            style = ProductTypography.prodSubtitle
                        )
                    } else {
                        Text(
                            "Payment details are not available.",
                            style = ProductTypography.prodDescription
                        )
                    }


                    // Divider after shipping details
                    IncludeApp().CustomDivider(Modifier.padding(vertical = 16.dp))
                }
            }
        }
    }
}
