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
import com.app.ecomapp.data.models.blogs.BlogResponse
import com.app.ecomapp.data.models.refer.ReferResponse
import com.app.ecomapp.data.models.wallet.WalletHistoryResponse
import com.app.ecomapp.data.repository.refer.ReferRepository
import com.app.ecomapp.domain.repository.NotificationRepository
import com.app.ecomapp.utils.UserDataStore
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReferViewModel @Inject constructor(
    private val application: Application,
    private val repository: ReferRepository,
    private val dataStoreHelper: UserDataStore

) : ViewModel() {
    // Fetch user ID from DataStore as Flow
    val userId: StateFlow<String?> = dataStoreHelper.getValue(UserDataStore.USER_ID)
        .stateIn(viewModelScope, SharingStarted.Lazily, "")

    private val _referList = MutableStateFlow<Resource<ReferResponse>?>(null)
    val referList: StateFlow<Resource<ReferResponse>?> get() = _referList

    private val _getWalletHistory = MutableStateFlow<Resource<WalletHistoryResponse>?>(null)
    val getWalletHistory: StateFlow<Resource<WalletHistoryResponse>?> get() = _getWalletHistory

    private val _selectedContacts = mutableStateListOf<ContactUiModel>()
    val selectedContacts: List<ContactUiModel> = _selectedContacts

    fun toggleContactSelection(contact: ContactUiModel) {
        if (_selectedContacts.contains(contact)) {
            _selectedContacts.remove(contact)
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
        val inviteJsonString = gson.toJson(inviteContacts) // This is your final JSON string
        Log.d("InviteJson", inviteJsonString)

        // Send to server
        Log.d("Invite", inviteContacts.toString())
    }

    private val _contactPager = MutableStateFlow<PagingData<ContactUiModel>>(PagingData.empty())
    val contactPager: StateFlow<PagingData<ContactUiModel>> = _contactPager

    fun loadContacts() {
        viewModelScope.launch {
            Pager(PagingConfig(pageSize = 20)) {
                ContactsPagingSource(application)
            }.flow
                .cachedIn(viewModelScope)
                .collectLatest {
                    _contactPager.value = it
                }
        }
    }

    fun fetchRefer() {

        viewModelScope.launch {
            userId.collect { id ->
                if (id != null) {
                    if (id.isNotEmpty()) {
                        repository.getReferralHistory(id).collect { result ->
                            _referList.value = result
                        }
                    }
                }
            }
        }
    }
    fun fetchWalletHistory() {

        viewModelScope.launch {
            userId.collect { id ->
                if (id != null) {
                    if (id.isNotEmpty()) {
                        repository.getWalletHistory(id).collect { result ->
                            _getWalletHistory.value = result
                        }
                    }
                }
            }
        }
    }

    /* val contactPager = Pager(PagingConfig(pageSize = 20)) {
         ContactsPagingSource(application)
     }.flow.cachedIn(viewModelScope)*/
}
