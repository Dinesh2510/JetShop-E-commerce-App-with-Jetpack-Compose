package com.app.ecomapp.messagingservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.app.ecomapp.MainPage
import com.compose.jetshop.R
import com.app.ecomapp.data.models.entity.NotificationEntity
import com.app.ecomapp.domain.repository.NotificationRepository
import com.app.ecomapp.utils.dataStore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.URL
import javax.inject.Inject
@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var repository: NotificationRepository

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New Token: $token")
        // Optionally send token to your backend here
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Avoid GlobalScope - use coroutine with proper context
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = applicationContext.dataStore.data.first()
                val notificationsEnabled = prefs[booleanPreferencesKey("notifications_enabled")] ?: true

                if (!notificationsEnabled) {
                    Log.d("FCM", "Notifications are disabled by user")
                    return@launch
                }

                var dataRemote = remoteMessage.data
                if (dataRemote.isNotEmpty()) {
                    val notificationType = dataRemote["notificationType"]
                    val title = dataRemote["title"] ?: "New Notification"
                    val message = dataRemote["message"] ?: "You have a new message"
                    val productId = dataRemote["productId"]
                    val orderId = dataRemote["orderId"]
                    val timestamp = dataRemote["timestamp"]
                    val customLog = dataRemote["customLog"]
                    val imageUrl = dataRemote["imageUrl"]

                    // Save to local DB
                    repository.insertNotification(
                        NotificationEntity(
                            title = title,
                            message = message,
                            timestamp = System.currentTimeMillis(),
                            isRead = false,
                            imageUrl = imageUrl ?: ""
                        )
                    )

                    // Prepare intents
                    val intent = when (notificationType) {
                        "order_update" -> {
                            Intent(this@MyFirebaseMessagingService, MainPage::class.java).apply {
                                data = Uri.parse("jetshop://order_details/$title")
                                putExtra("orderId", orderId)
                            }
                        }
                        "promotion" -> {
                            Intent(this@MyFirebaseMessagingService, MainPage::class.java).apply {
                                data = Uri.parse("jetshop://promotion_screen")
                            }
                        }
                        "app_update" -> {
                            Intent(this@MyFirebaseMessagingService, MainPage::class.java).apply {
                                data = Uri.parse("jetshop://app_update")
                            }
                        }
                        else -> {
                            Intent(this@MyFirebaseMessagingService, MainPage::class.java).apply {
                                data = Uri.parse("jetshop://all")
                            }
                        }
                    }

                    val pendingIntent = PendingIntent.getActivity(
                        this@MyFirebaseMessagingService,
                        0,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE
                    )

                    sendNotification(title, message, imageUrl, pendingIntent)
                }

            } catch (e: Exception) {
                Log.e("FCM", "Error processing notification", e)
            }
        }
    }

    private fun sendNotification(title: String, message: String, imageUrl: String?, pendingIntent: PendingIntent) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "ecommerce_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "E-Commerce Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.baseline_circle_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        imageUrl?.let {
            loadBitmapFromUrl(it)?.let { bitmap ->
                builder.setLargeIcon(bitmap)
                    .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
            }
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun loadBitmapFromUrl(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch (e: Exception) {
            Log.e("Notification", "Failed to load image from $imageUrl", e)
            null
        }
    }
}


/*
https://pixeldev.in/webservices/jetshop/send_promo_notification.php
* https://pixeldev.in/webservices/jetshop/notification/sendme.php
* user_id:9
data:{↵  "notificationType": "promotion",↵  "title": "Limited Time Offer!",↵  "message": "Get 20% off on your next order!",↵  "imageUrl": "https://picsum.photos/200/200",↵  "customLog": "Promotion campaign March 2025",↵  "timestamp": "2025-03-11T10:30:00Z"↵}↵
*
* */