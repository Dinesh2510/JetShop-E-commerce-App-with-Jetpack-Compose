package com.app.ecomapp.presentation.screens.profile.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecomapp.data.models.entity.NotificationEntity
import com.app.ecomapp.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private val repository: NotificationRepository) : ViewModel() {

    val notifications = repository.getNotifications().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    fun insertNotification(notification: NotificationEntity) {
        viewModelScope.launch {
            repository.insertNotification(notification)
        }
    }
    fun insertDummyNotifications() {
        viewModelScope.launch {
            val dummyNotifications = listOf(
                NotificationEntity(title = "Order Shipped", message = "Your order #1234 has been shipped!", timestamp = System.currentTimeMillis(), imageUrl = ""),
                NotificationEntity(title = "New Offer!", message = "Get 20% off on your next purchase!", timestamp = System.currentTimeMillis(), imageUrl = ""),
                NotificationEntity(title = "Payment Successful", message = "Your payment of ₹999 was successful.", timestamp = System.currentTimeMillis(), isRead = true, imageUrl = "")
            )
            dummyNotifications.forEach { repository.insertNotification(it) }
        }
    }
    fun markAsRead(notificationId: Int) {
        viewModelScope.launch {
            repository.markAsRead(notificationId)
        }
    }
}
