package com.app.ecomapp.presentation.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.appInfo.AppInfoResponse
import com.app.ecomapp.data.models.auth.RegisterResponse
import com.app.ecomapp.data.models.auth.UserResponse
import com.app.ecomapp.data.repository.auth.AuthRepository
import com.app.ecomapp.utils.UserDataStore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository, private val dataStoreHelper: UserDataStore
) : ViewModel() {

    private val _registerResponse: MutableStateFlow<Resource<RegisterResponse?>?> = MutableStateFlow(null)
    val registerResponse: StateFlow<Resource<RegisterResponse?>?> get() = _registerResponse

    private val _appInfo = MutableStateFlow<Resource<AppInfoResponse>?>(null)
    val appInfo: StateFlow<Resource<AppInfoResponse>?> = _appInfo

    private val _verifyOTPResponse: MutableStateFlow<Resource<RegisterResponse?>?> = MutableStateFlow(null)
    val verifyOTPResponse: StateFlow<Resource<RegisterResponse?>?> get() = _verifyOTPResponse

    private val _loginResponse: MutableStateFlow<Resource<RegisterResponse?>?> = MutableStateFlow(null)
    val loginResponse: StateFlow<Resource<RegisterResponse?>?> get() = _loginResponse

    private val _profileUpdateResponse: MutableStateFlow<Resource<CommonResponse?>?> = MutableStateFlow(null)
    val profileUpdateResponse: StateFlow<Resource<CommonResponse?>?> get() = _profileUpdateResponse

    private val _forgotPasswordResponse: MutableStateFlow<Resource<RegisterResponse?>?> = MutableStateFlow(null)
    val forgotPasswordResponse: StateFlow<Resource<RegisterResponse?>?> get() = _forgotPasswordResponse

    private val _resetPasswordResponse: MutableStateFlow<Resource<RegisterResponse?>?> = MutableStateFlow(null)
    val resetPasswordResponse: StateFlow<Resource<RegisterResponse?>?> get() = _resetPasswordResponse

    private val _userDataResponse: MutableStateFlow<Resource<UserResponse?>?> = MutableStateFlow(null)
    val userDataResponse: StateFlow<Resource<UserResponse?>?> get() = _userDataResponse


    // Fetch user ID from DataStore as Flow
    val userId: StateFlow<String?> = dataStoreHelper.getValue(UserDataStore.USER_ID)
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    // ✅ Login User
    // Handles user login by collecting response from the repository
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            authRepository.userLogin(email, password).collect { response ->
                _loginResponse.value = response
            }
        }
    }

    // ✅ Login User
    // Handles user login by collecting response from the repository
    fun updateUser(
        userId: String,
        firstName: String,
        lastName: String,
        fullName: String,
        phoneNumber: String,
        email: String,
        profile_image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            authRepository.updateUser(
                fname = firstName,
                lname = lastName,
                email = email,
                phone = phoneNumber,
                user_id = userId,
                profile_image = profile_image
            ).collect { response ->
                _profileUpdateResponse.value = response
            }
        }
    }

    // ✅ Forgot Password
    // Sends a password reset request for the given email
    fun forgotPassword(email: String) {
        viewModelScope.launch {
            authRepository.forgotPassword(email).collect { response ->
                _forgotPasswordResponse.value = response
            }
        }
    }

    // ✅ Reset Password
    // Resets the user password using OTP verification
    fun resetPassword(email: String, otp: String, password: String) {
        viewModelScope.launch {
            authRepository.resetPassword(email, otp, password).collect { response ->
                _resetPasswordResponse.value = response
            }
        }
    }

    // ✅ Register User
    // Registers a new user with the given name, email, phone, and password
    fun registerUser(fname: String,lname: String, email: String, phone: String, password: String, referred_by: String) {
        viewModelScope.launch {
            Log.d("registerUser_", "AuthViewModel: "+referred_by)
            authRepository.registerUser(fname,lname, email, phone, password,referred_by).collect { response ->
                _registerResponse.value = response
            }
        }
    }

    // ✅ Verify OTP
    // Verifies the OTP sent to the user’s email
    fun verifyOTP(otp: String, email: String) {
        viewModelScope.launch {
            authRepository.verifyOTP(otp, email).collect { response ->
                _verifyOTPResponse.value = response
            }
        }
    }

    fun getUserData(user_id: String) {
        viewModelScope.launch {
            authRepository.getUserData(user_id).collect { response ->
                _userDataResponse.value = response

                // ✅ Launch sendUserFCM() separately
                launch {
                    sendUserFCM()
                }
            }
        }
    }


    suspend fun sendUserFCM() {
        userId.collect { id ->
            if (id != null) {
                if (id.isNotEmpty()) {
                    // Fetch FCM Token and update it in API
                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val fcmToken = task.result
                            Log.w("AuthViewModel", "Fetching FCM token:-$fcmToken",)
                            updateFcmToken(id, fcmToken)
                        } else {
                            Log.w("AuthViewModel", "Fetching FCM token failed", task.exception)
                        }
                    }
                }
            }
        }
    }

    //Function to update the FCM token in the API
    private fun updateFcmToken(userId: String, fcmToken: String) {
        viewModelScope.launch {
            authRepository.updateFcmToken(userId, fcmToken).collect { response ->
                when (response) {
                    is Resource.Error -> Log.e(
                        "AuthViewModel",
                        "Error updating FCM token: ${response.message}"
                    )

                    Resource.Loading -> Log.e("AuthViewModel", "LOADING FCM")
                    is Resource.Success -> Log.d("AuthViewModel", "FCM Token Updated Successfully")
                }
            }
        }
    }

    fun fetchAppInfo() {
        viewModelScope.launch {
            authRepository.getAppInfo().collect {
                _appInfo.value = it
            }
        }
    }
}