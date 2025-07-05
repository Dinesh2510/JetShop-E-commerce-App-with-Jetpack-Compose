package com.app.ecomapp.data.repository.home// data/repository/AuthRepositoryImpl.kt
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.ProductResponse
import com.app.ecomapp.data.models.details.ProductDetailsResponse
import com.app.ecomapp.data.models.home.CategoryDetailsResponse
import com.app.ecomapp.data.models.home.HomeResponse
import com.app.ecomapp.data.models.home.ProductData
import com.app.ecomapp.data.remote.ApiService
import com.app.ecomapp.presentation.screens.home.ProductPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class HomeRepositoryImpl @Inject constructor(private val apiService: ApiService) : HomeRepository {

    override suspend fun getSliderCategoryProduct( user_id: String,
    ): Flow<Resource<HomeResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response =
                    apiService.getSliderCategoryProduct(user_id)
                if (response.status == "error") {
                    emit(Resource.Error(response.status ?: "Error occurred"))
                } else {
                    emit(Resource.Success(response))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }


    override suspend fun getProductDetails(
        userId: String,
        prodctId: String,
    ): Flow<Resource<ProductDetailsResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response = apiService.getProductDetails(userId, prodctId)
                if (response.status == "error") {
                    emit(Resource.Error(response.message ?: "Error occurred"))
                } else {
                    emit(Resource.Success(response))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }





    override suspend fun addToCart(
        user_id: String,
        product_id: String,
        quantity: String
    ): Flow<Resource<CommonResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response =
                    apiService.addToCart(user_id, product_id, quantity)
                if (response.status == "error") {
                    emit(Resource.Error(response.status ?: "Error occurred"))
                } else {
                    emit(Resource.Success(response))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }

    override suspend fun removeFromCart(
        user_id: String,
        product_id: String,
        quantity: String
    ): Flow<Resource<CommonResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response =
                    apiService.removeFromCart(user_id, product_id, quantity)
                if (response.status == "error") {
                    emit(Resource.Error(response.status ?: "Error occurred"))
                } else {
                    emit(Resource.Success(response))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }

    override suspend fun searchProducts(
        productName: String,
        rating: String
    ):  Flow<Resource<ProductResponse>>  {
        return flow {
            try {
                emit(Resource.Loading)
                val response =
                    apiService.searchProducts(productName,rating)
                if (response.status == "error") {
                    emit(Resource.Error(response.status ?: "Error occurred"))
                } else {
                    emit(Resource.Success(response))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }

    override suspend fun addOrUpdateReview(
        userId: String,
        productId: String,
        rating: Float,
        title: String,
        comment: String?
    ): Flow<Resource<CommonResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response = apiService.add_update_review(userId, productId, rating, title, comment)
                if (response.status == "error") {
                    emit(Resource.Error(response.status ?: "Error occurred"))
                } else {
                    emit(Resource.Success(response))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }

    override suspend fun listProductsByBrand(brand_id: String): Flow<Resource<CategoryDetailsResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response = apiService.listProductsByBrand(brand_id)
                if (response.error) {
                    emit(Resource.Error(response.error.toString() ?: "Error occurred"))
                } else {
                    emit(Resource.Success(response))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }

    override suspend fun listProductsByCategory(category_id: String): Flow<Resource<CategoryDetailsResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response = apiService.listProductsByCategory(category_id)
                Log.d("listProductsByCategory", "API Response: $response")

                if (response.success) {
                    Log.d("listProductsByCategory", "Emitting Resource.Success")
                    emit(Resource.Success(response))
                } else {
                    Log.d("listProductsByCategory", "Emitting Resource.Error")
                    emit(Resource.Error("Failed to fetch products"))
                }
            } catch (e: Exception) {
                Log.e("listProductsByCategory", "Exception: ${e.localizedMessage}")
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }
    override fun getProducts(): Flow<PagingData<ProductData>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ProductPagingSource(apiService) }
        ).flow
    }
}
