package com.app.ecomapp.presentation.screens.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.address.UserAddressData
import com.app.ecomapp.data.models.auth.PrimeMembership
import com.app.ecomapp.data.models.auth.UserResponse
import com.app.ecomapp.presentation.components.AppPrimaryButton
import com.app.ecomapp.presentation.components.AppTextField
import com.app.ecomapp.presentation.components.Spacer_12dp
import com.app.ecomapp.presentation.components.Spacer_16dp
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.presentation.components.friendlyErrorMessage
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.auth.AuthViewModel
import com.app.ecomapp.presentation.screens.home.PrimePayScreen
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.Constants.Companion.BASE_URL
import com.app.ecomapp.utils.Constants.Companion.Ruppes
import com.app.ecomapp.utils.UserDataStore
import com.compose.jetshop.R
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val userDataStore = remember { UserDataStore(context) }
    val user by userDataStore.getUserData.collectAsState(initial = null)
    val profileUpdateState by viewModel.profileUpdateResponse.collectAsState()
    val userDataResponse by viewModel.userDataResponse.collectAsState()
    val scrollState = rememberScrollState()

    var showImagePickerDialog by remember { mutableStateOf(false) }
    var showFullImagePreviewDialog by remember { mutableStateOf(false) }
    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var permissionDeniedPermanently by remember { mutableStateOf(false) }
    var deniedPermissionName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 1. Lifecycle Aware Call: Fetch latest data every time screen resumes
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        user?.userId?.let { uid ->
            if (uid.isNotEmpty()) {
                viewModel.getUserData(uid)
            }
        }
    }

    // Populate local input fields whenever user object emits
    LaunchedEffect(user) {
        user?.let {
            firstName = it.firstName.orEmpty()
            lastName = it.lastName.orEmpty()
            phoneNumber = it.phone.orEmpty()
            email = it.email.orEmpty()
        }
    }

    // Save fetched network profile to local DataStore safely
    LaunchedEffect(userDataResponse) {
        if (userDataResponse is Resource.Success) {
            val freshData = (userDataResponse as Resource.Success<UserResponse?>).data?.userData
            freshData?.let { data ->
                userDataStore.saveUser(
                    userId = data.userId.toString(),
                    name = "${data.firstName} ${data.lastName}".trim(),
                    first_name = data.firstName.orEmpty(),
                    last_name = data.lastName.orEmpty(),
                    email = data.email.orEmpty(),
                    phone = data.phone.orEmpty(),
                    user_profile = data.profileImage.orEmpty(),
                    isPrimeActive = data.isPrimeActive.toString(),
                    referralCode = data.referralCode.orEmpty(),
                    referredBy = data.referredBy.orEmpty(),
                    walletBalance = data.walletBalance,
                    defaultAddress = data.defaultAddress
                )
            }
        }
    }

    // Handle Profile Update completion & route error to banner
    LaunchedEffect(profileUpdateState) {
        when (val state = profileUpdateState) {
            is Resource.Success -> {
                errorMessage = null
                Toast.makeText(
                    context,
                    state.data?.message ?: "Profile updated successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                user?.let { viewModel.getUserData(it.userId) }
                viewModel.resetProfileUpdateState()
            }

            is Resource.Error -> {
                val rawError = state.message
                errorMessage = friendlyErrorMessage(rawError, "Failed to update profile. Please try again.")
                viewModel.resetProfileUpdateState()
            }

            else -> {}
        }
    }

    val launcherCamera = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val uri = saveBitmapToCache(context, it)
            selectedImageUri = uri
        }
    }

    val launcherGallery = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            permissionDeniedPermanently = !ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                deniedPermissionName
            )
            showPermissionDialog = true
        }
    }

    fun requestPermission(permission: String, onGranted: () -> Unit) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            onGranted()
        } else {
            deniedPermissionName = permission
            permissionLauncher.launch(permission)
        }
    }

    fun handleGalleryRequest() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        requestPermission(permission) {
            launcherGallery.launch("image/*")
        }
    }

    fun handleCameraRequest() {
        requestPermission(Manifest.permission.CAMERA) {
            launcherCamera.launch()
        }
    }

    // Full-Screen Image Preview Dialog
    if (showFullImagePreviewDialog) {
        Dialog(
            onDismissRequest = { showFullImagePreviewDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { showFullImagePreviewDialog = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 40.dp, end = 20.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Transparent,
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .aspectRatio(1f)
                ) {
                    val previewPainter = when {
                        selectedImageUri != null -> rememberAsyncImagePainter(selectedImageUri)
                        !user?.profileImage.isNullOrEmpty() -> rememberAsyncImagePainter(BASE_URL + user?.profileImage)
                        else -> painterResource(id = R.drawable.user)
                    }

                    Image(
                        painter = previewPainter,
                        contentDescription = "Full Profile Photo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            shape = RoundedCornerShape(18.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Permission Required",
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFF0F172A)
                )
            },
            text = {
                Text(
                    text = if (permissionDeniedPermanently)
                        "Permission was permanently denied. Please enable it from Settings."
                    else
                        "Permission is required to pick an image.",
                    fontFamily = Montserrat,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionDialog = false
                        if (permissionDeniedPermanently) {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("Settings", fontFamily = Montserrat, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancel", fontFamily = Montserrat, color = Color(0xFF64748B))
                }
            }
        )
    }

    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = "Update Photo",
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F172A)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                handleCameraRequest()
                                showImagePickerDialog = false
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Take Photo",
                                    fontFamily = Montserrat,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Use camera to capture image",
                                    fontFamily = Montserrat,
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                handleGalleryRequest()
                                showImagePickerDialog = false
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Choose from Gallery",
                                    fontFamily = Montserrat,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Select from saved photos",
                                    fontFamily = Montserrat,
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            ToolbarWithBackButtonAndTitle(
                "My Account",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Profile Completeness Progress Tracker
            val hasName = !user?.firstName.isNullOrBlank() && !user?.lastName.isNullOrBlank()
            val hasPhone = !user?.phone.isNullOrBlank()
            val hasPhoto = !user?.profileImage.isNullOrBlank() || selectedImageUri != null
            val hasAddress = user?.defaultAddress != null

            val completionPercentage = listOf(hasName, hasPhone, hasPhoto, hasAddress).count { it } * 25

            if (completionPercentage < 100) {
                ProfileCompletenessCard(
                    completionPercentage = completionPercentage,
                    missingItemsText = when {
                        !hasAddress -> "Add default delivery address for instant checkout"
                        !hasPhone -> "Add your verified phone number"
                        !hasPhoto -> "Add a profile photo"
                        else -> "Complete remaining details"
                    }
                )
            }

            // 2. Dashboard Quick Stats (Wallet & Referral)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { navController.navigate(Screen.ReferralScreen.route) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Jet Wallet",
                            fontSize = 11.sp,
                            fontFamily = Montserrat,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "$Ruppes${String.format(Locale.US, "%.2f", user?.walletBalance ?: 0.0)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Montserrat,
                            color = Color(0xFF0F172A)
                        )
                    }
                }

                val referralCode = user?.referralCode.orEmpty()
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (referralCode.isNotBlank()) {
                                clipboardManager.setText(AnnotatedString(referralCode))
                                Toast.makeText(context, "Referral code copied!", Toast.LENGTH_SHORT).show()
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFDF2F8)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = Color(0xFFDB2777),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Referral Code",
                            fontSize = 11.sp,
                            fontFamily = Montserrat,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = referralCode.ifBlank { "Not Assigned" },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Montserrat,
                            color = Color(0xFF0F172A),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // 3. Default Delivery Address Card
            DefaultAddressDashboardCard(
                defaultAddress = user?.defaultAddress,
                onManageClick = {
                    navController.navigate(Screen.Address.route)
                }
            )

            // 4. Prime Membership Status Card
            val networkUser = (userDataResponse as? Resource.Success)?.data?.userData
            val primeMembership = networkUser?.primeMembership ?: user?.primeMembership
            val isPrimeActive = networkUser?.isPrimeActive == 1 ||
                    networkUser?.isPrimeActive.toString() == "1" ||
                    user?.isPrimeActive == 1

            if (isPrimeActive && primeMembership != null) {
                ShowActivePlanCard(primeMembership = primeMembership)
            } else {
                ExpiredOrNoPlanCard(
                    isExpired = primeMembership != null,
                    expiryDate = primeMembership?.expiryDate.orEmpty(),
                    onRenewClick = {
                        val currentUserId = networkUser?.userId?.toString() ?: user?.userId?.toString().orEmpty()
                        val currentEmail = email.ifEmpty { networkUser?.email.orEmpty() }
                        val currentContact = phoneNumber.ifEmpty { networkUser?.phone.orEmpty() }

                        val intent = Intent(context, PrimePayScreen::class.java).apply {
                            putExtra("user_id", currentUserId)
                            putExtra("appName", "JetShop")
                            putExtra("email", currentEmail)
                            putExtra("contact", currentContact)
                        }
                        context.startActivity(intent)
                    }
                )
            }

            // 5. Personal Information Edit Form Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Visual affordance banner indicating this section is editable
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Personal Information",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Montserrat,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = "Editable",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = Montserrat,
                                color = Color(0xFF64748B),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer_8dp()

                    // Avatar with Full Preview Click + Camera Change Badge
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier.size(104.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            border = BorderStroke(2.5.dp, Color(0xFF2563EB)),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .size(98.dp)
                                .clickable { showFullImagePreviewDialog = true } // 2. Full Image View
                        ) {
                            when {
                                selectedImageUri != null -> {
                                    Image(
                                        painter = rememberAsyncImagePainter(selectedImageUri),
                                        contentDescription = "Profile Photo Preview",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                !user?.profileImage.isNullOrEmpty() -> {
                                    Image(
                                        painter = rememberAsyncImagePainter(BASE_URL + user?.profileImage),
                                        contentDescription = "Profile Photo Preview",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                else -> {
                                    Image(
                                        painter = painterResource(id = R.drawable.user),
                                        contentDescription = "Default Profile",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0xFFEFF6FF)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }

                        // Camera Icon specifically triggers Picker
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF2563EB),
                            shadowElevation = 3.dp,
                            modifier = Modifier
                                .size(34.dp)
                                .clickable { showImagePickerDialog = true }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Change Profile Picture",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = "Tap photo to preview",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            fontFamily = Montserrat
                        )
                        Text(
                            text = " • ",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "Tap camera to change",
                            fontSize = 11.sp,
                            color = Color(0xFF2563EB),
                            fontFamily = Montserrat,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { showImagePickerDialog = true }
                        )
                    }

                    // Animated Inline Error Box
                    AnimatedVisibility(
                        visible = !errorMessage.isNullOrBlank(),
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFEF2F2),
                            border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorMessage.orEmpty(),
                                    fontSize = 12.sp,
                                    fontFamily = Montserrat,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFB91C1C),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    Spacer_8dp()

                    AppTextField(
                        value = firstName,
                        onValueChange = {
                            firstName = it
                            errorMessage = null
                        },
                        label = "First Name",
                        leadingIcon = Icons.Default.Person
                    )

                    AppTextField(
                        value = lastName,
                        onValueChange = {
                            lastName = it
                            errorMessage = null
                        },
                        label = "Last Name",
                        leadingIcon = Icons.Default.Person
                    )

                    AppTextField(
                        value = email,
                        onValueChange = {},
                        label = "Email Address",
                        leadingIcon = Icons.Default.Email,
                        enabled = false,
                        readOnly = true,
                        trailingIcon = {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFE2E8F0),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Locked",
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B),
                                        fontFamily = Montserrat,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    )

                    AppTextField(
                        value = phoneNumber,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() }) {
                                phoneNumber = input
                                errorMessage = null
                            }
                        },
                        label = "Phone Number",
                        leadingIcon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AppPrimaryButton(
                        text = "Save Changes",
                        isLoading = profileUpdateState is Resource.Loading,
                        onClick = {
                            keyboardController?.hide()
                            val trimmedFirst = firstName.trim()
                            val trimmedLast = lastName.trim()
                            val trimmedPhone = phoneNumber.trim()

                            when {
                                trimmedFirst.isBlank() -> {
                                    errorMessage = "First name is required"
                                }
                                trimmedLast.isBlank() -> {
                                    errorMessage = "Last name is required"
                                }
                                trimmedPhone.isBlank() -> {
                                    errorMessage = "Phone number is required"
                                }
                                trimmedPhone.length < 10 -> {
                                    errorMessage = "Please enter a valid 10-digit mobile number"
                                }
                                else -> {
                                    errorMessage = null
                                    user?.let {
                                        coroutineScope.launch {
                                            val imagePart = selectedImageUri?.let { uri ->
                                                uriToMultipart(context, uri)
                                            }

                                            viewModel.updateUser(
                                                userId = it.userId.toString(),
                                                firstName = trimmedFirst,
                                                lastName = trimmedLast,
                                                fullName = "$trimmedFirst $trimmedLast".trim(),
                                                phoneNumber = trimmedPhone,
                                                email = email,
                                                profile_image = imagePart
                                            )
                                            selectedImageUri = null
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileCompletenessCard(
    completionPercentage: Int,
    missingItemsText: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Profile Completeness",
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF14532D)
                    )
                }
                Text(
                    text = "$completionPercentage%",
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = Color(0xFF16A34A)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { completionPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = Color(0xFF16A34A),
                trackColor = Color(0xFFDCFCE7)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = missingItemsText,
                fontFamily = Montserrat,
                fontSize = 11.sp,
                color = Color(0xFF15803D)
            )
        }
    }
}

@Composable
fun DefaultAddressDashboardCard(
    defaultAddress: UserAddressData?,
    onManageClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onManageClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Address",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Delivery Address",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Montserrat,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = if (defaultAddress != null) "Default Shipping" else "No default address",
                            fontSize = 11.sp,
                            fontFamily = Montserrat,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (defaultAddress != null) "Change" else "Add New",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Montserrat,
                        color = Color(0xFF2563EB)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(11.dp)
                    )
                }
            }

            Spacer_12dp()
            HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFF1F5F9))
            Spacer_12dp()

            if (defaultAddress != null) {
                Row(verticalAlignment = Alignment.Top) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = (defaultAddress.type?.ifBlank { "Home" } ?: "Home").uppercase(Locale.getDefault()),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569),
                            fontFamily = Montserrat,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = defaultAddress.address.orEmpty(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Montserrat,
                            color = Color(0xFF334155),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${defaultAddress.city.orEmpty()}, ${defaultAddress.state.orEmpty()} - ${defaultAddress.zipCode.orEmpty()}",
                            fontSize = 11.sp,
                            fontFamily = Montserrat,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AddLocationAlt,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Set a default delivery address to enable 1-tap checkout.",
                        fontSize = 12.sp,
                        fontFamily = Montserrat,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

@Composable
fun ShowActivePlanCard(
    primeMembership: PrimeMembership,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "👑",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Prime Membership",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        fontFamily = Montserrat
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFDBEAFE))
                ) {
                    Text(
                        text = primeMembership.planName.uppercase(Locale.getDefault()),
                        color = Color(0xFF2563EB),
                        fontSize = 11.sp,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer_16dp()
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFF1F5F9))
            Spacer_16dp()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Subscription Fee",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    fontFamily = Montserrat
                )
                Text(
                    text = "$Ruppes ${primeMembership.planPrice}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    fontFamily = Montserrat
                )
            }

            Spacer_8dp()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Valid Until",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    fontFamily = Montserrat
                )
                Text(
                    text = formatDateTime(primeMembership.expiryDate),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF059669),
                    fontFamily = Montserrat
                )
            }
        }
    }
}

@Composable
fun ExpiredOrNoPlanCard(
    isExpired: Boolean,
    expiryDate: String,
    onRenewClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (isExpired) Color(0xFFFED7AA) else Color(0xFFE9D5FF)),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpired) Color(0xFFFFF7ED) else Color(0xFFFAF5FF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = "Alert",
                    tint = if (isExpired) Color(0xFFEA580C) else Color(0xFF9333EA),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isExpired) "Membership Expired" else "Unlock Prime Benefits",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Montserrat,
                    color = if (isExpired) Color(0xFFEA580C) else Color(0xFF9333EA)
                )
            }

            Spacer_8dp()

            Text(
                text = if (isExpired) {
                    if (expiryDate.isNotEmpty()) {
                        "Your Prime subscription expired on ${formatDateTime(expiryDate)}. Renew now to enjoy free shipping and prime discounts!"
                    } else {
                        "Your Prime subscription has expired. Renew now to reactivate member benefits!"
                    }
                } else {
                    "Join Prime today to unlock free deliveries, express shipping, and exclusive member-only pricing!"
                },
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                fontFamily = Montserrat,
                color = Color(0xFF475569),
                lineHeight = 19.sp
            )

            Spacer_16dp()

            Button(
                onClick = onRenewClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isExpired) Color(0xFFEA580C) else Color(0xFF7C3AED)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isExpired) "Renew Prime Now" else "Explore Prime Plans",
                        fontWeight = FontWeight.Bold,
                        fontFamily = Montserrat,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

fun formatDateTime(dateStr: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val parsed = inputFormat.parse(dateStr)
        parsed?.let { outputFormat.format(it) } ?: dateStr
    } catch (e: Exception) {
        dateStr
    }
}

fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "captured_image_${System.currentTimeMillis()}.jpg")
    file.outputStream().use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
    }
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

fun uriToMultipart(
    context: Context,
    uri: Uri,
    paramName: String = "profile_image"
): MultipartBody.Part? {
    return try {
        val file = FileUtils.from(context, uri)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        MultipartBody.Part.createFormData(paramName, file.name, requestBody)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

object FileUtils {
    fun from(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return file
    }
}