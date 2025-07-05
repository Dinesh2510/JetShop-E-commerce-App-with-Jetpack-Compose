package com.app.ecomapp.presentation.screens.profile.notification

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.app.ecomapp.data.models.entity.NotificationEntity
import com.app.ecomapp.presentation.components.TitleSmall
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.CommonFunction.LottieAnimationView
import com.app.ecomapp.utils.NetworkImage
import com.compose.jetshop.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    control: NavHostController,
    viewModel: NotificationViewModel = hiltViewModel(),
) {

    val notifications by viewModel.notifications.collectAsState()
    //viewModel.insertDummyNotifications()
    Scaffold(
        topBar = {
            ToolbarWithBackButtonAndTitle("Notification",
                onBackClick = { control.popBackStack() })
        }
    ) { paddingValues ->
        if (notifications.isEmpty()) {
            LottieAnimationView(animationResId = R.raw.empty_box, text = "No Notifications Yet!")

//            EmptyState(modifier = Modifier.padding(paddingValues))
        } else {
            NotificationList(notifications, modifier = Modifier.padding(paddingValues), viewModel)
        }
    }
}


@Composable
fun NotificationList(
    notifications: List<NotificationEntity>,
    modifier: Modifier = Modifier,
    viewModel: NotificationViewModel,
) {
    var selectedNotification by remember { mutableStateOf<NotificationEntity?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(notifications) { notification ->
            NotificationItem(notification) {
                viewModel.markAsRead(notification.id)
                selectedNotification =
                    notification // When clicked, set the notification to show in the dialog

            }
        }
    }
    // Display the AlertDialog with the selected notification's details
    selectedNotification?.let { notification ->
        NotificationDetailsDialog(notification = notification, onDismiss = {
            selectedNotification = null // Dismiss the dialog by resetting the selected notification
        })
    }
}


fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No Notifications Yet!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You're all caught up!",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NotificationItem(notification: NotificationEntity, onClick: () -> Unit) {
    val unreadDotColor = if (notification.isRead) Color.Transparent else Color.Red
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }, // onClick triggers the alert dialog
        shape = RoundedCornerShape(12.dp), // Round the corners of the card
        elevation = CardDefaults.cardElevation() // Elevation for shadow effect
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Log.d("TAG_ITEM", "NotificationItem: " + notification.imageUrl)
            NetworkImage(url = notification.imageUrl, modifier = Modifier.size(40.dp))

            Spacer(modifier = Modifier.width(16.dp))

            // Content part (Title, Message, Timestamp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatDate(notification.timestamp), fontFamily = Montserrat,
                    fontWeight = FontWeight.Normal
                )


            }

            // Unread dot indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color = unreadDotColor, shape = CircleShape)
            )
        }
    }
}

@Composable
fun NotificationDetailsDialog(notification: NotificationEntity, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Notification Details") },
        text = {
            Column {
                NetworkImage(url = notification.imageUrl)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Title: ${notification.title}")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Message: ${notification.message}")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Timestamp: ${notification.timestamp}")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
