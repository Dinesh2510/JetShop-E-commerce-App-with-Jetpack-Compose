package com.app.ecomapp.presentation.screens.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.order.UserOrder
import com.app.ecomapp.presentation.components.ApiErrorView
import com.app.ecomapp.presentation.components.AppPrimaryButton
import com.app.ecomapp.presentation.components.Spacer_12dp
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
    val userDataStore = remember { UserDataStore(context) }
    val user by userDataStore.getUserData.collectAsState(initial = null)
    val ordersState by viewModel.userOrdersState.collectAsState()
    val cancelState by viewModel.cancelOrderState.collectAsState()

    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()

    // Store only the Order ID to prevent displaying stale cached dialog state
    var selectedOrderId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(ordersState) {
        if (ordersState !is Resource.Loading) {
            isRefreshing = false
        }
    }

    LaunchedEffect(user?.userId) {
        user?.userId?.let { id ->
            if (id.isNotEmpty()) {
                viewModel.getUserOrders(id)
            }
        }
    }

    LaunchedEffect(cancelState) {
        if (cancelState is Resource.Success) {
            user?.userId?.let { viewModel.getUserOrders(it) }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            ToolbarWithBackButtonAndTitle(
                "My Orders",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        when (val state = ordersState) {
            is Resource.Loading -> {
                if (!isRefreshing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF2563EB),
                            strokeWidth = 3.dp
                        )
                    }
                }
            }

            is Resource.Success -> {
                val data = state.data
                val orders = data?.userOrders.orEmpty()

                // Dynamically resolve the active selected order from the latest reactive list
                val currentSelectedOrder = remember(orders, selectedOrderId) {
                    orders.find { it.orderId == selectedOrderId }
                }

                if (orders.isNotEmpty()) {
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = {
                            isRefreshing = true
                            user?.userId?.let { viewModel.getUserOrders(it) }
                        },
                        state = pullToRefreshState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        indicator = {
                            CustomOrderPullToRefreshIndicator(
                                state = pullToRefreshState,
                                isRefreshing = isRefreshing,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 10.dp)
                            )
                        }
                    ) {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 12.dp,
                                bottom = 24.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .navigationBarsPadding()
                        ) {
                            items(orders.size, key = { orders[it].orderId }) { orderIndex ->
                                val order = orders[orderIndex]
                                OrderItemNew(
                                    order = order,
                                    viewModel = viewModel,
                                    userId = user?.userId ?: "",
                                    onOrderClick = { selectedOrderId = order.orderId }
                                )
                            }
                        }
                    }
                } else {
                    PullToRefreshBox(
                        isRefreshing = isRefreshing,
                        onRefresh = {
                            isRefreshing = true
                            user?.userId?.let { viewModel.getUserOrders(it) }
                        },
                        state = pullToRefreshState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        indicator = {
                            CustomOrderPullToRefreshIndicator(
                                state = pullToRefreshState,
                                isRefreshing = isRefreshing,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 10.dp)
                            )
                        }
                    ) {
                        EmptyOrdersView(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            onStartShoppingClick = {
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(Screen.Main.route) { inclusive = true }
                                }
                            }
                        )
                    }
                }

                // Render detail popup with reactive data so cancelled/updated state reflects immediately
                if (currentSelectedOrder != null) {
                    OrderDetailsPopup(
                        order = currentSelectedOrder,
                        onClose = { selectedOrderId = null },
                        onCancelClick = {
                            viewModel.cancelOrder(
                                userId = user?.userId ?: "",
                                orderId = currentSelectedOrder.orderId
                            )
                        }
                    )
                }
            }

            is Resource.Error -> {
                ApiErrorView(
                    message = state.message ?: "Failed to load orders. Please try again.",
                    modifier = Modifier.padding(paddingValues),
                    onRetry = {
                        user?.userId?.let { viewModel.getUserOrders(it) }
                    }
                )
            }

            else -> {}
        }
    }
}

@Composable
fun EmptyOrdersView(
    modifier: Modifier = Modifier,
    title: String = "No Orders Yet",
    subtitle: String = "You haven't placed any orders yet. Explore our top categories and grab great deals today!",
    onStartShoppingClick: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimationView(
                animationResId = R.raw.empty_box
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Montserrat,
            color = Color(0xFF0F172A),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            fontSize = 13.sp,
            fontFamily = Montserrat,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppPrimaryButton(
            text = "Start Shopping",
            onClick = onStartShoppingClick,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(48.dp)
        )
    }
}

@Composable
fun OrderItemNew(
    order: UserOrder,
    viewModel: OrderViewModel,
    userId: String,
    onOrderClick: () -> Unit
) {
    var showCancelConfirmDialog by remember { mutableStateOf(false) }

    val createdDate: String = remember(order.createdAt) { formatDate(order.createdAt) }
    val expectedDeliveryDate: String = remember(order.expected_delivery_date) { formatDate(order.expected_delivery_date) }

    val (statusBgColor, statusTextColor) = when (order.status) {
        "Pending" -> Color(0xFFEFF6FF) to Color(0xFF2563EB)
        "Processing" -> Color(0xFFFEF3C7) to Color(0xFFD97706)
        "Shipped" -> Color(0xFFE0E7FF) to Color(0xFF4F46E5)
        "Delivered" -> Color(0xFFECFDF5) to Color(0xFF059669)
        "Cancelled" -> Color(0xFFFEE2E2) to Color(0xFFDC2626)
        "Returned" -> Color(0xFFF1F5F9) to Color(0xFF64748B)
        else -> Color(0xFFF1F5F9) to Color(0xFF64748B)
    }

    val (paymentBgColor, paymentTextColor) = when (order.paymentStatus) {
        "Completed", "COD_CONFIRMED" -> Color(0xFFECFDF5) to Color(0xFF059669)
        "Pending" -> Color(0xFFFEF3C7) to Color(0xFFD97706)
        "Failed" -> Color(0xFFFEE2E2) to Color(0xFFDC2626)
        else -> Color(0xFFF1F5F9) to Color(0xFF64748B)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOrderClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F5F9)
                ) {
                    Text(
                        text = "#${order.orderId}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        fontFamily = Montserrat,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusBgColor,
                    border = BorderStroke(1.dp, statusTextColor.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = order.status,
                        color = statusTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Montserrat,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer_12dp()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Total Amount",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        fontFamily = Montserrat
                    )
                    Text(
                        text = "$Ruppes${order.totalAmount}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A),
                        fontFamily = Montserrat
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Ordered On",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        fontFamily = Montserrat
                    )
                    Text(
                        text = createdDate,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155),
                        fontFamily = Montserrat
                    )
                }
            }

            Spacer_12dp()
            HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFF1F5F9))
            Spacer_12dp()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = if (order.status == "Cancelled") Color(0xFFDC2626) else Color(0xFF2563EB),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (order.status == "Cancelled") "Order Cancelled" else "Delivery by $expectedDeliveryDate",
                        fontSize = 12.sp,
                        color = if (order.status == "Cancelled") Color(0xFFDC2626) else Color(0xFF334155),
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = paymentBgColor
                ) {
                    Text(
                        text = if (order.paymentStatus == "COD_CONFIRMED") "COD" else order.paymentStatus,
                        color = paymentTextColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Montserrat,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            if (order.paymentDetails != null && order.paymentDetails.paymentMethod != "COD" && order.paymentStatus == "Pending" && order.status != "Cancelled") {
                Spacer_8dp()
                Surface(
                    color = Color(0xFFFFF1F2),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = Color(0xFFE11D48),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Complete payment within 1 hour to prevent cancellation.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFBE123C),
                            fontFamily = Montserrat
                        )
                    }
                }
            }

            if (order.status == "Pending" || order.status == "Processing") {
                Spacer_12dp()
                OutlinedButton(
                    onClick = { showCancelConfirmDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFFDC2626)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Cancel Order",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Montserrat
                    )
                }
            }
        }
    }

    if (showCancelConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showCancelConfirmDialog = false },
            shape = RoundedCornerShape(18.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Cancel Order #${order.orderId}?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    fontFamily = Montserrat,
                    color = Color(0xFF0F172A)
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to cancel this order? Any refund due will be credited back according to bank timelines.",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    fontFamily = Montserrat
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelConfirmDialog = false
                        viewModel.cancelOrder(userId = userId, orderId = order.orderId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Confirm Cancel", fontFamily = Montserrat, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelConfirmDialog = false }) {
                    Text("Keep Order", fontFamily = Montserrat, color = Color(0xFF64748B))
                }
            }
        )
    }
}

@Composable
fun OrderDetailsPopup(
    order: UserOrder,
    onClose: () -> Unit,
    onCancelClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    var showCancelConfirmDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(60)
        visible = true
    }

    val (statusBgColor, statusTextColor) = when (order.status) {
        "Pending" -> Color(0xFFEFF6FF) to Color(0xFF2563EB)
        "Processing" -> Color(0xFFFEF3C7) to Color(0xFFD97706)
        "Shipped" -> Color(0xFFE0E7FF) to Color(0xFF4F46E5)
        "Delivered" -> Color(0xFFECFDF5) to Color(0xFF059669)
        "Cancelled" -> Color(0xFFFEE2E2) to Color(0xFFDC2626)
        "Returned" -> Color(0xFFF1F5F9) to Color(0xFF64748B)
        else -> Color(0xFFF1F5F9) to Color(0xFF64748B)
    }

    Dialog(
        onDismissRequest = {
            coroutineScope.launch {
                visible = false
                delay(180)
                onClose()
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.92f),
            exit = fadeOut(animationSpec = tween(180)) + scaleOut(targetScale = 0.92f)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Order Details",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A),
                                    fontFamily = Montserrat
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = statusBgColor,
                                    border = BorderStroke(1.dp, statusTextColor.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = order.status,
                                        color = statusTextColor,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = Montserrat,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Order ID: #${order.orderId}",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                fontFamily = Montserrat
                            )
                        }

                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    visible = false
                                    delay(180)
                                    onClose()
                                }
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEFF6FF))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF1E293B),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer_16dp()

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Delivery Status",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A),
                                    fontFamily = Montserrat
                                )
                                Spacer_8dp()
                                DynamicOrderTracker(order = order)
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingBag,
                                        contentDescription = null,
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Items in Order (${order.items.size})",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A),
                                        fontFamily = Montserrat
                                    )
                                }

                                Spacer_12dp()

                                order.items.forEachIndexed { index, item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${index + 1}. ${item.productName}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF1E293B),
                                                fontFamily = Montserrat
                                            )
                                            Text(
                                                text = "Qty: ${item.quantity} • Price: $Ruppes${item.price}",
                                                fontSize = 11.sp,
                                                color = Color(0xFF64748B),
                                                fontFamily = Montserrat
                                            )
                                        }

                                        Text(
                                            text = "$Ruppes${item.price.toDoubleOrNull()?.times(item.quantity.toIntOrNull() ?: 1) ?: item.price}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A),
                                            fontFamily = Montserrat
                                        )
                                    }

                                    if (index < order.items.lastIndex) {
                                        HorizontalDivider(
                                            thickness = 0.8.dp,
                                            color = Color(0xFFF1F5F9),
                                            modifier = Modifier.padding(vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Delivery Address",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A),
                                        fontFamily = Montserrat
                                    )
                                }

                                Spacer_8dp()

                                order.shippingDetails?.let { shipping ->
                                    Text(
                                        text = shipping.address.orEmpty(),
                                        fontSize = 13.sp,
                                        color = Color(0xFF334155),
                                        fontFamily = Montserrat,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${shipping.city.orEmpty()}, ${shipping.state.orEmpty()} ${shipping.postalCode.orEmpty()}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B),
                                        fontFamily = Montserrat
                                    )
                                    if (!shipping.googleAddress.isNullOrBlank()) {
                                        Text(
                                            text = shipping.googleAddress,
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8),
                                            fontFamily = Montserrat
                                        )
                                    }
                                } ?: Text(
                                    text = "Standard Shipping Address",
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B),
                                    fontFamily = Montserrat
                                )
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ReceiptLong,
                                        contentDescription = null,
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Payment Breakdown",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A),
                                        fontFamily = Montserrat
                                    )
                                }

                                Spacer_12dp()

                                OrderSummaryLine(label = "Subtotal", value = "$Ruppes${order.subtotal}")

                                val discount = order.discountValue?.replace(",", "")?.toDoubleOrNull() ?: 0.0
                                if (discount > 0.0) {
                                    OrderSummaryLine(
                                        label = "Coupon Savings",
                                        value = "- $Ruppes${order.discountValue}",
                                        valueColor = Color(0xFF059669)
                                    )
                                }

                                OrderSummaryLine(label = "Tax", value = "$Ruppes${order.taxAmount}")
                                OrderSummaryLine(label = "Delivery Fee", value = "$Ruppes${order.deliveryCharges}")

                                HorizontalDivider(
                                    thickness = 1.dp,
                                    color = Color(0xFFF1F5F9),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Total Paid",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A),
                                        fontFamily = Montserrat
                                    )
                                    Text(
                                        text = "$Ruppes${order.totalAmount}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF2563EB),
                                        fontFamily = Montserrat
                                    )
                                }

                                if (order.paymentDetails != null) {
                                    Spacer_8dp()
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFF8FAFC),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Payment,
                                                contentDescription = null,
                                                tint = Color(0xFF64748B),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Paid via ${order.paymentDetails.paymentMethod.orEmpty()} • ${order.paymentDetails.transactionStatus.orEmpty()}",
                                                fontSize = 12.sp,
                                                color = Color(0xFF475569),
                                                fontFamily = Montserrat,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (order.status == "Pending" || order.status == "Processing") {
                            OutlinedButton(
                                onClick = { showCancelConfirmDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color(0xFFDC2626)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Cancel This Order",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = Montserrat
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCancelConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showCancelConfirmDialog = false },
            shape = RoundedCornerShape(18.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Cancel Order #${order.orderId}?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    fontFamily = Montserrat,
                    color = Color(0xFF0F172A)
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to cancel this order? Any refund due will be credited back according to bank timelines.",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    fontFamily = Montserrat
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelConfirmDialog = false
                        onCancelClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Confirm Cancel", fontFamily = Montserrat, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelConfirmDialog = false }) {
                    Text("Keep Order", fontFamily = Montserrat, color = Color(0xFF64748B))
                }
            }
        )
    }
}

@Composable
fun OrderSummaryLine(
    label: String,
    value: String,
    valueColor: Color = Color(0xFF334155)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF64748B),
            fontFamily = Montserrat
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
            fontFamily = Montserrat
        )
    }
}

fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

@Composable
fun DynamicOrderTracker(order: UserOrder) {
    when (order.status) {
        "Cancelled" -> {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFEF2F2),
                border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEE2E2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "This order was cancelled",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626),
                            fontFamily = Montserrat
                        )
                        Text(
                            text = "No further updates will be posted for this shipment.",
                            fontSize = 11.sp,
                            color = Color(0xFF7F1D1D),
                            fontFamily = Montserrat
                        )
                    }
                }
            }
        }

        "Returned" -> {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Item returned and processed",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569),
                        fontFamily = Montserrat
                    )
                }
            }
        }

        else -> {
            val normalSteps = listOf("Pending", "Processing", "Shipped", "Delivered")
            val currentStep = normalSteps.indexOf(order.status).let { if (it >= 0) it + 1 else 1 }
            StepProgressBar(currentStep = currentStep, statuses = normalSteps)
        }
    }
}

@Composable
fun StepProgressBar(currentStep: Int, statuses: List<String>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            statuses.forEachIndexed { index, _ ->
                val isCompleted = index < currentStep
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(if (isCompleted) Color(0xFF2563EB) else Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                if (index < statuses.lastIndex) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.5.dp)
                            .background(if (index < currentStep - 1) Color(0xFF2563EB) else Color(0xFFE2E8F0))
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            statuses.forEachIndexed { index, step ->
                Text(
                    text = step,
                    fontSize = 11.sp,
                    fontWeight = if (index < currentStep) FontWeight.Bold else FontWeight.Medium,
                    color = if (index < currentStep) Color(0xFF2563EB) else Color(0xFF94A3B8),
                    fontFamily = Montserrat
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomOrderPullToRefreshIndicator(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier
) {
    val distanceFraction = state.distanceFraction.coerceIn(0f, 1.5f)
    val isVisible = isRefreshing || distanceFraction > 0.1f

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(200)) + scaleIn(spring(dampingRatio = 0.6f)),
        exit = fadeOut(tween(250)) + scaleOut(targetScale = 0.7f),
        modifier = modifier
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse_radar")

        val spinAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(900, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "spin_angle"
        )

        val shimmerTranslate by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 300f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer_badge"
        )

        val animatedScale by animateFloatAsState(
            targetValue = if (distanceFraction >= 1f || isRefreshing) 1.05f else 0.9f + (distanceFraction * 0.1f),
            animationSpec = spring(dampingRatio = 0.5f),
            label = "scale_anim"
        )

        val isTriggered = distanceFraction >= 1f && !isRefreshing

        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF0F172A),
            shadowElevation = 12.dp,
            border = BorderStroke(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = if (isTriggered || isRefreshing) {
                        listOf(Color(0xFF38BDF8), Color(0xFF2563EB), Color(0xFF818CF8))
                    } else {
                        listOf(Color(0xFF334155), Color(0xFF1E293B))
                    }
                )
            ),
            modifier = Modifier.graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(28.dp)) {
                        val strokeWidth = 2.8.dp.toPx()

                        drawCircle(
                            color = Color(0xFF1E293B),
                            style = Stroke(width = strokeWidth)
                        )

                        if (isRefreshing) {
                            drawArc(
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        Color(0xFF2563EB).copy(alpha = 0.1f),
                                        Color(0xFF38BDF8),
                                        Color(0xFF60A5FA)
                                    )
                                ),
                                startAngle = spinAngle,
                                sweepAngle = 270f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        } else {
                            drawArc(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF38BDF8), Color(0xFF2563EB))
                                ),
                                startAngle = -90f,
                                sweepAngle = (distanceFraction * 360f).coerceIn(0f, 360f),
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }

                    if (isRefreshing) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8))
                        )
                    } else if (isTriggered) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(15.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.ShoppingBag,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier
                                .size(14.dp)
                                .rotate(distanceFraction * 25f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                AnimatedContent(
                    targetState = when {
                        isRefreshing -> "Refreshing Orders..."
                        isTriggered -> "Release to Refresh"
                        else -> "Pull Down to Refresh"
                    },
                    transitionSpec = {
                        fadeIn(tween(180)) togetherWith fadeOut(tween(180))
                    },
                    label = "text_morph"
                ) { statusText ->
                    val textBrush = if (isRefreshing || isTriggered) {
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFE2E8F0),
                                Color(0xFF38BDF8),
                                Color(0xFFE2E8F0)
                            ),
                            start = Offset.Zero,
                            end = Offset(x = shimmerTranslate, y = 0f)
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8))
                        )
                    }

                    Text(
                        text = statusText,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        style = TextStyle(brush = textBrush)
                    )
                }
            }
        }
    }
}