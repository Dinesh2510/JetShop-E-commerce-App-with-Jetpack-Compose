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
import com.app.ecomapp.utils.Constants
import com.app.ecomapp.utils.IncludeApp
import com.app.ecomapp.utils.PaymentState
import com.app.ecomapp.utils.UserDataStore
import com.compose.jetshop.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecomapp.presentation.components.Spacer_16dp
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.Constants.Companion.Ruppes

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
                title = {
                    TitleSmall(
                        text = "Select Plan",
                        color = Color.Black
                    )
                },
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

// INSIDE StartPaymentScreen replace the payment details Column with this:

                val payableAmountInRupees = payableAmountInPaise / 100.0

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // --- Header Section ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEFF6FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ReceiptLong,
                                        contentDescription = null,
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Payment Summary",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Montserrat,
                                    color = Color(0xFF0F172A)
                                )
                            }

                            if (selectedPlan != null) {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFFECFDF5),
                                    border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                                ) {
                                    Text(
                                        text = "Plan Selected",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF059669),
                                        fontFamily = Montserrat
                                    )
                                }
                            }
                        }

                        Spacer_16dp()
                        HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F5F9))
                        Spacer_16dp()

                        // --- Content Section ---
                        if (selectedPlan != null) {
                            val formattedPlanName = selectedPlan?.name
                                ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                                .orEmpty()

                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                // Plan Name Row
                                SummaryRow(
                                    label = "Membership Plan",
                                    value = formattedPlanName,
                                    highlightValue = true
                                )

                                // Plan Base Price
                                SummaryRow(
                                    label = "Plan Price",
                                    value = "$Ruppes${selectedPlan?.priceInRupees()}"
                                )

                                // Convenience / Processing Fee
                                SummaryRow(
                                    label = "Processing Fee",
                                    value = "FREE",
                                    isFree = true
                                )

                                Spacer_8dp()

                                // Total Highlight Box
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Total Payable",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E293B),
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
                                            text = "$Ruppes${String.format(Locale.getDefault(), "%.2f", payableAmountInRupees)}",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF2563EB),
                                            fontFamily = Montserrat
                                        )
                                    }
                                }
                            }
                        } else {
                            // Empty / Select Plan Prompt
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .padding(vertical = 20.dp, horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "👈 Please select a Prime Plan above",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF64748B),
                                        fontFamily = Montserrat,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Choose a membership to proceed with payment",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8),
                                        fontFamily = Montserrat,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer_16dp()

                        // --- Pay Button ---
                        Button(
                            onClick = {
                                val currentPlan = selectedPlan
                                if (currentPlan != null) {
                                    paymentViewModel.startPayment(
                                        activity = context,
                                        appName = appName,
                                        orderID = currentPlan.name,
                                        razorpayKeyId = razorpayKeyId,
                                        amount = payableAmountInPaise.toString(),
                                        email = email,
                                        contact = contact
                                    )
                                } else {
                                    Toast.makeText(context, "Please select a plan to continue", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = selectedPlan != null,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2563EB),
                                disabledContainerColor = Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (selectedPlan != null) Color.White else Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (selectedPlan != null) "Proceed to Pay $Ruppes${payableAmountInRupees.toInt()}" else "Select a Plan",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = Montserrat
                                )
                            }
                        }

                        // Trust badge
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "100% Safe & Secure Payments via Razorpay",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B),
                                fontFamily = Montserrat
                            )
                        }
                    }
                }
            }
        })
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    highlightValue: Boolean = false,
    isFree: Boolean = false
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
            fontFamily = Montserrat,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = if (highlightValue) FontWeight.SemiBold else FontWeight.Medium,
            color = when {
                isFree -> Color(0xFF059669) // Green for FREE
                highlightValue -> Color(0xFF0F172A)
                else -> Color(0xFF334155)
            },
            fontFamily = Montserrat
        )
    }
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