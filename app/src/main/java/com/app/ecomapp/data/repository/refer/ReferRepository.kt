package com.app.ecomapp.data.repository.refer// data/repository/AuthRepository.kt
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.address.AddressRequest
import com.app.ecomapp.data.models.address.AddressUpdateRequest
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.coupon.CouponsResponse
import com.app.ecomapp.data.models.address.AddressResponse
import com.app.ecomapp.data.models.blogs.BlogResponse
import com.app.ecomapp.data.models.refer.ReferResponse
import com.app.ecomapp.data.models.wallet.WalletHistoryResponse
import kotlinx.coroutines.flow.Flow

interface ReferRepository {
    suspend fun getReferralHistory(userId: String):Flow< Resource<ReferResponse>>
    suspend fun getWalletHistory(userId: String):Flow< Resource<WalletHistoryResponse>>
}