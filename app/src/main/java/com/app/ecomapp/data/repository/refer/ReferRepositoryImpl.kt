package com.app.ecomapp.data.repository.refer// data/repository/AuthRepositoryImpl.kt
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.address.AddressRequest
import com.app.ecomapp.data.models.address.AddressUpdateRequest
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.coupon.CouponsResponse
import com.app.ecomapp.data.models.address.AddressResponse
import com.app.ecomapp.data.models.blogs.BlogResponse
import com.app.ecomapp.data.models.refer.ReferResponse
import com.app.ecomapp.data.models.wallet.WalletHistoryResponse
import com.app.ecomapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class ReferRepositoryImpl @Inject constructor(private val apiService: ApiService) :
    ReferRepository {


    override suspend fun getReferralHistory(userId: String): Flow<Resource<ReferResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response =
                    apiService.getReferralHistory(userId)
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

    override suspend fun getWalletHistory(userId: String): Flow<Resource<WalletHistoryResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response =
                    apiService.getWalletHistory(userId)
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
}
