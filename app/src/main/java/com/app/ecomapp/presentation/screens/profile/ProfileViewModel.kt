package com.app.ecomapp.presentation.screens.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.address.AddressRequest
import com.app.ecomapp.data.models.address.AddressUpdateRequest
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.coupon.CouponsResponse
import com.app.ecomapp.data.models.address.AddressResponse
import com.app.ecomapp.data.models.blogs.BlogData
import com.app.ecomapp.data.models.blogs.BlogResponse
import com.app.ecomapp.data.repository.profile.ProfileRepository
import com.app.ecomapp.utils.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val dataStoreHelper: UserDataStore
) : ViewModel() {

    private val _couponsResponse: MutableStateFlow<Resource<CouponsResponse?>?> =
        MutableStateFlow(null)
    val couponsResponse: StateFlow<Resource<CouponsResponse?>?> get() = _couponsResponse

    // Fetch user ID from DataStore as Flow
    val userId: StateFlow<String?> = dataStoreHelper.getValue(UserDataStore.USER_ID)
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    // Fetch username from DataStore as Flow
    val userName: StateFlow<String?> = dataStoreHelper.getValue(UserDataStore.USER_NAME)
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    private val _addressResponse = MutableStateFlow<Resource<CommonResponse>?>(null)
    val addressResponse: StateFlow<Resource<CommonResponse>?> = _addressResponse

    private val _addressesList: MutableStateFlow<Resource<AddressResponse?>?> =
        MutableStateFlow(null)
    val addressesList: StateFlow<Resource<AddressResponse?>?> get() = _addressesList

    private val _blogList = MutableStateFlow<Resource<BlogResponse>?>(null)
    val blogList: StateFlow<Resource<BlogResponse>?> get() = _blogList

    private val _selectedBlog = MutableStateFlow<BlogData?>(null)
    val selectedBlog: StateFlow<BlogData?> = _selectedBlog

    fun setSelectedBlog(blog: BlogData) {
        Log.d("TAGViewModel", "setSelectedBlog: $blog")
        _selectedBlog.value = blog
    }

    fun fetchBlogs() {
        viewModelScope.launch {
            profileRepository.getBlogs().collect { result ->
                _blogList.value = result
            }
        }
    }

    fun addAddress(addressRequest: AddressRequest) {
        viewModelScope.launch {
            _addressResponse.value = Resource.Loading
            _addressResponse.value = profileRepository.addAddress(addressRequest)
            getAddresses()
        }
    }

    fun updateAddress(addressUpdateRequest: AddressUpdateRequest) {
        viewModelScope.launch {
            _addressResponse.value = Resource.Loading
            _addressResponse.value = profileRepository.updateAddress(addressUpdateRequest)
            getAddresses()
        }
    }

    fun getCouponCode() {
        viewModelScope.launch {
            profileRepository.getCouponCode().collect { response ->
                _couponsResponse.value = response
            }
        }
    }

    fun getAddresses() {
        viewModelScope.launch {
            val currentId = if (!userId.value.isNullOrEmpty()) userId.value else dataStoreHelper.getValue(UserDataStore.USER_ID).first()
            if (!currentId.isNullOrEmpty()) {
                profileRepository.getAddresses(currentId).collect { response ->
                    _addressesList.value = response
                }
            }
        }
    }

    // Function to delete address and refresh list immediately
    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            val currentId = if (!userId.value.isNullOrEmpty()) userId.value else dataStoreHelper.getValue(UserDataStore.USER_ID).first()
            if (!currentId.isNullOrEmpty()) {
                profileRepository.deleteAddress(currentId, addressId).collect { response ->
                    if (response is Resource.Success) {
                        getAddresses()
                    } else if (response is Resource.Error) {
                        Log.e("ProfileViewModel", "Error deleting address: ${response.message}")
                    }
                }
            }
        }
    }

    // Function to set default address and refresh list immediately
    fun setDefaultAddress(addressId: String) {
        viewModelScope.launch {
            val currentId = if (!userId.value.isNullOrEmpty()) userId.value else dataStoreHelper.getValue(UserDataStore.USER_ID).first()
            if (!currentId.isNullOrEmpty()) {
                profileRepository.setDefaultAddress(currentId, addressId).collect { response ->
                    if (response is Resource.Success) {
                        getAddresses()
                    } else if (response is Resource.Error) {
                        Log.e("ProfileViewModel", "Error setting default address: ${response.message}")
                    }
                }
            }
        }
    }
}