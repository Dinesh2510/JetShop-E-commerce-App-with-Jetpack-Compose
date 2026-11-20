package com.app.ecomapp.data.local

import androidx.room.*
import com.app.ecomapp.data.models.entity.WishlistProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWishlist(product: WishlistProduct)

    @Delete
    suspend fun removeFromWishlist(product: WishlistProduct)

    @Query("SELECT * FROM wishlist")
    fun getWishlist(): Flow<List<WishlistProduct>>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist WHERE product_id = :productId)")
    fun isProductInWishlist(productId: String): Flow<Boolean>

    @Query("DELETE FROM wishlist")
    suspend fun clearWishlist()

}
