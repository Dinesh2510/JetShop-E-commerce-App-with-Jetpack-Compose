package com.app.ecomapp.data.repository.home// data/repository/AuthRepository.kt
import androidx.paging.PagingData
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.ProductResponse
import com.app.ecomapp.data.models.details.ProductDetailsResponse
import com.app.ecomapp.data.models.home.CategoryDetailsResponse
import com.app.ecomapp.data.models.home.HomeResponse
import com.app.ecomapp.data.models.home.ProductData
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    suspend fun getSliderCategoryProduct(user_id: String): Flow<Resource<HomeResponse>>
    suspend fun getProductDetails(userId: String, prodctId: String): Flow<Resource<ProductDetailsResponse>>
    suspend fun addToCart(user_id: String, product_id: String,quantity: String): Flow<Resource<CommonResponse>>
    suspend fun removeFromCart(user_id: String, product_id: String,quantity: String): Flow<Resource<CommonResponse>>
    suspend fun searchProducts(productName: String, rating: String): Flow<Resource<ProductResponse>>
    suspend fun addOrUpdateReview(
        userId: String,
        productId: String,
        rating: Float,
        title: String,
        comment: String?
    ): Flow<Resource<CommonResponse>>
    suspend fun listProductsByBrand(brand_id: String): Flow<Resource<CategoryDetailsResponse>>
    suspend fun listProductsByCategory(category_id: String): Flow<Resource<CategoryDetailsResponse>>
    fun getProducts(): Flow<PagingData<ProductData>>

}