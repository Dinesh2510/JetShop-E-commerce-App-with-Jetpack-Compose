package com.app.ecomapp.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ConnectivityViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    val isOnline: StateFlow<Boolean> = NetworkConnectivityObserver(context)
        .isOnline
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true) // true = no flash at startup
}