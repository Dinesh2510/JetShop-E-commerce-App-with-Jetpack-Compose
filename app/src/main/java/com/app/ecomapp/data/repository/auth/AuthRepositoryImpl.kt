package com.app.ecomapp.data.repository.auth// data/repository/AuthRepositoryImpl.kt
import android.util.Log
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.appInfo.AppInfoResponse
import com.app.ecomapp.data.models.auth.RegisterRequest
import com.app.ecomapp.data.models.auth.RegisterResponse
import com.app.ecomapp.data.models.auth.UserResponse
import com.app.ecomapp.data.remote.ApiService
import com.app.ecomapp.utils.CommonFunction.toRequestBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(private val apiService: ApiService) : AuthRepository {

    override suspend fun registerUser(
        fname: String, lname: String,
        email: String,
        phone: String,
        password: String, referred_by: String
    ): Flow<Resource<RegisterResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                Log.d("registerUser_", "AuthRepositoryImpl: "+referred_by)
                val response =
                    apiService.registerUser(RegisterRequest(fname, lname, email, phone, password,referred_by))
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

    override suspend fun verifyOTP(otp: String, email: String): Flow<Resource<RegisterResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response = apiService.verifyOtp(email, otp)
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

    override suspend fun userLogin(
        email: String,
        password: String
    ): Flow<Resource<RegisterResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response = apiService.userLogin(email, password)
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

    override suspend fun forgotPassword(email: String): Flow<Resource<RegisterResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response = apiService.forgotPassword(email)
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

    override suspend fun resetPassword(
        email: String,
        otp: String,
        password: String
    ): Flow<Resource<RegisterResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response = apiService.resetPassword(email, otp, password)
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

    override suspend fun getUserData(user_id: String): Flow<Resource<UserResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response = apiService.getUserData(user_id)
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

    override suspend fun updateFcmToken(
        userId: String,
        fcmToken: String
    ): Flow<Resource<CommonResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response = apiService.updateFcmToken(userId, fcmToken)
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

    override suspend fun updateUser(
        fname: String,
        lname: String,
        email: String,
        phone: String,
        user_id: String,
        profile_image: MultipartBody.Part?
    ): Flow<Resource<CommonResponse>> {
        return flow {
            try {
                // Emit loading state
                emit(Resource.Loading)

                // Call the API method to update user data
                val response = apiService.updateUser(
                    userId = user_id.toRequestBody(),
                    fname = fname.toRequestBody(),
                    lname = lname.toRequestBody(),
                    email = email.toRequestBody(),
                    phone = phone.toRequestBody(),
                    profile_image= profile_image
                )

                // Check response status and emit success or error
                if (response.status == "error") {
                    emit(Resource.Error(response.message ?: "Error occurred"))
                } else {
                    emit(Resource.Success(response))
                }
            } catch (e: Exception) {
                // Emit error in case of failure
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }
    override suspend fun getAppInfo(): Flow<Resource<AppInfoResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getAppInfo()
            if (response.status) {
                emit(Resource.Success(response))
            } else {
                emit(Resource.Error(response.message ?: "Something went wrong"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Network error"))
        }
    }

}