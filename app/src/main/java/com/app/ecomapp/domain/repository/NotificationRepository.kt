package com.app.ecomapp.domain.repository

import com.app.ecomapp.data.local.NotificationDao
import com.app.ecomapp.data.models.entity.NotificationEntity
import javax.inject.Inject

class NotificationRepository @Inject constructor(private val notificationDao: NotificationDao) {
    fun getNotifications() = notificationDao.getNotifications()

    suspend fun insertNotification(notification: NotificationEntity) {
        notificationDao.insertNotification(notification)
    }

    suspend fun markAsRead(notificationId: Int) {
        notificationDao.markAsRead(notificationId)
    }
}
