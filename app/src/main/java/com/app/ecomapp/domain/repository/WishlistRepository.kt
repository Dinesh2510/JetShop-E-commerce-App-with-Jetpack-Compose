package com.app.ecomapp.domain.repository

import com.app.ecomapp.data.local.WishlistDao
import com.app.ecomapp.data.models.entity.WishlistProduct
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WishlistRepository @Inject constructor(private val wishlistDao: WishlistDao) {

    fun getWishlist(): Flow<List<WishlistProduct>> = wishlistDao.getWishlist()

    fun isProductInWishlist(productId: String): Flow<Boolean> =
        wishlistDao.isProductInWishlist(productId)

    suspend fun addToWishlist(product: WishlistProduct) {
        wishlistDao.addToWishlist(product)
    }

    suspend fun removeFromWishlist(product: WishlistProduct) {
        wishlistDao.removeFromWishlist(product)
    }
    suspend fun clearWishlist() {
        wishlistDao.clearWishlist()
    }

}
