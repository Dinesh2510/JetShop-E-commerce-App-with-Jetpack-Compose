package com.app.ecomapp.presentation.screens.payment

import android.app.Activity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.razorpay.Checkout
import org.json.JSONObject
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.placeOrder.PurchasePrimeRequest
import com.app.ecomapp.data.repository.payment.PaymentRepository
import com.app.ecomapp.presentation.components.Plan
import com.app.ecomapp.utils.PaymentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: PaymentRepository
) : ViewModel() {

    // Separate state for Initiate Payment API
    private val _initiatePayment = MutableStateFlow<Resource<CommonResponse?>?>(null)
    val initiatePayment: StateFlow<Resource<CommonResponse?>?> = _initiatePayment

    // Separate state for Verify Payment API
    private val _verifyPayment = MutableStateFlow<Resource<CommonResponse?>?>(null)
    val verifyPayment: StateFlow<Resource<CommonResponse?>?> = _verifyPayment

    // Separate Payment UI state (Handles Razorpay transaction status)
    private val _paymentState = mutableStateOf<PaymentState>(PaymentState.Idle)
    val paymentState: State<PaymentState> get() = _paymentState
    // Separate state for Verify Payment API
    private val _purchasePrimeMembership = MutableStateFlow<Resource<CommonResponse>?>(null)
    val purchasePrimeMembership: StateFlow<Resource<CommonResponse>?> = _purchasePrimeMembership

    fun purchasePrimeMembership(request: PurchasePrimeRequest) {
        viewModelScope.launch {
            _purchasePrimeMembership.value = Resource.Loading
            repository.purchasePrimeMembership(request).collect { response ->
                _purchasePrimeMembership.value = response
            }
        }
    }

    private val _selectedPlan = mutableStateOf<Plan?>(null)
    val selectedPlan: State<Plan?> = _selectedPlan

    fun setSelectedPlan(plan: Plan?) {
        _selectedPlan.value = plan
    }

    fun initiatePayment(orderId: String, userId: String, paymentMethod: String, amount: Double, currency: String) {
        viewModelScope.launch {
            repository.initiatePayment(orderId, userId, paymentMethod, amount, currency).collect { response ->
                _initiatePayment.value = response
            }
        }
    }

    fun startPayment(
        activity: Activity,
        appName: String,
        orderID: String,
        razorpayKeyId: String,
        amount: String,
        email: String,
        contact: String
    ) {
        _paymentState.value = PaymentState.Loading
        val checkout = Checkout()
        checkout.setKeyID(razorpayKeyId)

        val options = JSONObject().apply {
            put("name", appName)
            put("description", "Payment for Order $orderID")
            put("image", "https://pixeldev.in/assets/img/logo.png")
            put("currency", "INR")
            put("amount", amount)
            put("prefill", JSONObject().apply {
                put("email", email)
                put("contact", contact)
            })
        }

        try {
            checkout.open(activity, options)
        } catch (e: Exception) {
            e.printStackTrace()
            _paymentState.value = PaymentState.Failure("Error in payment: ${e.message}", -1)
        }
    }

    fun verifyPayment(userId: String, orderId: String, transactionId: String, status: String, gatewayResponse: String, planName : String, planPrice: String) {
        viewModelScope.launch {
            _verifyPayment.value = Resource.Loading
            delay(3000) // Show loading for 5 seconds before showing verification result

            repository.verifyPayment(userId, orderId, transactionId, status, gatewayResponse,planName, planPrice).collect { response ->
                _verifyPayment.value = response
            }
        }
    }
}

