package com.app.ecomapp.data.repository.auth// data/repository/AuthRepository.kt
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.appInfo.AppInfoResponse
import com.app.ecomapp.data.models.auth.RegisterResponse
import com.app.ecomapp.data.models.auth.UserResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface AuthRepository {
    suspend fun registerUser(
        fname: String,
        lname: String,
        email: String,
        phone: String,
        password: String, referred_by: String
    ): Flow<Resource<RegisterResponse>>

    suspend fun verifyOTP(otp: String, email: String): Flow<Resource<RegisterResponse>>
    suspend fun userLogin(email: String, password: String, ): Flow<Resource<RegisterResponse>>
    suspend fun forgotPassword(email: String): Flow<Resource<RegisterResponse>>
    suspend fun resetPassword(
        email: String,
        otp: String,
        password: String, ): Flow<Resource<RegisterResponse>>

    suspend fun getUserData(user_id: String): Flow<Resource<UserResponse>>
    suspend fun updateFcmToken(userId: String, fcmToken: String): Flow<Resource<CommonResponse>>
    suspend fun updateUser(
        fname: String,
        lname: String,
        email: String,
        phone: String,
        user_id: String,
        profile_image: MultipartBody. Part?,
    ): Flow<Resource<CommonResponse>>

    suspend fun getAppInfo(): Flow<Resource<AppInfoResponse>>
}
