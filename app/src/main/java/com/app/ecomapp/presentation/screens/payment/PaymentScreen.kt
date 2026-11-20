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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.lifecycleScope
import com.app.ecomapp.data.Resource
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator
import com.app.ecomapp.presentation.components.Plan
import com.app.ecomapp.presentation.components.PrimeSubscriptionScreen
import com.app.ecomapp.presentation.components.Spacer_10dp
import com.app.ecomapp.presentation.components.Spacer_16dp
import com.app.ecomapp.presentation.components.Spacer_20dp
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.components.TitleSmall
import com.app.ecomapp.presentation.screens.home.HomeViewModel
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.Constants.Companion.Ruppes
import com.app.ecomapp.utils.PaymentState
import com.app.ecomapp.utils.UserDataStore
import com.compose.jetshop.R
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class PaymentScreen : ComponentActivity(), PaymentResultListener {

    private val paymentViewModel: PaymentViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private var orderID: String = ""
    private var userId: String = ""
    private var selectedPlan: Plan? = null

    private var razorpayKeyId by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataStoreHelper = UserDataStore(applicationContext)

        lifecycleScope.launch {
            try {
                val appInfo = dataStoreHelper.getAppInfoObject()
                appInfo?.let {
                    if (it.razorpay_key.isNotEmpty()) {
                        razorpayKeyId = it.razorpay_key
                        Log.d("RAZOR_KEY", "Loaded key: $razorpayKeyId")
                    }
                } ?: Log.e("RAZOR_KEY", "AppInfo is null")
            } catch (e: Exception) {
                Log.e("RAZOR_KEY", "Error loading Razorpay key: ${e.message}")
            }
        }

        // Extracting Intent Data
        userId = intent.getStringExtra("user_id") ?: ""
        orderID = intent.getStringExtra("orderId") ?: ""
        val appName = intent.getStringExtra("appName") ?: ""
        val amount = intent.getStringExtra("amount") ?: "0"
        val email = intent.getStringExtra("email") ?: ""
        val contact = intent.getStringExtra("contact") ?: ""

        Log.d("PaymentScreen", "✅ userId: $userId, orderID: $orderID, amount: $amount")

        setContent {
            MaterialTheme {
                val fallbackKey = stringResource(id = R.string.razorpay_key_id)
                val effectiveKey = if (razorpayKeyId.isNotEmpty()) razorpayKeyId else fallbackKey

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
                                val err = state.message ?: "Verification Error"
                                Log.e("PaymentScreen", "❌ Verification Error: $err")
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
                            razorpayKeyId = effectiveKey,
                            amount = amount,
                            email = email,
                            contact = contact,
                            paymentViewModel = paymentViewModel,
                            homeViewModel = homeViewModel,
                            onPlanSelected = {
                                selectedPlan = it
                                Log.d("SelectedPlan", "Plan selected: ${it?.name} ₹${it?.price}")
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
        Log.d("finishWithResult", "✅ orderID: $orderID result: $isSuccess")
        finish()
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        Toast.makeText(this, "Payment is Successful", Toast.LENGTH_SHORT).show()
        razorpayPaymentId?.let {
            Log.d("Razorpay", "✅ Payment Success: Transaction ID: $it")
            Log.d("PlanInfo", "Selected plan: ${selectedPlan?.name} ₹${selectedPlan?.price}")

            paymentViewModel.verifyPayment(
                userId = userId,
                orderId = orderID,
                transactionId = it,
                status = "SUCCESS",
                gatewayResponse = it,
                planName = selectedPlan?.name ?: "",
                planPrice = (selectedPlan?.price ?: "0").toString()
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
            planName = selectedPlan?.name ?: "",
            planPrice = (selectedPlan?.price ?: 0).toString()
        )

        Toast.makeText(this, "Payment Failed!", Toast.LENGTH_SHORT).show()
    }
}

// -------------------------------------------------------------
// UI SCREEN & COMPONENTS
// -------------------------------------------------------------

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
    onPlanSelected: (Plan?) -> Unit,
) {
    val context = LocalContext.current as Activity
    var showPrimeSubscribe by remember { mutableStateOf(true) }

    val baseAmountInPaise = remember(amount) {
        ((amount.toDoubleOrNull() ?: 0.0) * 100).toInt()
    }

    var payableAmountInPaise by remember(amount) {
        mutableIntStateOf(baseAmountInPaise)
    }

    var selectedPlan by remember { mutableStateOf<Plan?>(null) }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = { TitleSmall(text = "Payment", color = Color(0xFF0F172A)) },
                navigationIcon = {
                    IconButton(onClick = { context.finish() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .navigationBarsPadding()
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
                                    payableAmountInPaise = baseAmountInPaise
                                    null
                                } else {
                                    onPlanSelected(plan)
                                    val planPricePaise = ((plan?.price.toString().toDoubleOrNull() ?: 0.0) * 100).toInt()
                                    payableAmountInPaise = baseAmountInPaise + planPricePaise
                                    plan
                                }
                            },
                            onNoThanks = { showPrimeSubscribe = false },
                            selectedPlan = selectedPlan
                        )
                    }
                }

                Spacer_20dp()

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
                                    color = Color(0xFFFEF3C7),
                                    border = BorderStroke(1.dp, Color(0xFFFDE68A))
                                ) {
                                    Text(
                                        text = "👑 Prime Included",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFB45309),
                                        fontFamily = Montserrat
                                    )
                                }
                            }
                        }

                        Spacer_16dp()
                        HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F5F9))
                        Spacer_16dp()

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            PaymentSummaryLineItem(
                                label = "Cart Items Total",
                                value = "$Ruppes$amount"
                            )

                            selectedPlan?.let { plan ->
                                val formattedPlanName = plan.name.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                                }
                                PaymentSummaryLineItem(
                                    label = "Prime Membership ($formattedPlanName)",
                                    value = "+ $Ruppes${plan.priceInRupees()}",
                                    highlightValue = true,
                                    valueColor = Color(0xFF2563EB)
                                )
                            }

                            PaymentSummaryLineItem(
                                label = "Convenience / Processing Fee",
                                value = "FREE",
                                valueColor = Color(0xFF059669)
                            )

                            Spacer_8dp()

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
                                            text = "Total Amount",
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

                        Spacer_16dp()

                        Button(
                            onClick = {
                                Log.d("PaymentScreen", "Initiating Razorpay with key: $razorpayKeyId, amount: $payableAmountInPaise")
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
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2563EB)
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
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Pay $Ruppes${payableAmountInRupees.toInt()}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = Montserrat
                                )
                            }
                        }

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
                                modifier = Modifier.size(13.dp)
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

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    )
}

@Composable
fun PaymentSummaryLineItem(
    label: String,
    value: String,
    highlightValue: Boolean = false,
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
            fontFamily = Montserrat,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = if (highlightValue) FontWeight.SemiBold else FontWeight.Medium,
            color = valueColor,
            fontFamily = Montserrat
        )
    }
}

@Composable
fun PaymentLoadingDialog() {
    Dialog(onDismissRequest = { }) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.width(260.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CenteredCircularProgressIndicator()
                Spacer_16dp()
                Text(
                    text = "Verifying Payment...",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    fontFamily = Montserrat
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Please do not press back or close the app",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    fontFamily = Montserrat
                )
            }
        }
    }
}

@Composable
fun SuccessDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Success", fontFamily = Montserrat, fontWeight = FontWeight.Bold) },
        text = { Text(message, fontFamily = Montserrat) },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Text("OK", fontFamily = Montserrat)
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
            Text(text = "Payment Error", fontWeight = FontWeight.Bold, fontFamily = Montserrat)
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "❌ $errorMessage", color = Color(0xFFEF4444), fontFamily = Montserrat)
            }
        },
        confirmButton = {
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text("Retry Payment", fontFamily = Montserrat)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontFamily = Montserrat, color = Color(0xFF64748B))
            }
        }
    )
}