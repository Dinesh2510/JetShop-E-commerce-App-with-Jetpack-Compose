package com.app.ecomapp.presentation.screens.order

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.placeOrder.PlaceOrderDetailsResponse
import com.app.ecomapp.presentation.components.LoadingButton
import com.app.ecomapp.presentation.components.PaymentFailedScreen
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.cart.OrderSummaryRow
import com.app.ecomapp.presentation.screens.cart.OrderSummaryTitleRow
import com.app.ecomapp.presentation.screens.cart.PaymentMethod
import com.app.ecomapp.presentation.screens.payment.PaymentScreen
import com.app.ecomapp.presentation.screens.payment.PaymentViewModel
import com.app.ecomapp.ui.theme.AppMainTypography
import com.app.ecomapp.ui.theme.BlueDarkNew
import com.app.ecomapp.ui.theme.ButtonLight
import com.app.ecomapp.ui.theme.DarkTextPrimary
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.ui.theme.ProductTypography
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
    val hasNavigated = remember { mutableStateOf(false) } // mutableStateOf to enable updates

    // Fetch order details when screen is launched
    LaunchedEffect(Unit) {
        Log.d("OrderScreen", "OrderScreen: ")
        viewModel.getPlaceOrderDetails(order_id, "")
    }

    // Refresh order details only when applyCoupons or removeCoupons succeed
    LaunchedEffect(applyCoupons, removeCoupons) {
        if (applyCoupons is Resource.Success || removeCoupons is Resource.Success) {
            viewModel.getPlaceOrderDetails(order_id, "")
        } else if (applyCoupons is Resource.Error || removeCoupons is Resource.Error) {
            val errorMessage = (applyCoupons as? Resource.Error)?.message
                ?: (removeCoupons as? Resource.Error)?.message
                ?: "Unknown error"

            Toast.makeText(context, "Error in payment: $errorMessage", Toast.LENGTH_SHORT).show()
        }
    }

    HandleApiState(
        apiState = viewModel.orderState,
        showLoader = true,
        navController = navController,
        onSuccess = { data ->
            apiData = data
        }
    ) {
        apiData?.let {
            PaymentPage(paymentViewModel, navController, it, viewModel, order_id, hasNavigated)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaymentPage(
    paymentViewModel: PaymentViewModel,
    navController: NavController,
    apiData: PlaceOrderDetailsResponse?,
    viewModel: OrderViewModel,
    order_id: String,
    hasNavigated: MutableState<Boolean> // It will be a MutableState now

) {
    var selectedPaymentMethod by remember { mutableStateOf("") }
    var couponCode by remember { mutableStateOf("") }
    var appliedCoupon by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current as Activity
    val userDataStore = UserDataStore(context)
    var paymentApiData by remember { mutableStateOf<CommonResponse?>(null) }
    val initiatePaymentState by paymentViewModel.initiatePayment.collectAsState()
    val hasNavigated = remember { mutableStateOf(false) } // mutableStateOf to enable updates

    // ✅ Get user_id
    val userId by userDataStore.getValue(UserDataStore.USER_ID).collectAsState(initial = "")
    val userEmail by userDataStore.getValue(UserDataStore.USER_EMAIL).collectAsState(initial = "")
    val userPhone by userDataStore.getValue(UserDataStore.USER_PHONE).collectAsState(initial = "")
    val paymentFailed = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

    // Payment result launcher
    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val orderId = result.data?.getStringExtra("order_id") ?: ""
            Log.d("OrderScreen", "✅ Payment Successful for Order ID: $orderId")
            navController.navigate("success_screen/$orderId")
        } else {
            Log.e("OrderScreen", "❌ Payment Failed")
            paymentFailed.value = true // Trigger failure state
            errorMessage.value = "Payment failed due to some error." // Set the error message
        }
    }

    // Your other composable content here, such as a payment form


    // Update appliedCoupon when API response updates
    LaunchedEffect(apiData) {
        appliedCoupon = apiData?.orderDetails?.couponCode
    }
    // Handle payment state change
    LaunchedEffect(initiatePaymentState) {
        if (initiatePaymentState is Resource.Success) {
            val data = (initiatePaymentState as Resource.Success).data
            if (data?.paymentStatus == "COD_CONFIRMED" && !hasNavigated.value) {  // Access the value with .value
                navController.navigate("success_screen/${order_id}") {
                    popUpTo(0) { inclusive = true }
                }
                // Set the flag to true to prevent further navigation
                hasNavigated.value = true  // Update the state here
            }
        }
    }
    val paymentMethods = listOf(
        PaymentMethod("COD", "COD", R.drawable.cod),
        PaymentMethod("RAZORPAY", "RazorPay", R.drawable.razor_pay_icon)
    )
    val amount = apiData?.orderDetails?.totalAmount?.replace(
        ",",
        ""
    )
    val amountInPaise = apiData?.orderDetails?.totalAmount
        ?.replace(",", "")  // Remove commas
        ?.toDoubleOrNull()  // Convert to Double (to handle decimals)
        ?.times(100)        // Convert to paise
        ?.toInt()           // Convert to Integer (required by Razorpay)

    val email = userEmail
    val contact = userPhone
    val razorpayKeyId = stringResource(id = R.string.razorpay_key_id)
    val appName = stringResource(id = R.string.app_name)
    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            topBar = {
                ToolbarWithBackButtonAndTitle(
                    title = "Order Details",
                    onBackClick = { navController.popBackStack() }
                )
            },
            modifier = Modifier.fillMaxSize(),
            content = { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(bottom = 100.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    // Coupon Code Section
                    item {
                        Spacer(Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(8.dp)
                            ,colors = CardDefaults.cardColors(Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = couponCode.uppercase(Locale.getDefault()),
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
                                    Button(
                                        onClick = {
                                            if (couponCode.isNotEmpty() && couponCode != appliedCoupon) {
                                                viewModel.applyCoupon(
                                                    order_id = order_id,
                                                    coupon_code = couponCode
                                                )
                                            } else {
                                                Log.d("TAG_Man", "PaymentPage: ERROR")
                                            }
                                        },
                                        enabled = couponCode.isNotEmpty() && couponCode != appliedCoupon
                                    ) {
                                        Text("Apply", fontFamily = Montserrat)
                                    }
                                }

                                // Show applied coupon with remove option
                                appliedCoupon?.let { coupon ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Applied Coupon: $coupon ✅",
                                            color = Color.Black,
                                            fontFamily = Montserrat
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(onClick = {
                                            viewModel.removeCoupon(
                                                order_id = order_id,
                                                coupon_code = coupon
                                            )
                                            appliedCoupon = null
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove Coupon",
                                                tint = Color.Red
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Payment Methods Section
                    item {
                        Spacer(Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Payment Method",
                                    style = AppMainTypography.subHeader
                                )
                                Spacer(Modifier.height(8.dp))

                                FlowRow(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp, bottom = 10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(20.dp),
                                    maxItemsInEachRow = 3
                                ) {
                                    paymentMethods.forEach { method ->
                                        Row(
                                            modifier = Modifier
                                                .clickable { selectedPaymentMethod = method.id }
                                                .background(if (selectedPaymentMethod == method.id) BlueDarkNew else Color.Transparent)
                                                .border(
                                                    width = 2.dp,
                                                    color = if (selectedPaymentMethod == method.id) ButtonLight else Color.Transparent,
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
                                                style = ProductTypography.prodTitleMedium
                                            )
                                        }
                                    }
                                }
                                /* Text(
                                     text = "Payment Method:-$selectedPaymentMethod",
                                     fontWeight = FontWeight.Bold,
                                     fontSize = 18.sp,
                                     fontFamily = Montserrat
                                 )*/
                            }
                        }
                    }

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
                                   style = AppMainTypography.subHeader
                                )
                                Spacer(Modifier.height(8.dp))
                                apiData?.orderDetails?.let { order ->
                                    OrderSummaryTitleRow("Order ID", order.orderId, true)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                    OrderSummaryRow("Subtotal", order.subtotal)
                                    OrderSummaryRow("Discount", order.discountValue)
                                    OrderSummaryRow("Tax", order.taxAmount)
                                    OrderSummaryRow("Delivery Charges", order.deliveryCharges)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                    OrderSummaryRow(
                                        "Final Total",
                                        order.totalAmount,
                                        bold = true
                                    )
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            WindowInsets.navigationBars.asPaddingValues() // ✅ Prevents overlap
                        )
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    when (val response = initiatePaymentState) {
                        is Resource.Loading -> {
                            // CenteredCircularProgressIndicator()
                        }

                        is Resource.Success -> {
                            response.data?.let { data ->
                                Text(
                                    text = data.message.orEmpty(),
                                    color = Color.Green
                                )

                                when (data.paymentStatus) {
                                    /*"COD_CONFIRMED" -> {
                                        *//*val intent = Intent(context, HandleActivity::class.java).apply {
                                        putExtra("navigateTo", "SuccessScreen")
                                    }
                                    context.startActivity(intent)

                                    (context as? Activity)?.finish() // ✅ Ensures the current Activity is removed
*//*
                                    navController.navigate("success_screen/${order_id}") {
                                        popUpTo(0) { inclusive = true } // ✅ Clears the entire backstack
                                    }
                                }*/


                                    "PENDING" -> {
                                        // ✅ Launch Razorpay payment screen with required data
                                        LaunchedEffect(Unit) {
                                            Log.d("TAG_LaunchedEffect", "PaymentPage: " + userId)
                                            val intent =
                                                Intent(context, PaymentScreen::class.java).apply {
                                                    putExtra("user_id", userId)
                                                    putExtra("orderId", order_id)
                                                    putExtra("appName", appName)
                                                    putExtra("amount", amount.toString())
                                                    putExtra("email", email)
                                                    putExtra("contact", contact)
                                                }
                                            // context.startActivity(intent)
                                            activityResultLauncher.launch(intent) // Start activity and wait for result

                                            //navController.popBackStack() // ✅ Removes OrderScreen from backstack
                                        }
                                    }

                                    else -> {
                                        Text("Error: Other Payment", color = Color.Red)

                                    }
                                }
                            }
                        }

                        is Resource.Error -> {
                            Text("Error: ${response.message}", color = Color.Red)
                        }

                        else -> {
                            Text(
                                "Click the button to initiate payment",
                               style = ProductTypography.prodSubtitle
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    LoadingButton(
                        text = "Make Payment",
                        isLoading = initiatePaymentState is Resource.Loading,
                        onClick = {
                            if (selectedPaymentMethod.isNotEmpty()) {
                                if (amountInPaise != null && email != null && contact != null) {
                                    paymentViewModel.initiatePayment(
                                        orderId = order_id,
                                        userId = userId!!,
                                        paymentMethod = selectedPaymentMethod,
                                        amount = amount!!.toDouble(),
                                        currency = "INR"
                                    )
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please select payment method",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
        // Conditionally display the PaymentFailedScreen if payment failed
        if (paymentFailed.value) {
            Log.e("OrderScreen", "❌ Payment SCREEN")

            PaymentFailedScreen(
                navController,
                errorMessage = errorMessage.value,
                onRetryClick = {
                    // Retry logic, reset failure state and potentially retry payment
                    Log.d("PaymentScreen", "Retry clicked")
                    paymentFailed.value = false // Reset failure state
                    // Go back to the previous screen
                    navController.popBackStack()
                },
                onGoHomeClick = {
                    paymentFailed.value = false // Reset failure state
                    // Optionally, initiate payment process again
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) { inclusive = true }
                    }

                }
            )
        }
    }
}
