package com.app.ecomapp.presentation.screens.order

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.placeOrder.PlaceOrderDetailsResponse
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator
import com.app.ecomapp.presentation.components.PaymentFailedScreen
import com.app.ecomapp.presentation.components.Spacer_16dp
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.cart.PaymentMethod
import com.app.ecomapp.presentation.screens.payment.PaymentScreen
import com.app.ecomapp.presentation.screens.payment.PaymentViewModel
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.Constants.Companion.Ruppes
import com.app.ecomapp.utils.HandleApiState
import com.app.ecomapp.utils.UserDataStore
import com.compose.jetshop.R
import java.util.Locale

@Composable
fun OrderScreen(
    navController: NavController,
    order_id: String,
    viewModel: OrderViewModel = hiltViewModel(),
    paymentViewModel: PaymentViewModel = hiltViewModel(),
) {
    Log.d("OrderScreen", "Composable Launched with order_id: $order_id")

    val orderState by viewModel.orderState.collectAsState()
    val removeCoupons by viewModel.removeCoupons.collectAsState()
    val applyCoupons by viewModel.applyCoupon.collectAsState()
    var apiData by remember { mutableStateOf<PlaceOrderDetailsResponse?>(null) }
    val context = LocalContext.current
    val hasNavigated = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getPlaceOrderDetails(order_id, "")
    }

    LaunchedEffect(applyCoupons, removeCoupons) {
        if (applyCoupons is Resource.Success || removeCoupons is Resource.Success) {
            viewModel.getPlaceOrderDetails(order_id, "")
        } else if (applyCoupons is Resource.Error || removeCoupons is Resource.Error) {
            val errorMessage = (applyCoupons as? Resource.Error)?.message
                ?: (removeCoupons as? Resource.Error)?.message
                ?: "Unknown error"

            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    HandleApiState(
        apiState = viewModel.orderState,
        showLoader = true,
        onRetry = { viewModel.getPlaceOrderDetails(order_id = order_id,"") },
        navController = navController,
        onSuccess = { data ->
            apiData = data
        }
    ) {
        apiData?.let {
            PaymentPage(
                paymentViewModel = paymentViewModel,
                navController = navController,
                apiData = it,
                viewModel = viewModel,
                order_id = order_id,
                hasNavigated = hasNavigated
            )
        }
    }
}

@Composable
fun PaymentPage(
    paymentViewModel: PaymentViewModel,
    navController: NavController,
    apiData: PlaceOrderDetailsResponse,
    viewModel: OrderViewModel,
    order_id: String,
    hasNavigated: MutableState<Boolean>
) {
    val context = LocalContext.current as Activity
    val userDataStore = remember { UserDataStore(context) }

    val userId by userDataStore.getValue(UserDataStore.USER_ID).collectAsState(initial = "")
    val userEmail by userDataStore.getValue(UserDataStore.USER_EMAIL).collectAsState(initial = "")
    val userPhone by userDataStore.getValue(UserDataStore.USER_PHONE).collectAsState(initial = "")

    val initiatePaymentState by paymentViewModel.initiatePayment.collectAsState()

    var selectedPaymentMethod by remember { mutableStateOf("RAZORPAY") }
    var couponInput by remember { mutableStateOf("") }
    var appliedCoupon by remember { mutableStateOf<String?>(null) }

    val paymentFailed = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

    val appName = stringResource(id = R.string.app_name)
    val amountString = apiData.orderDetails?.totalAmount?.replace(",", "").orEmpty()

    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultOrderId = result.data?.getStringExtra("order_id") ?: order_id
            Log.d("OrderScreen", "Payment Successful for Order ID: $resultOrderId")
            navController.navigate("success_screen/$resultOrderId") {
                popUpTo(0) { inclusive = true }
            }
        } else {
            Log.e("OrderScreen", "Payment Failed or Cancelled")
            paymentFailed.value = true
            errorMessage.value = "Payment was cancelled or unsuccessful. Please try again."
        }
    }

    LaunchedEffect(apiData) {
        appliedCoupon = apiData.orderDetails?.couponCode
    }

    LaunchedEffect(initiatePaymentState) {
        if (initiatePaymentState is Resource.Success) {
            val data = (initiatePaymentState as Resource.Success).data
            if (data?.paymentStatus == "COD_CONFIRMED" && !hasNavigated.value) {
                hasNavigated.value = true
                navController.navigate("success_screen/$order_id") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    val paymentMethods = remember {
        listOf(
            PaymentMethod("RAZORPAY", "Online Payment (UPI, Cards, NetBanking)", R.drawable.razor_pay_icon),
            PaymentMethod("COD", "Cash On Delivery (Pay upon delivery)", R.drawable.cod)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color(0xFFF8FAFC),
            topBar = {
                ToolbarWithBackButtonAndTitle(
                    title = "Order Payment",
                    onBackClick = { navController.popBackStack() }
                )
            },
            bottomBar = {
                OrderPaymentBottomBar(
                    totalAmount = apiData.orderDetails?.totalAmount.orEmpty(),
                    isLoading = initiatePaymentState is Resource.Loading,
                    selectedMethod = selectedPaymentMethod,
                    onPayClick = {
                        if (selectedPaymentMethod.isEmpty()) {
                            Toast.makeText(context, "Please choose a payment method", Toast.LENGTH_SHORT).show()
                            return@OrderPaymentBottomBar
                        }

                        val parsedAmount = amountString.toDoubleOrNull() ?: 0.0

                        if (selectedPaymentMethod == "COD") {
                            paymentViewModel.initiatePayment(
                                orderId = order_id,
                                userId = userId.orEmpty(),
                                paymentMethod = "COD",
                                amount = parsedAmount,
                                currency = "INR"
                            )
                        } else {
                            val intent = Intent(context, PaymentScreen::class.java).apply {
                                putExtra("user_id", userId)
                                putExtra("orderId", order_id)
                                putExtra("appName", appName)
                                putExtra("amount", amountString)
                                putExtra("email", userEmail.orEmpty())
                                putExtra("contact", userPhone.orEmpty())
                            }
                            activityResultLauncher.launch(intent)
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Order ID Info Badge
                item {
                    OrderInfoTile(orderId = apiData.orderDetails?.orderId ?: order_id)
                }

                // 2. Coupon Card
                item {
                    CouponSectionCard(
                        couponInput = couponInput,
                        appliedCoupon = appliedCoupon,
                        onCouponChange = { couponInput = it },
                        onApplyCoupon = {
                            if (couponInput.isNotBlank() && couponInput != appliedCoupon) {
                                viewModel.applyCoupon(order_id = order_id, coupon_code = couponInput)
                            }
                        },
                        onRemoveCoupon = {
                            appliedCoupon?.let { code ->
                                viewModel.removeCoupon(order_id = order_id, coupon_code = code)
                                appliedCoupon = null
                            }
                        }
                    )
                }

                // 3. Payment Methods Card
                item {
                    PaymentMethodsCard(
                        methods = paymentMethods,
                        selectedMethod = selectedPaymentMethod,
                        onSelectMethod = { selectedPaymentMethod = it }
                    )
                }

                // 4. Order Summary Card
                item {
                    apiData.orderDetails?.let { details ->
                        OrderSummaryBreakdownCard(details = details)
                    }
                }

                // 5. Trust Badge
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "256-Bit Encrypted & PCI-DSS Compliant Payment",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontFamily = Montserrat
                        )
                    }
                }
            }
        }

        // Failure Screen Overlay
        if (paymentFailed.value) {
            PaymentFailedScreen(
                navController = navController,
                errorMessage = errorMessage.value,
                onRetryClick = {
                    paymentFailed.value = false
                },
                onGoHomeClick = {
                    paymentFailed.value = false
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

// -------------------------------------------------------------
// COMPONENTS
// -------------------------------------------------------------

@Composable
fun OrderInfoTile(orderId: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFEFF6FF),
        border = BorderStroke(1.dp, Color(0xFFDBEAFE)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Order Number",
                    fontSize = 13.sp,
                    color = Color(0xFF1E3A8A),
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "#$orderId",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D4ED8),
                fontFamily = Montserrat
            )
        }
    }
}

@Composable
fun CouponSectionCard(
    couponInput: String,
    appliedCoupon: String?,
    onCouponChange: (String) -> Unit,
    onApplyCoupon: () -> Unit,
    onRemoveCoupon: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ConfirmationNumber,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Offers & Coupons",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    fontFamily = Montserrat
                )
            }

            Spacer_16dp()

            if (appliedCoupon.isNullOrBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = couponInput.uppercase(Locale.getDefault()),
                        onValueChange = onCouponChange,
                        placeholder = {
                            Text(
                                "Enter Coupon Code",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8),
                                fontFamily = Montserrat
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = onApplyCoupon,
                        enabled = couponInput.isNotBlank(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB),
                            disabledContainerColor = Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Text(
                            text = "Apply",
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFECFDF5),
                    border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF059669),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = appliedCoupon.uppercase(Locale.getDefault()),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF065F46),
                                    fontFamily = Montserrat
                                )
                                Text(
                                    text = "Coupon applied successfully!",
                                    fontSize = 11.sp,
                                    color = Color(0xFF047857),
                                    fontFamily = Montserrat
                                )
                            }
                        }

                        IconButton(
                            onClick = onRemoveCoupon,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove coupon",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodsCard(
    methods: List<PaymentMethod>,
    selectedMethod: String,
    onSelectMethod: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Payment,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Select Payment Option",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    fontFamily = Montserrat
                )
            }

            Spacer_16dp()

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                methods.forEach { method ->
                    val isSelected = selectedMethod == method.id

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectMethod(method.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Image(
                                    painter = painterResource(id = method.imageRes),
                                    contentDescription = method.name,
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = method.name,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFF1E3A8A) else Color(0xFF334155),
                                    fontFamily = Montserrat
                                )
                            }

                            RadioButton(
                                selected = isSelected,
                                onClick = { onSelectMethod(method.id) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFF2563EB),
                                    unselectedColor = Color(0xFF94A3B8)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderSummaryBreakdownCard(details: com.app.ecomapp.data.models.placeOrder.OrderDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Payment Breakdown",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    fontFamily = Montserrat
                )
            }

            Spacer_16dp()

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ModernPaymentSummaryRow(label = "Items Subtotal", value = "$Ruppes${details.subtotal}")

                val discountVal = details.discountValue.replace(",", "").toDoubleOrNull() ?: 0.0
                if (discountVal > 0.0) {
                    ModernPaymentSummaryRow(
                        label = "Coupon Savings",
                        value = "- $Ruppes${details.discountValue}",
                        valueColor = Color(0xFF10B981)
                    )
                }

                ModernPaymentSummaryRow(label = "Taxes & Charges", value = "$Ruppes${details.taxAmount}")

                val deliveryVal = details.deliveryCharges.replace(",", "").toDoubleOrNull() ?: 0.0
                ModernPaymentSummaryRow(
                    label = "Delivery Fee",
                    value = if (deliveryVal <= 0.0) "FREE" else "$Ruppes${details.deliveryCharges}",
                    valueColor = if (deliveryVal <= 0.0) Color(0xFF10B981) else Color(0xFF334155)
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Amount to Pay",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            fontFamily = Montserrat
                        )
                        Text(
                            text = "Inclusive of all taxes",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontFamily = Montserrat
                        )
                    }

                    Text(
                        text = "$Ruppes${details.totalAmount}",
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

@Composable
fun ModernPaymentSummaryRow(
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
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
            fontFamily = Montserrat
        )
    }
}

@Composable
fun OrderPaymentBottomBar(
    totalAmount: String,
    isLoading: Boolean,
    selectedMethod: String,
    onPayClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
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
                    text = "Total Amount",
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
                onClick = onPayClick,
                enabled = !isLoading,
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedMethod == "COD") "Confirm Order" else "Pay Now",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Montserrat
                        )
                    }
                }
            }
        }
    }
}