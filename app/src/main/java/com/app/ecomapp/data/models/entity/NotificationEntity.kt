package com.app.ecomapp.data.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val imageUrl: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
