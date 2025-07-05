package com.app.ecomapp.presentation.screens.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.razorpay.PaymentResultListener
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.lifecycleScope
import com.app.ecomapp.data.Resource
import com.app.ecomapp.presentation.components.PrimeSubscribe
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator
import com.app.ecomapp.presentation.components.Plan
import com.app.ecomapp.presentation.components.PrimeSubscriptionScreen
import com.app.ecomapp.presentation.components.Spacer_10dp
import com.app.ecomapp.presentation.components.Spacer_20dp
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.components.TitleSmall
import com.app.ecomapp.presentation.screens.cart.OrderSummaryRow
import com.app.ecomapp.presentation.screens.home.HomeViewModel
import com.app.ecomapp.ui.theme.AppMainTypography
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.IncludeApp
import com.app.ecomapp.utils.NetworkImage
import com.app.ecomapp.utils.PaymentState
import com.app.ecomapp.utils.UserDataStore
import com.compose.jetshop.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
@AndroidEntryPoint
class PaymentScreen : ComponentActivity(), PaymentResultListener {

    private val paymentViewModel: PaymentViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private var orderID: String = ""
    private var userId: String = ""
    private var selectedPlan: Plan? = null
    var razorpayKeyId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataStoreHelper = UserDataStore(applicationContext)

        lifecycleScope.launch {
            try {
                val appInfo = dataStoreHelper.getAppInfoObject()
                appInfo?.let {
                    razorpayKeyId = it.razorpay_key
                    Log.d("RAZOR_KEY", "Loaded key: $razorpayKeyId")
                } ?: Log.e("RAZOR_KEY", "AppInfo is null")
            } catch (e: Exception) {
                Log.e("RAZOR_KEY", "Error loading Razorpay key: ${e.message}")
            }
        }

        // ✅ Extracting Intent Data
        userId = intent.getStringExtra("user_id") ?: ""
        orderID = intent.getStringExtra("orderId") ?: ""
        val appName = intent.getStringExtra("appName") ?: ""
        val amount = intent.getStringExtra("amount") ?: "0"
        val email = intent.getStringExtra("email") ?: ""
        val contact = intent.getStringExtra("contact") ?: ""

        Log.d("PaymentScreen", "✅ userId: $userId, orderID: $orderID, amount: $amount")

        setContent {
            MaterialTheme {
                val verifyPaymentState by paymentViewModel.verifyPayment.collectAsState()
                val paymentState = paymentViewModel.paymentState.value

                var showErrorDialog by remember { mutableStateOf(false) }
                var errorMessage by remember { mutableStateOf("") }

                LaunchedEffect(verifyPaymentState) {
                    verifyPaymentState?.let { state ->
                        when (state) {
                            is Resource.Success -> {
                                Log.d("PaymentScreen", "✅ Payment Verified Successfully!")
                                finishWithResult(true)
                            }

                            is Resource.Error -> {
                                val errorMessage = state.message
                                Log.e("PaymentScreen", "❌ Verification Error: $errorMessage")
                                finishWithResult(false)
                            }

                            else -> {}
                        }
                    }
                }

                when (paymentState) {
                    is PaymentState.Loading -> PaymentLoadingDialog()
                    is PaymentState.Success -> Log.d("Razorpay", "✅ Payment Successful")
                    is PaymentState.Failure -> {
                        errorMessage = paymentState.error
                        showErrorDialog = true
                    }

                    PaymentState.Idle -> {
                        StartPaymentScreen(
                            orderID = orderID,
                            appName = appName,
                            razorpayKeyId = razorpayKeyId,
                            amount = amount,
                            email = email,
                            contact = contact,
                            paymentViewModel = paymentViewModel,
                            homeViewModel = homeViewModel,
                            onPlanSelected = {
                                selectedPlan = it
                                Log.d("SelectedPlan", "Plan selected: ${it?.name} - ₹${it?.price}")
                            }
                        )
                    }
                }

                if (showErrorDialog) {
                    ErrorDialog(
                        errorMessage = errorMessage,
                        onDismiss = {
                            showErrorDialog = false
                            finishWithResult(false)
                        },
                        onRetry = { showErrorDialog = false }
                    )
                }
            }
        }
    }

    private fun finishWithResult(isSuccess: Boolean) {
        val resultIntent = Intent().apply {
            putExtra("order_id", orderID)
        }
        setResult(if (isSuccess) Activity.RESULT_OK else Activity.RESULT_CANCELED, resultIntent)
        Log.d("finishWithResult", "✅: orderID: $orderID $isSuccess")
        finish()
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        Toast.makeText(this, "Payment is Successful", Toast.LENGTH_SHORT).show()
        razorpayPaymentId?.let {
            Log.d("Razorpay", "✅ Payment Success: Transaction ID: $it")

            Log.d("PlanInfo", "Selected plan: ${selectedPlan?.name} - ₹${selectedPlan?.price}")

            paymentViewModel.verifyPayment(
                userId = userId,
                orderId = orderID,
                transactionId = it,
                status = "SUCCESS",
                gatewayResponse = it,
                planName = selectedPlan?.name ?: "",           // ✅ Default empty if null
                planPrice = (selectedPlan?.price ?: "0").toString()           // ✅ Default 0 if null
            )
        }
    }


    override fun onPaymentError(errorCode: Int, errorDescription: String?) {
        val errorMsg = "Payment failed: Code $errorCode, Response: $errorDescription"
        Log.e("Razorpay", "❌ $errorMsg")

        paymentViewModel.verifyPayment(
            userId = userId,
            orderId = orderID,
            transactionId = "FAILED",
            status = "FAILED",
            gatewayResponse = errorMsg,
            planName = selectedPlan?.name ?: "",           // ✅ Default empty
            planPrice = (selectedPlan?.price ?: 0).toString()           // ✅ Default 0
        )

        Toast.makeText(this, "Payment Failed!", Toast.LENGTH_SHORT).show()
    }



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartPaymentScreen(
    orderID: String,
    appName: String,
    razorpayKeyId: String,
    amount: String,
    email: String,
    contact: String,
    paymentViewModel: PaymentViewModel,
    homeViewModel: HomeViewModel,
    onPlanSelected: (Plan?) -> Unit, // ✅ Callback to send plan
) {
    val context = LocalContext.current as Activity
    var showPrimeSubscribe by remember { mutableStateOf(true) }

    var payableAmountInPaise by remember {
        mutableStateOf((amount.toDouble() * 100).toInt())
    }

    var selectedPlan by remember { mutableStateOf<Plan?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { TitleSmall(text = "Payment") },
                navigationIcon = {
                    IconButton(onClick = { context.finish() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val userIsPrimeActive by homeViewModel.userIsPrimeActive.collectAsState()
                if (userIsPrimeActive != "1") {
                    AnimatedVisibility(
                        visible = showPrimeSubscribe,
                        exit = fadeOut() + slideOutVertically { -it }
                    ) {
                        PrimeSubscriptionScreen(
                            onPlanSelect = { plan ->
                                selectedPlan = if (selectedPlan == plan) {
                                    onPlanSelected(null)
                                    payableAmountInPaise = (amount.toDouble() * 100).toInt()
                                    null
                                } else {
                                    onPlanSelected(plan)
                                    payableAmountInPaise =
                                        (amount.toDouble() * 100).toInt() + (plan!!.price * 100)
                                    plan
                                }
                            },
                            onNoThanks = { showPrimeSubscribe = false },
                            selectedPlan = selectedPlan
                        )
                    }
                }

                Spacer_20dp()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .background(Color.White)
                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val payableAmountInRupees = payableAmountInPaise / 100.0

                    Text(
                        text = "Payment Details",
                        style = AppMainTypography.subHeader,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    IncludeApp().CustomDivider(Modifier.padding(vertical = 16.dp))
                    OrderSummaryRow("Cart Amount", amount)
                    selectedPlan?.let {
                        OrderSummaryRow("Selected Plan", it.priceInRupees())
                    }
                    OrderSummaryRow("Final Amount", payableAmountInRupees.toString(), true)
                    IncludeApp().CustomDivider(Modifier.padding(vertical = 16.dp))

                    Button(
                        onClick = {
                            paymentViewModel.startPayment(
                                activity = context,
                                appName = appName,
                                orderID = orderID,
                                razorpayKeyId = razorpayKeyId,
                                amount = payableAmountInPaise.toString(),
                                email = email,
                                contact = contact
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Pay Now", style = AppMainTypography.labelLarge)
                    }
                }
            }
        }
    )
}



@Composable
fun PaymentLoadingDialog() {
    Dialog(onDismissRequest = { }) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CenteredCircularProgressIndicator()
                Spacer_10dp()
                Text(
                    text = "Verifying Payment...",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun SuccessDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Success") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}


@Composable
fun ErrorDialog(
    errorMessage: String,
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Payment Error", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "❌ $errorMessage", color = Color.Red)
            }
        },
        confirmButton = {
            Button(onClick = onRetry) {
                Text("Retry Payment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}}