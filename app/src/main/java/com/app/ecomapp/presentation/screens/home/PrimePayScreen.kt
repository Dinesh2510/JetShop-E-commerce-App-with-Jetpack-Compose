package com.app.ecomapp.presentation.screens.home

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.lifecycleScope
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.placeOrder.PurchasePrimeRequest
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator
import com.app.ecomapp.presentation.components.Plan
import com.app.ecomapp.presentation.components.PrimeSubscriptionScreen
import com.app.ecomapp.presentation.components.Spacer_10dp
import com.app.ecomapp.presentation.components.Spacer_20dp
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.components.TitleSmall
import com.app.ecomapp.presentation.screens.payment.PaymentViewModel
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.Constants.Companion.Ruppes
import com.app.ecomapp.utils.IncludeApp
import com.app.ecomapp.utils.PaymentState
import com.app.ecomapp.utils.UserDataStore
import com.compose.jetshop.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class PrimePayScreen : ComponentActivity(), PaymentResultListener {

    private val paymentViewModel: PaymentViewModel by viewModels()
    private var userId: String = ""
    var razorpayKeyId by mutableStateOf("")


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
        val appName = intent.getStringExtra("appName") ?: ""
        val amount = intent.getStringExtra("amount") ?: "0"
        val email = intent.getStringExtra("email") ?: ""
        val contact = intent.getStringExtra("contact") ?: ""


        // ✅ Log Extracted Data
        Log.d("PaymentScreen", "✅ userId: $userId, orderID: $email, amount: $amount")

        setContent {
            MaterialTheme {
                val verifyPurchasePrimeMembershipState by paymentViewModel.purchasePrimeMembership.collectAsState()
                val paymentState = paymentViewModel.paymentState.value

                var showErrorDialog by remember { mutableStateOf(false) }
                var errorMessage by remember { mutableStateOf("") }
                var successMessage by remember { mutableStateOf("") }
                var showSuccessDialog by remember { mutableStateOf(false) }
                var context = LocalContext.current
                // ✅ Handle Payment Verification Separately
                LaunchedEffect(verifyPurchasePrimeMembershipState) {
                    verifyPurchasePrimeMembershipState?.let { state ->
                        when (state) {
                            is Resource.Success -> {
                                Log.d("PaymentScreen", "✅ Payment Verified Successfully!")
                                val successMessage =
                                    state.data.message
                                Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
                                finishWithResult(true) // ✅ Send success result with order_id
                            }

                            is Resource.Error -> {
                                Log.d("PaymentScreen", "❌ Payment VERIFY ERROR !")

                                val errorMessage =
                                    state.message // "Payment failed. Please try again."
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                finishWithResult(false) // ❌ Send failure result with order_id
                            }

                            else -> {
                                Log.d("PaymentScreen", "❌ Payment ELSE PART VERIFY !")
                            }
                        }
                    }
                }

                // ✅ Handle Payment Status
                when (paymentState) {
                    is PaymentState.Loading -> PaymentLoadingDialog()
                    is PaymentState.Success -> {
                        Log.d("Razorpay", "✅ Payment Successful")
                    }

                    is PaymentState.Failure -> {
                        errorMessage = paymentState.error
                        Log.e("Razorpay", "❌ Payment Failed: $errorMessage")
                        showErrorDialog = true
                    }

                    PaymentState.Idle -> {
                        Log.d("PaymentScreen", "🔄 Starting Payment UI")
                        if (razorpayKeyId.isNotEmpty()) {
                            StartPaymentScreen(
                                appName = appName,
                                razorpayKeyId = razorpayKeyId,
                                amount = amount,
                                email = email,
                                contact = contact,
                                paymentViewModel = paymentViewModel
                            )
                        } else {
                           CenteredCircularProgressIndicator()
                        }

                    }
                }

                // ✅ Show Success Alert
                if (showSuccessDialog) {
                    SuccessDialog(
                        message = successMessage,
                        onDismiss = {
                            showSuccessDialog = false
                            finishWithResult(true) // ✅ Send success result before finishing
                        }
                    )
                }

                // ✅ Show Error Dialog
                if (showErrorDialog) {
                    ErrorDialog(
                        errorMessage = errorMessage,
                        onDismiss = {
                            showErrorDialog = false
                            finishWithResult(false) // ❌ Send failure result before finishing
                        },
                        onRetry = {
                            showErrorDialog = false
                        }
                    )
                }
            }
        }
    }

    private fun finishWithResult(isSuccess: Boolean) {

        Log.d("finishWithResult", "✅:  $isSuccess")

        if (isSuccess){
            Log.d("finishWithResult", "✅:  $isSuccess")

            finish()
        }else{
            finish()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        Toast.makeText(this, "Payment is Successful", Toast.LENGTH_SHORT).show()
        razorpayPaymentId?.let {
            Log.d("Razorpay", "✅ Payment Success: Transaction ID: $it")
            val selectedPlan = paymentViewModel.selectedPlan.value

            val planName = selectedPlan?.name ?: "null"
            val planPrice = selectedPlan?.price ?: 111

            Log.d("Razorpay", "✅ Payment Success: Transaction ID: $it")
            Log.d("Razorpay", "✅ Plan Name: $planName | Plan Price: ₹$planPrice")

            val request = PurchasePrimeRequest(
                user_id = userId.toInt(),
                plan_name = planName,
                plan_price = planPrice,
                amount_paid = planPrice,
                payment_method = "Razorpay",
                transaction_id = razorpayPaymentId,
                transaction_status = "success"
            )

            paymentViewModel.purchasePrimeMembership(request)
        }
    }

    override fun onPaymentError(errorCode: Int, errorDescription: String?) {
        val errorMsg = "Payment failed: Code $errorCode, Response: $errorDescription"
        Log.e("Razorpay", "❌ $errorMsg")
        val selectedPlan = paymentViewModel.selectedPlan.value

        val planName = selectedPlan?.name ?: "null"
        val planPrice = selectedPlan?.price ?: 111
        Log.d("Razorpay", "✅ Plan Name: $planName | Plan Price: ₹$planPrice")

        val request = PurchasePrimeRequest(
            user_id = userId.toInt(),
            plan_name = planName,
            plan_price = planPrice,
            amount_paid = planPrice,
            payment_method = "Razorpay",
            transaction_id = "failed",
            transaction_status = "failed"
        )

        paymentViewModel.purchasePrimeMembership(request)
        Toast.makeText(this, "Payment Failed!", Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartPaymentScreen(
    appName: String,
    razorpayKeyId: String,
    amount: String, // This is the base amount in rupees
    email: String,
    contact: String,
    paymentViewModel: PaymentViewModel
) {
    val context = LocalContext.current as Activity
    var showPrimeSubscribe by remember { mutableStateOf(true) }

    // Calculate amount in paise (amount is in rupees, so multiply by 100 to get paise)
    var payableAmountInPaise by remember {
        mutableStateOf((amount.toDouble() * 100).toInt())
    }

    var selectedPlan by remember { mutableStateOf<Plan?>(null) } // Track selected plan
    Scaffold(
        topBar = {
            TopAppBar(
                title = { TitleSmall(text = "Select Plan") },
                navigationIcon = {
                    IconButton(onClick = { context.finish() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White.copy(alpha = 0.2f),
                    titleContentColor = Color.White
                )
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()), // Make screen scrollable
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                AnimatedVisibility(
                    visible = showPrimeSubscribe,
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { -it })
                ) {
                    PrimeSubscriptionScreen(
                        onPlanSelect = { plan ->
                            if (selectedPlan == plan) {
                                // Deselect the plan and reset to original amount in paise
                                selectedPlan = null
                                payableAmountInPaise =
                                    (amount.toDouble() * 100).toInt() // Reset to default amount (in paise)
                            } else {
                                // Select the new plan and add its price (in paise)
                                selectedPlan = plan
                                payableAmountInPaise =
                                    (amount.toDouble() * 100).toInt() + (plan!!.price * 100) // Convert plan price to paise and add to total
                            }
                        },
                        onNoThanks = {
                            showPrimeSubscribe = false
                        },
                        selectedPlan = selectedPlan // Pass selectedPlan to PrimeSubscriptionScreen
                    )
                }
                paymentViewModel.setSelectedPlan(selectedPlan)
                Spacer_20dp()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .background(Color.White)
                        .border(
                            1.dp,
                            Color.Gray,
                            shape = RoundedCornerShape(12.dp)
                        ) // Border for the entire column
                        .padding(16.dp), // Inner padding for content
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    val payableAmountInRupees = payableAmountInPaise / 100.0

                    // Payment Details Title
                    Text(
                        text = "Payment Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontFamily = Montserrat,
                        modifier = Modifier.padding(bottom = 8.dp) // Padding below the title
                    )

                    // Divider after title
                    IncludeApp().CustomDivider(Modifier.padding(vertical = 16.dp))
                    // OrderSummaryRow("Cart Amount", amount)
                    selectedPlan?.let {
                        OrderSummaryPrimeRow("Selected Plan Name",
                            it.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
                        OrderSummaryPrimeRow("Selected Plan", Ruppes +it.priceInRupees())
                    }
                    OrderSummaryPrimeRow("Final Amount", Ruppes +payableAmountInRupees.toString(), true)
                    IncludeApp().CustomDivider(Modifier.padding(vertical = 16.dp))

                    // Pay Now button
                    Button(
                        onClick = {
                            // Razorpay expects the amount in paise, so we send payableAmountInPaise directly (in paise)
                            if (selectedPlan != null) {
                                paymentViewModel.startPayment(
                                    activity = context,
                                    appName = appName,
                                    orderID = selectedPlan?.name!!,
                                    razorpayKeyId = razorpayKeyId,
                                    amount = payableAmountInPaise.toString(), // Send amount in paise to Razorpay
                                    email = email,
                                    contact = contact
                                )
                            }else{
                                Toast.makeText(context,"Select the plan", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth() // Make the button take full width
                    ) {
                        Text("Pay Now", fontFamily = Montserrat)
                    }
                }
            }
        })
}


@Composable
fun OrderSummaryPrimeRow(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "$label:",
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
            fontFamily = Montserrat
        )
        Text(
            value,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
            fontFamily = Montserrat
        )
    }
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
}