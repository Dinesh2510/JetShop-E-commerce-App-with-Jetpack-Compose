package com.app.ecomapp.presentation.screens.home

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.ProductResponse
import com.app.ecomapp.data.models.cart.CartResponse
import com.app.ecomapp.data.models.details.ProductDetailsResponse
import com.app.ecomapp.data.models.home.CategoryDetailsResponse
import com.app.ecomapp.data.models.home.HomeResponse
import com.app.ecomapp.data.repository.cart.CartRepository
import com.app.ecomapp.data.repository.home.HomeRepository
import com.app.ecomapp.utils.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val cartRepository: CartRepository,
    private val dataStoreHelper: UserDataStore
) : ViewModel() {

    private val _homeResponse: MutableStateFlow<Resource<HomeResponse?>?> = MutableStateFlow(null)
    val homeResponse: StateFlow<Resource<HomeResponse?>?> get() = _homeResponse

    private val _cartResponse = MutableStateFlow<Resource<CartResponse?>>(Resource.Loading)
    val cartResponse: StateFlow<Resource<CartResponse?>> = _cartResponse

    private val _addToCartResponse: MutableStateFlow<Resource<CommonResponse?>?> =
        MutableStateFlow(null)
    val addToCartResponse: StateFlow<Resource<CommonResponse?>?> get() = _addToCartResponse

    private val _removeToCartResponse: MutableStateFlow<Resource<CommonResponse?>?> =
        MutableStateFlow(null)
    val removeToCartResponse: StateFlow<Resource<CommonResponse?>?> get() = _removeToCartResponse

    private val _addReview: MutableStateFlow<Resource<CommonResponse?>?> = MutableStateFlow(null)
    val addReview: StateFlow<Resource<CommonResponse?>?> get() = _addReview

    // StateFlow to manage the search response state (either success, loading, or error)
    private val _searchResponse = MutableStateFlow<Resource<ProductResponse>?>(null)
    val searchResponse: StateFlow<Resource<ProductResponse>?> get() = _searchResponse

    private val _listOfProductByCategory =
        MutableStateFlow<Resource<CategoryDetailsResponse?>>(Resource.Loading)
    val listOfProductByCategory: StateFlow<Resource<CategoryDetailsResponse?>> =
        _listOfProductByCategory

    private val _listOfProductByBrand =
        MutableStateFlow<Resource<CategoryDetailsResponse?>>(Resource.Loading)
    val listOfProductByBrand: StateFlow<Resource<CategoryDetailsResponse?>> = _listOfProductByBrand

    private val _productDetails: MutableStateFlow<Resource<ProductDetailsResponse?>?> =
        MutableStateFlow(null)
    val productDetails: StateFlow<Resource<ProductDetailsResponse?>?> get() = _productDetails

    private val _cartQuantities = MutableStateFlow(mutableMapOf<String, Int>())
    val cartQuantities: StateFlow<Map<String, Int>> get() = _cartQuantities


    // Fetch user ID from DataStore as Flow
    val userId: StateFlow<String?> = dataStoreHelper.getValue(UserDataStore.USER_ID)
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    // Fetch username from DataStore as Flow
    val userName: StateFlow<String?> = dataStoreHelper.getValue(UserDataStore.USER_FIRST_NAME)
        .stateIn(viewModelScope, SharingStarted.Lazily, "")


    // Fetch username from DataStore as Flow
    val userEmail: StateFlow<String?> = dataStoreHelper.getValue(UserDataStore.USER_EMAIL)
        .stateIn(viewModelScope, SharingStarted.Lazily, "")


    // Fetch username from DataStore as Flow
    val userPhone: StateFlow<String?> = dataStoreHelper.getValue(UserDataStore.USER_PHONE)
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    // Fetch username from DataStore as Flow
    val userIsPrimeActive: StateFlow<String?> = dataStoreHelper.getValue(UserDataStore.isUserPrimeActive)
        .stateIn(viewModelScope, SharingStarted.Lazily, "")


    fun updateCartQuantity(productId: String, quantity: Int) {
        _cartQuantities.value = _cartQuantities.value.toMutableMap().apply {
            this[productId] = quantity
        }
    }

    fun resetCart() {
        _cartQuantities.value = mutableMapOf()
    }

    // ✅ get all categories Listing
    fun getSliderCategoryProducts() {
        viewModelScope.launch {
            userId.collect { id ->
                if (id != null) {
                    if (id.isNotEmpty()) {
                        homeRepository.getSliderCategoryProduct(id).collect { response ->
                            _homeResponse.value = response
                        }
                    } else {
                        homeRepository.getSliderCategoryProduct("").collect { response ->
                            _homeResponse.value = response
                        }
                    }
                }
            }
        }
    }


    // ✅ get all categories Listing
    fun getProductDetails(userId: String, productId: String) {
        Log.d("TAG_", "getProductDetails: $userId$productId")
        viewModelScope.launch {
            homeRepository.getProductDetails(userId, productId).collect { response ->
                _productDetails.value = response
            }
        }
    }

    fun getListProductsByBrand(
        brand_id: String,
    ) {
        viewModelScope.launch {
            homeRepository.listProductsByBrand(brand_id)
                .collect { response ->
                    _listOfProductByBrand.value = response
                }
        }
    }

    fun getListProductsByCategory(
        category_id: String,
    ) {
        viewModelScope.launch {
            homeRepository.listProductsByCategory(category_id)
                .collect { response ->
                    _listOfProductByCategory.value = response
                }
        }
    }

    val productList = homeRepository.getProducts()
        .cachedIn(viewModelScope)

    fun AddUpdateReview(
        productId: String, rating: Float, title: String, comment: String?
    ) {
        viewModelScope.launch {
            userId.collect { id ->
                if (id != null) {
                    homeRepository.addOrUpdateReview(id, productId, rating, title, comment)
                        .collect { response ->
                            _addReview.value = response
                        }
                }
            }
        }
    }


    // ✅ get all Search Listing
    fun searchProducts(productName: String, rating: String) {
        // Emit loading state before making the network request
        _searchResponse.value = Resource.Loading

        viewModelScope.launch {
            try {
                // Collect the response from the repository which is a Flow<Resource<ProductResponse>>
                homeRepository.searchProducts(productName, rating).collect { resource ->
                    _searchResponse.value = resource
                }
            } catch (e: Exception) {
                // Emit error if an exception occurs
                _searchResponse.value = Resource.Error("Failed to load products")
            }
        }
    }

    /** THIS CODE FOR CARTSCREEN*/

    // ✅ Fetch Cart List
    fun getMyCartList() {
        viewModelScope.launch {
            userId.collect { id ->
                if (id != null) {
                    if (id.isNotEmpty()) {
                        cartRepository.getCartList(id).collect { response ->
                            _cartResponse.value = response
                        }
                    }
                }
            }
        }
    }


    // ✅ Delete from Cart
    fun deleteFromCart(cartId: String) {
        viewModelScope.launch {
            cartRepository.deleteCart(cartId).collect { response ->
                if (response is Resource.Success) {
                    getMyCartList() // ✅ Refresh cart after removing
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
                        homeRepository.addToCart(id, productId, quantity).collect { response ->
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
                        homeRepository.removeFromCart(id, productId, "1").collect { response ->
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


    init {
        //   getSliderCategoryProducts()
        getMyCartList()
    }
}