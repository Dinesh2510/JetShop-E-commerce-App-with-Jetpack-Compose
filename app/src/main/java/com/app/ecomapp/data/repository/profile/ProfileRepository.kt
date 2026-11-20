package com.app.ecomapp.data.repository.profile// data/repository/AuthRepository.kt
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.address.AddressRequest
import com.app.ecomapp.data.models.address.AddressUpdateRequest
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.coupon.CouponsResponse
import com.app.ecomapp.data.models.address.AddressResponse
import com.app.ecomapp.data.models.blogs.BlogResponse
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getCouponCode(): Flow<Resource<CouponsResponse>>
    suspend fun addAddress(addressRequest: AddressRequest): Resource<CommonResponse>
    suspend fun updateAddress(addressUpdateRequest: AddressUpdateRequest): Resource<CommonResponse>
    suspend fun getAddresses(userId: String):Flow< Resource<AddressResponse>>
    suspend fun deleteAddress(userId: String, address_id: String):Flow< Resource<CommonResponse>>
    suspend fun setDefaultAddress(userId: String, address_id: String):Flow< Resource<CommonResponse>>
    suspend fun getBlogs():Flow< Resource<BlogResponse>>
}