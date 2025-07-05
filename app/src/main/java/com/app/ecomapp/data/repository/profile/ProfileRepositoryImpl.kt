package com.app.ecomapp.data.repository.profile// data/repository/AuthRepositoryImpl.kt
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.address.AddressRequest
import com.app.ecomapp.data.models.address.AddressUpdateRequest
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.coupon.CouponsResponse
import com.app.ecomapp.data.models.address.AddressResponse
import com.app.ecomapp.data.models.blogs.BlogResponse
import com.app.ecomapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class ProfileRepositoryImpl @Inject constructor(private val apiService: ApiService) : ProfileRepository {

    override suspend fun getCouponCode(): Flow<Resource<CouponsResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response =
                    apiService.getCouponCode()
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
    override suspend fun addAddress(addressRequest: AddressRequest): Resource<CommonResponse> {
        return try {
            val response = apiService.addAddress(
                userId = addressRequest.userId,
                address = addressRequest.address,
                city = addressRequest.city,
                state = addressRequest.state,
                zipCode = addressRequest.zip_code,
                country = addressRequest.country,
                latitude = addressRequest.latitude.toString(),
                longitude = addressRequest.longitude.toString(),
                type = addressRequest.type,
                google_address = addressRequest.google_address,
                default_address	 = addressRequest.default_address
            )
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Something went wrong")
        }
    }

    override suspend fun updateAddress(addressUpdateRequest: AddressUpdateRequest): Resource<CommonResponse> {
        return try {
            val response = apiService.updateAddress(
                addressId = addressUpdateRequest.addressId,
                userId = addressUpdateRequest.userId,
                address = addressUpdateRequest.address,
                city = addressUpdateRequest.city,
                state = addressUpdateRequest.state,
                zipCode = addressUpdateRequest.zip_code,
                country = addressUpdateRequest.country,
                latitude = addressUpdateRequest.latitude.toString(),
                longitude = addressUpdateRequest.longitude.toString(),
                google_address = addressUpdateRequest.google_address,
                type = addressUpdateRequest.type
            )
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Something went wrong")
        }
    }

    override suspend fun getAddresses(userId: String): Flow<Resource<AddressResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response =
                    apiService.listAddresses(userId)
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

    override suspend fun deleteAddress(
        userId: String,
        address_id: String
    ): Flow<Resource<CommonResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response =
                    apiService.deleteAddress(address_id,userId)
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

    override suspend fun setDefaultAddress(
        userId: String,
        address_id: String
    ): Flow<Resource<CommonResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response =
                    apiService.setDefaultAddress(address_id,userId)
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
    override suspend fun getBlogs(): Flow<Resource<BlogResponse>> = flow {
        try {
            emit(Resource.Loading)
            val response = apiService.getBlogs()
            if (response.status == "success") {
                emit(Resource.Success(response))
            } else {
                emit(Resource.Error(response.status ?: "Error occurred"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Network error"))
        }
    }
}
