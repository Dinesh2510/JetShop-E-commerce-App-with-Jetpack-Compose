package com.app.ecomapp.presentation.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.cart.CartResponse
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.checkout.OrderDetailsResponse
import com.app.ecomapp.data.repository.cart.CartRepository
import com.app.ecomapp.utils.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: CartRepository, private val dataStoreHelper: UserDataStore
) : ViewModel() {


    private val _addToCartResponse: MutableStateFlow<Resource<CommonResponse?>?> =
        MutableStateFlow(null)
    val addToCartResponse: StateFlow<Resource<CommonResponse?>?> get() = _addToCartResponse

    private val _removeToCartResponse: MutableStateFlow<Resource<CommonResponse?>?> =
        MutableStateFlow(null)
    val removeToCartResponse: StateFlow<Resource<CommonResponse?>?> get() = _removeToCartResponse

    // Fetch user ID from DataStore as Flow
    val userId: StateFlow<String?> = dataStoreHelper.getValue(UserDataStore.USER_ID)
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    // Fetch username from DataStore as Flow
    val userName: StateFlow<String?> = dataStoreHelper.getValue(UserDataStore.USER_NAME)
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    private val _cartResponse = MutableStateFlow<Resource<CartResponse?>>(Resource.Loading)
    val cartResponse: StateFlow<Resource<CartResponse?>> = _cartResponse

    private val _orderDetails = MutableStateFlow<Resource<OrderDetailsResponse>?>(null)
    val orderDetails: StateFlow<Resource<OrderDetailsResponse>?> get() = _orderDetails

    fun fetchOrderDetails(couponCode: String?) {
        viewModelScope.launch {
            userId.collect { id ->
                if (id != null) {
                    if (couponCode != null) {
                        repository.getOrderDetails(id, couponCode).collect { response ->
                            _orderDetails.value = response
                        }
                    }
                }
            }
        }
    }

    // ✅ Fetch Cart List
    fun getMyCartList() {
        viewModelScope.launch {
            userId.collect { id ->
                if (id != null) {
                    if (id.isNotEmpty()) {
                        repository.getCartList(id).collect { response ->
                            _cartResponse.value = response
                        }
                    }
                }
            }
        }
    }

    // ✅ Add to Cart
    fun addToCart(productId: String, quantity: String) {
        viewModelScope.launch {
            userId.collect { id ->
                if (id != null) {
                    if (id.isNotEmpty()) { // Ensure userId is available before API call
                        repository.addToCart(id, productId, quantity).collect { response ->
                            if (response is Resource.Success) {
                                getMyCartList() // ✅ Refresh cart after adding
                                _addToCartResponse.value = response
                            }

                        }
                    }
                }
            }
        }
    }

    // ✅ Remove from Cart
    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            userId.collect { id ->
                if (id != null) {
                    if (id.isNotEmpty()) {
                        repository.removeFromCart(id, productId, "1")
                            .collect { response ->
                                if (response is Resource.Success) {
                                    getMyCartList() // ✅ Refresh cart after removing
                                    _removeToCartResponse.value = response

                                }
                            }
                    }
                }
            }
        }
    }

    // ✅ Delete from Cart
    fun deleteFromCart(cartId: String) {
        viewModelScope.launch {
            repository.deleteCart(cartId)
                .collect { response ->
                    if (response is Resource.Success) {
                        getMyCartList() // ✅ Refresh cart after removing
                    }
                }
        }
    }


}