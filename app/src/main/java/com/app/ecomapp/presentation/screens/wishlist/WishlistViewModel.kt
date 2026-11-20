package com.app.ecomapp.presentation.screens.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecomapp.data.models.entity.WishlistProduct
import com.app.ecomapp.domain.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(private val repository: WishlistRepository) : ViewModel() {

    private val _wishlist = MutableStateFlow<List<WishlistProduct>>(emptyList())
    val wishlist = _wishlist.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getWishlist().collect {
                _wishlist.value = it
            }
        }
    }

    fun addToWishlist(product: WishlistProduct) {
        viewModelScope.launch {
            repository.addToWishlist(product)
        }
    }

    fun removeFromWishlist(product: WishlistProduct) {
        viewModelScope.launch {
            repository.removeFromWishlist(product)
        }
    }

    fun isProductInWishlist(productId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.isProductInWishlist(productId).collect { isInWishlist ->
                callback(isInWishlist)
            }
        }
    }
    fun clearWishlist() {
        viewModelScope.launch {
            repository.clearWishlist()
        }
    }

}
