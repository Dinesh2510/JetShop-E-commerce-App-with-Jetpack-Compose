package com.app.ecomapp.presentation.screens.profile.referral

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.refer.ReferResponse
import com.app.ecomapp.data.models.wallet.WalletHistoryResponse
import com.app.ecomapp.data.repository.refer.ReferRepository
import com.app.ecomapp.utils.UserDataStore
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReferViewModel @Inject constructor(
    private val application: Application,
    private val repository: ReferRepository,
    private val dataStoreHelper: UserDataStore
) : ViewModel() {

    val userId: StateFlow<String?> = dataStoreHelper.getValue(UserDataStore.USER_ID)
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    private val _referList = MutableStateFlow<Resource<ReferResponse>?>(null)
    val referList: StateFlow<Resource<ReferResponse>?> get() = _referList

    private val _getWalletHistory = MutableStateFlow<Resource<WalletHistoryResponse>?>(null)
    val getWalletHistory: StateFlow<Resource<WalletHistoryResponse>?> get() = _getWalletHistory

    private val _selectedContacts = mutableStateListOf<ContactUiModel>()
    val selectedContacts: List<ContactUiModel> = _selectedContacts

    // Clean, pre-configured Paged Flow
    val contactPager: Flow<PagingData<ContactUiModel>> = Pager(
        config = PagingConfig(
            pageSize = 30,
            prefetchDistance = 10,
            enablePlaceholders = false
        )
    ) {
        ContactsPagingSource(application)
    }.flow.cachedIn(viewModelScope)

    fun toggleContactSelection(contact: ContactUiModel) {
        if (_selectedContacts.any { it.number == contact.number }) {
            _selectedContacts.removeAll { it.number == contact.number }
        } else {
            _selectedContacts.add(contact)
        }
    }

    data class InviteContact(
        val first_name: String,
        val last_name: String,
        val phone: String
    )

    fun inviteSelectedContacts() {
        val inviteContacts = selectedContacts.map {
            InviteContact(
                first_name = it.firstName,
                last_name = it.lastName,
                phone = it.number
            )
        }

        val gson = Gson()
        val inviteJsonString = gson.toJson(inviteContacts)
        Log.d("InviteJson", inviteJsonString)
    }

    fun fetchRefer() {
        viewModelScope.launch {
            val currentId = if (!userId.value.isNullOrEmpty()) userId.value else dataStoreHelper.getValue(UserDataStore.USER_ID).first()
            if (!currentId.isNullOrEmpty()) {
                repository.getReferralHistory(currentId).collect { result ->
                    _referList.value = result
                }
            }
        }
    }

    fun fetchWalletHistory() {
        viewModelScope.launch {
            val currentId = if (!userId.value.isNullOrEmpty()) userId.value else dataStoreHelper.getValue(UserDataStore.USER_ID).first()
            if (!currentId.isNullOrEmpty()) {
                repository.getWalletHistory(currentId).collect { result ->
                    _getWalletHistory.value = result
                }
            }
        }
    }
}