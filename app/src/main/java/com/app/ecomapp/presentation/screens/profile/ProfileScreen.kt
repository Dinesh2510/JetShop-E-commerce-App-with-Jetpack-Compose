package com.app.ecomapp.presentation.screens.profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.app.ecomapp.presentation.components.LoginPromptDialog
import com.app.ecomapp.presentation.components.Spacer_12dp
import com.app.ecomapp.presentation.components.Spacer_20dp
import com.app.ecomapp.presentation.components.Spacer_24dp
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.presentation.screens.home.DeveloperShowcaseCard
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.CommonFunction.getAppVersionName
import com.app.ecomapp.utils.Constants.Companion.BASE_URL
import com.app.ecomapp.utils.IncludeApp
import com.app.ecomapp.utils.UserDataStore
import com.compose.jetshop.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    val context = LocalContext.current
    var showAboutDialog by remember { mutableStateOf(false) }
    var showFullImagePreviewDialog by remember { mutableStateOf(false) }
    val dataStoreHelper = remember { UserDataStore(context) }
    val isLoggedIn = remember { mutableStateOf(false) }
    val showLoginDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoggedIn.value = dataStoreHelper.isUserLoggedIn(context)
    }
    val user by dataStoreHelper.getUserData.collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        val currentUser = user

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (currentUser == null) {
                        Modifier.clickable {
                            navController.navigate(Screen.Login.route)
                        }
                    } else {
                        Modifier
                    }
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentUser != null) {
                    // ================= Logged In State =================
                    if (!currentUser.profileImage.isNullOrBlank()) {
                        val imageUrl = BASE_URL + currentUser.profileImage
                        Log.d("TAG_profile", "Full image URL = $imageUrl")
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color(0xFF2563EB), CircleShape)
                                .clickable { showFullImagePreviewDialog = true },
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Log.e("TAG_profile", "profile_image is null or empty")
                        Image(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "Default User Picture",
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEFF6FF))
                                .clickable { showFullImagePreviewDialog = true },
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer_12dp()

                    Column(modifier = Modifier.weight(1f)) {
                        val fullName = "${currentUser.firstName.orEmpty()} ${currentUser.lastName.orEmpty()}".trim()
                        Text(
                            text = if (fullName.isNotBlank()) fullName else "Valued Customer",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            fontFamily = Montserrat,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentUser.email.orEmpty().ifBlank { currentUser.phone.orEmpty() },
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            fontFamily = Montserrat,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    // ================= Guest / Not Logged In State =================
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Guest",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer_12dp()

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hello, Guest!",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            fontFamily = Montserrat
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Sign in to view orders, wishlist & wallet",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            fontFamily = Montserrat,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF2563EB),
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.Login.route)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Sign In",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Montserrat
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = "Go",
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer_20dp()

        SectionTitle(title = "Developer & Source")
        DeveloperShowcaseCard(
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer_8dp()

        // --- Group 1: Account & Shopping ---
        SectionTitle(title = "My Account")
        Spacer_8dp()
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                ModernProfileRow(icon = R.drawable.userprofile, name = "My Account") {
                    if (isLoggedIn.value) {
                        navController.navigate(Screen.UpdateProfile.route)
                    } else {
                        showLoginDialog.value = true
                    }
                }
                CardItemDivider()
                ModernProfileRow(icon = R.drawable.checklist, name = "My Orders") {
                    if (isLoggedIn.value) {
                        navController.navigate(Screen.UserOrderScreen.route)
                    } else {
                        showLoginDialog.value = true
                    }
                }
                CardItemDivider()
                ModernProfileRow(icon = R.drawable.location, name = "Delivery Addresses") {
                    if (isLoggedIn.value) {
                        navController.navigate(Screen.Address.route)
                    } else {
                        showLoginDialog.value = true
                    }
                }
            }
        }

        Spacer_20dp()

        // --- Group 2: Benefits & Content ---
        SectionTitle(title = "Offers & Rewards")
        Spacer_8dp()
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                ModernProfileRow(icon = R.drawable.coupon, name = "Coupons & Offers") {
                    if (isLoggedIn.value) {
                        navController.navigate(Screen.PromoCode.route)
                    } else {
                        showLoginDialog.value = true
                    }
                }
                CardItemDivider()
                ModernProfileRow(icon = R.drawable.coin, name = "Refer & Earn") {
                    if (isLoggedIn.value) {
                        navController.navigate(Screen.ReferralScreen.route)
                    } else {
                        showLoginDialog.value = true
                    }
                }
                CardItemDivider()
                ModernProfileRow(icon = R.drawable.blog, name = "Blogs & Stories") {
                    navController.navigate(Screen.BlogsScreen.route)
                }
            }
        }

        Spacer_20dp()

        // --- Group 3: App Settings & Support ---
        SectionTitle(title = "Settings & Support")
        Spacer_8dp()
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                ModernProfileRow(icon = R.drawable.bell, name = "Notifications") {
                    navController.navigate(Screen.Notification.route)
                }
                CardItemDivider()
                ModernProfileRow(icon = R.drawable.setting, name = "Settings") {
                    navController.navigate(Screen.SettingScreen.route)
                }
                CardItemDivider()
                ModernProfileRow(icon = R.drawable.help, name = "Help & Support") {
                    navController.navigate(Screen.HelpScreen.route)
                }
                CardItemDivider()
                ModernProfileRow(icon = R.drawable.about, name = "About App") {
                    showAboutDialog = true
                }
            }
        }

        Spacer_24dp()

        // --- Version Tag ---
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFEFF6FF),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Version " + getAppVersionName(context),
                color = Color(0xFF2563EB),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showAboutDialog) {
            IncludeApp().ShowAbout {
                showAboutDialog = false
            }
        }
    }

    // Full Profile Image Preview Dialog
    if (showFullImagePreviewDialog && user != null) {
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
                    val previewPainter = if (!user?.profileImage.isNullOrEmpty()) {
                        rememberAsyncImagePainter(BASE_URL + user?.profileImage)
                    } else {
                        painterResource(id = R.drawable.user)
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

    LoginPromptDialog(
        showDialog = showLoginDialog.value,
        onDismiss = { showLoginDialog.value = false },
        onLoginClick = {
            showLoginDialog.value = false
            navController.navigate(Screen.Login.route)
        }
    )
}

@Composable
fun ManagedSetting(navController: NavHostController) {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val themeState by settingsViewModel.themeState.collectAsState()
    SettingsScreen(
        navController = navController,
        themeState = themeState,
        viewModel = settingsViewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    themeState: ThemeState,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var isClearing by remember { mutableStateOf(false) }
    val dataStoreHelper = remember { UserDataStore(context) }
    val isLoggedIn = remember { mutableStateOf(false) }
    val cache = viewModel.cacheSize.value

    // Collect app info reactively from DataStore
    val appInfo by dataStoreHelper.getAppInfoFlow.collectAsState(initial = null)

    val privacyUrl = appInfo?.privacyUrl.orEmpty()
    val termsUrl = appInfo?.termsUrl.orEmpty()

    LaunchedEffect(Unit) {
        isLoggedIn.value = dataStoreHelper.isUserLoggedIn(context)
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            ToolbarWithBackButtonAndTitle(
                "Settings",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = paddingValues.calculateTopPadding() + 12.dp,
                bottom = paddingValues.calculateBottomPadding() + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // General Preferences
            item {
                SectionTitle("Preferences")
                Spacer_8dp()
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        ModernProfileRow(
                            icon = R.drawable.bell,
                            name = "Push Notifications",
                            showArrow = false,
                            trailing = {
                                Switch(
                                    checked = viewModel.notificationEnabled.value,
                                    onCheckedChange = { viewModel.toggleNotification(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF2563EB)
                                    )
                                )
                            }
                        )
                        CardItemDivider()
                        ModernProfileRow(
                            icon = R.drawable.mop,
                            name = "Clear Cache",
                            subtitle = if (isClearing) "Clearing..." else cache,
                            onClick = {
                                isClearing = true
                                viewModel.clearCache {
                                    isClearing = false
                                    Toast.makeText(context, "Cache cleared", Toast.LENGTH_SHORT).show()
                                }
                            },
                            trailing = {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFEFF6FF)
                                ) {
                                    Text(
                                        text = "Clean",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2563EB),
                                        fontFamily = Montserrat,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        )
                        CardItemDivider()
                        ModernProfileRow(
                            icon = R.drawable.theme,
                            name = "App Theme (beta)",
                            subtitle = themeState.appTheme.name,
                            onClick = { showDialog = true }
                        )
                    }
                }
            }

            // Legal & Terms
            item {
                SectionTitle("Legal & Policies")
                Spacer_8dp()
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        ModernProfileRow(
                            icon = R.drawable.privacy,
                            name = "Privacy Policy",
                            onClick = {
                                if (privacyUrl.isNotBlank()) {
                                    val encodedUrl = URLEncoder.encode(privacyUrl, "UTF-8")
                                    navController.navigate("${Screen.WebViewScreen.route}/Privacy Policy/$encodedUrl")
                                } else {
                                    Toast.makeText(context, "Unable to load link. Please try again.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        CardItemDivider()
                        ModernProfileRow(
                            icon = R.drawable.terms,
                            name = "Terms of Use",
                            onClick = {
                                if (termsUrl.isNotBlank()) {
                                    val encodedUrl = URLEncoder.encode(termsUrl, "UTF-8")
                                    navController.navigate("${Screen.WebViewScreen.route}/Terms & Conditions/$encodedUrl")
                                } else {
                                    Toast.makeText(context, "Unable to load link. Please try again.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }

            // Logout Action
            if (isLoggedIn.value) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    LogoutSettingItem {
                        CoroutineScope(Dispatchers.IO).launch {
                            dataStoreHelper.clearUserData()
                            dataStoreHelper.saveLoginState(context, false)
                        }
                        navController.navigate(Screen.OnBoard.route) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = false }
                            launchSingleTop = true
                        }
                        Toast.makeText(context, "Logged out!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    if (showDialog) {
        ThemeSelectorDialog2025(
            currentTheme = themeState.appTheme,
            onDismiss = { showDialog = false },
            onThemeSelected = {
                viewModel.setTheme(it)
            }
        )
    }
}

@Composable
fun LogoutSettingItem(onLogoutConfirmed: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            shape = RoundedCornerShape(18.dp),
            containerColor = Color.White,
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Confirm Logout",
                    fontWeight = FontWeight.Bold,
                    fontFamily = Montserrat,
                    fontSize = 18.sp,
                    color = Color(0xFF0F172A)
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to sign out of your account?",
                    fontFamily = Montserrat,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onLogoutConfirmed()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Logout", fontFamily = Montserrat, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel", fontFamily = Montserrat, color = Color(0xFF64748B))
                }
            }
        )
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
        border = BorderStroke(1.dp, Color(0xFFFECDD3)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFEE2E2)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logout),
                    contentDescription = "Logout",
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer_12dp()
            Text(
                text = "Sign Out",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Montserrat,
                color = Color(0xFFE11D48),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ThemeSelectorDialog2025(
    currentTheme: AppThemeOption,
    onDismiss: () -> Unit,
    onThemeSelected: (AppThemeOption) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                "Choose App Theme",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Montserrat,
                color = Color(0xFF0F172A)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AppThemeOption.values().forEach { theme ->
                    val isSelected = theme == currentTheme
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onThemeSelected(theme)
                                onDismiss()
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    onThemeSelected(theme)
                                    onDismiss()
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Color(0xFF2563EB),
                                    unselectedColor = Color(0xFF94A3B8)
                                )
                            )
                            Spacer_8dp()
                            Text(
                                text = theme.name,
                                fontSize = 14.sp,
                                fontFamily = Montserrat,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF1D4ED8) else Color(0xFF1E293B)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = Montserrat,
        color = Color(0xFF64748B),
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
fun ModernProfileRow(
    icon: Int,
    name: String,
    subtitle: String? = null,
    showArrow: Boolean = true,
    trailing: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = name,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Montserrat,
                color = Color(0xFF0F172A)
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    fontFamily = Montserrat
                )
            }
        }

        if (trailing != null) {
            trailing()
        } else if (showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun CardItemDivider() {
    HorizontalDivider(
        thickness = 1.dp,
        color = Color(0xFFF1F5F9),
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}