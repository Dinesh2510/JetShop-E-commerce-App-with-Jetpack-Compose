package com.app.ecomapp.presentation.screens.home

import android.os.SystemClock
import android.util.Log
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

typealias CartMap = Map<String, Int>

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val cartRepository: CartRepository,
    private val dataStoreHelper: UserDataStore
) : ViewModel() {

    companion object {
        private const val TAG = "HOME_DEBUG"
        private const val REFRESH_COOLDOWN_MS = 60_000L // 60 seconds
    }

    private var lastFetchTimestamp = 0L
    private var isFetchingHome = false

    private val _cooldownMessage = MutableSharedFlow<String>()
    val cooldownMessage: SharedFlow<String> = _cooldownMessage.asSharedFlow()

    private val _homeResponse = MutableStateFlow<Resource<HomeResponse?>?>(Resource.Loading)
    val homeResponse: StateFlow<Resource<HomeResponse?>?> = _homeResponse.asStateFlow()

    private val _cartResponse = MutableStateFlow<Resource<CartResponse?>>(Resource.Loading)
    val cartResponse: StateFlow<Resource<CartResponse?>> = _cartResponse.asStateFlow()

    private val _addToCartResponse = MutableStateFlow<Resource<CommonResponse?>?>(null)
    val addToCartResponse: StateFlow<Resource<CommonResponse?>?> = _addToCartResponse.asStateFlow()

    private val _removeToCartResponse = MutableStateFlow<Resource<CommonResponse?>?>(null)
    val removeToCartResponse: StateFlow<Resource<CommonResponse?>?> = _removeToCartResponse.asStateFlow()

    private val _addReview = MutableStateFlow<Resource<CommonResponse?>?>(null)
    val addReview: StateFlow<Resource<CommonResponse?>?> = _addReview.asStateFlow()

    private val _searchResponse = MutableStateFlow<Resource<ProductResponse>?>(null)
    val searchResponse: StateFlow<Resource<ProductResponse>?> = _searchResponse.asStateFlow()

    private val _listOfProductByCategory =
        MutableStateFlow<Resource<CategoryDetailsResponse?>>(Resource.Loading)
    val listOfProductByCategory: StateFlow<Resource<CategoryDetailsResponse?>> =
        _listOfProductByCategory.asStateFlow()

    private val _listOfProductByBrand =
        MutableStateFlow<Resource<CategoryDetailsResponse?>>(Resource.Loading)
    val listOfProductByBrand: StateFlow<Resource<CategoryDetailsResponse?>> =
        _listOfProductByBrand.asStateFlow()

    private val _productDetails = MutableStateFlow<Resource<ProductDetailsResponse?>?>(null)
    val productDetails: StateFlow<Resource<ProductDetailsResponse?>?> = _productDetails.asStateFlow()

    private val _cartQuantities = MutableStateFlow<CartMap>(emptyMap())
    val cartQuantities: StateFlow<CartMap> = _cartQuantities.asStateFlow()

    val userId: StateFlow<String?> = dataStoreHelper.getValue(UserDataStore.USER_ID)
        .catch { e ->
            Log.e(TAG, "Error reading USER_ID from DataStore", e)
            emit("")
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val userName: StateFlow<String?> = dataStoreHelper.getValue(UserDataStore.USER_FIRST_NAME)
        .catch { emit("") }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val userEmail: StateFlow<String?> = dataStoreHelper.getValue(UserDataStore.USER_EMAIL)
        .catch { emit("") }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val userPhone: StateFlow<String?> = dataStoreHelper.getValue(UserDataStore.USER_PHONE)
        .catch { emit("") }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val userIsPrimeActive: StateFlow<String?> =
        dataStoreHelper.getValue(UserDataStore.isUserPrimeActive)
            .catch { emit("") }
            .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val userProfile = dataStoreHelper.getUserData
        .catch { e ->
            Log.e(TAG, "Error reading userProfile from DataStore", e)
            emit(null)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    private var homeFetchJob: Job? = null

    init {
        Log.d(TAG, "HomeViewModel initialized -> triggering initial fetch")
        getSliderCategoryProducts()
        getMyCartList()
    }

    private suspend fun getSafeUserId(): String {
        val cached = userId.value
        if (!cached.isNullOrEmpty()) return cached

        val fromStore = withTimeoutOrNull(1500L) {
            try {
                dataStoreHelper.getValue(UserDataStore.USER_ID).firstOrNull()
            } catch (e: Exception) {
                Log.e(TAG, "getSafeUserId exception: ${e.message}")
                ""
            }
        }
        return fromStore ?: ""
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        _cartQuantities.value = _cartQuantities.value.toMutableMap().apply {
            this[productId] = quantity
        }
    }

    fun resetCart() {
        _cartQuantities.value = emptyMap()
    }

    // 1. Add dedicated refresh state flow
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun getSliderCategoryProducts(isPullToRefresh: Boolean = false) {
        val now = SystemClock.elapsedRealtime()

        // 1. Cooldown check
        if (isPullToRefresh && (now - lastFetchTimestamp < REFRESH_COOLDOWN_MS)) {
            viewModelScope.launch {
                _isRefreshing.value = true
                delay(600)
                _isRefreshing.value = false
                _cooldownMessage.emit("Products are up to date.")
            }
            return
        }

        // 2. Prevent concurrent duplicate calls
        if (isFetchingHome) return

        homeFetchJob?.cancel()
        homeFetchJob = viewModelScope.launch {
            isFetchingHome = true
            if (isPullToRefresh) {
                _isRefreshing.value = true
            } else if (_homeResponse.value !is Resource.Success) {
                _homeResponse.value = Resource.Loading
            }

            try {
                val currentUserId = getSafeUserId()
                homeRepository.getSliderCategoryProduct(currentUserId)
                    .catch { e ->
                        Log.e(TAG, "Flow exception in getSliderCategoryProduct: ${e.message}", e)
                        emit(Resource.Error(e.localizedMessage ?: "Failed to load products"))
                    }
                    .collect { response ->
                        _homeResponse.value = response
                        if (response is Resource.Success) {
                            lastFetchTimestamp = SystemClock.elapsedRealtime()
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Unhandled exception: ${e.message}", e)
                _homeResponse.value = Resource.Error(e.localizedMessage ?: "Failed to load products")
            } finally {
                // ALWAYS reset both flags here
                isFetchingHome = false
                _isRefreshing.value = false
            }
        }
    }
    fun getProductDetails(userId: String, productId: String) {
        Log.d(TAG, "getProductDetails: userId=$userId, productId=$productId")
        viewModelScope.launch {
            try {
                homeRepository.getProductDetails(userId, productId).collect { response ->
                    _productDetails.value = response
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching product details", e)
                _productDetails.value = Resource.Error(e.localizedMessage ?: "Failed to load details")
            }
        }
    }

    fun getListProductsByBrand(brand_id: String) {
        viewModelScope.launch {
            try {
                homeRepository.listProductsByBrand(brand_id).collect { response ->
                    _listOfProductByBrand.value = response
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching brand products", e)
                _listOfProductByBrand.value = Resource.Error(e.localizedMessage ?: "Failed to load brand products")
            }
        }
    }

    fun getListProductsByCategory(category_id: String) {
        viewModelScope.launch {
            try {
                homeRepository.listProductsByCategory(category_id).collect { response ->
                    _listOfProductByCategory.value = response
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching category products", e)
                _listOfProductByCategory.value = Resource.Error(e.localizedMessage ?: "Failed to load category products")
            }
        }
    }

    val productList = homeRepository.getProducts()
        .cachedIn(viewModelScope)

    fun AddUpdateReview(
        productId: String,
        rating: Float,
        title: String,
        comment: String?
    ) {
        viewModelScope.launch {
            try {
                val currentUserId = getSafeUserId()
                if (currentUserId.isNotEmpty()) {
                    homeRepository.addOrUpdateReview(currentUserId, productId, rating, title, comment)
                        .collect { response ->
                            _addReview.value = response
                        }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding review", e)
                _addReview.value = Resource.Error(e.localizedMessage ?: "Failed to submit review")
            }
        }
    }

    fun searchProducts(productName: String, rating: String) {
        _searchResponse.value = Resource.Loading
        viewModelScope.launch {
            try {
                homeRepository.searchProducts(productName, rating).collect { resource ->
                    _searchResponse.value = resource
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error searching products", e)
                _searchResponse.value = Resource.Error(e.localizedMessage ?: "Failed to load products")
            }
        }
    }

    fun getMyCartList() {
        viewModelScope.launch {
            try {
                val currentUserId = getSafeUserId()
                if (currentUserId.isNotEmpty()) {
                    Log.d(TAG, "Fetching Cart List for logged-in userId=$currentUserId")
                    cartRepository.getCartList(currentUserId).collect { response ->
                        _cartResponse.value = response
                    }
                } else {
                    Log.d(TAG, "Skipping Cart List fetch (User is Guest/Logged Out)")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading cart", e)
            }
        }
    }

    fun deleteFromCart(cartId: String) {
        viewModelScope.launch {
            try {
                cartRepository.deleteCart(cartId).collect { response ->
                    if (response is Resource.Success) {
                        getMyCartList()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting cart item", e)
            }
        }
    }

    fun addToCart(productId: String, quantity: String) {
        viewModelScope.launch {
            try {
                val currentUserId = getSafeUserId()
                if (currentUserId.isNotEmpty()) {
                    homeRepository.addToCart(currentUserId, productId, quantity).collect { response ->
                        if (response is Resource.Success) {
                            getMyCartList()
                            _addToCartResponse.value = response
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding item to cart", e)
            }
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            try {
                val currentUserId = getSafeUserId()
                if (currentUserId.isNotEmpty()) {
                    homeRepository.removeFromCart(currentUserId, productId, "1").collect { response ->
                        if (response is Resource.Success) {
                            getMyCartList()
                            _removeToCartResponse.value = response
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error removing item from cart", e)
            }
        }
    }
}