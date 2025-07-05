package com.app.ecomapp.presentation.screens.profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.compose.jetshop.R
import com.app.ecomapp.presentation.components.AppButton
import com.app.ecomapp.presentation.components.LoginPromptDialog
import com.app.ecomapp.presentation.components.Spacer_12dp
import com.app.ecomapp.presentation.components.Spacer_16dp
import com.app.ecomapp.presentation.components.Spacer_24dp
import com.app.ecomapp.presentation.components.Spacer_32dp
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.ui.theme.AppMainTypography
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.CommonFunction.getAppVersionName
import com.app.ecomapp.utils.Constants.Companion.BASE_URL
import com.app.ecomapp.utils.IncludeApp
import com.app.ecomapp.utils.UserDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    val context = LocalContext.current
    var showAboutDialog by remember { mutableStateOf(false) }
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
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer_32dp()

        // ✅ Profile Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            user?.let {
                if (!it.profileImage.isNullOrBlank()) {
                    val imageUrl = BASE_URL+ it.profileImage

                    Log.d("TAG_profile", "Full image URL = $imageUrl")
                    Image(
                        painter = rememberAsyncImagePainter(BASE_URL+user?.profileImage),
                        contentDescription = null,
                        modifier = Modifier
                            .size(65.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Log.e("TAG_profile", "profile_image is null or empty")
                    Image(
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = "Default Image",
                        modifier = Modifier
                            .size(65.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }


            }
            Spacer_12dp()

            Column(modifier = Modifier.weight(1f)) {
                user?.let {
                    Text(
                        text = (it.firstName + " " + it.lastName) ?: "Name",
                        style = AppMainTypography.subHeader,
                    )
                    Text(
                        text = it.email,
                        style = AppMainTypography.bodyText,
                        color = Color.Gray
                    )
                }
            }

            /*IconButton(
                onClick = { Toast.makeText(context, "Editing profile", Toast.LENGTH_SHORT).show() }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = MaterialTheme.colorScheme.primary
                )
            }*/
        }

        Spacer_16dp()
        HorizontalDivider()

        ProfileItem(icon = R.drawable.userprofile, name = "Profile") {
            if (isLoggedIn.value) {
                navController.navigate(Screen.UpdateProfile.route)
            }else{
                showLoginDialog.value = true
            }
        }
        ProfileItem(icon = R.drawable.checklist, name = "My Orders") {
            if (isLoggedIn.value) {
                navController.navigate(Screen.UserOrderScreen.route)
            }else{
                showLoginDialog.value = true
            }
        }
        ProfileItem(icon = R.drawable.location, name = "My Address") {
            if (isLoggedIn.value) {
                navController.navigate(Screen.Address.route)
            }else{
                showLoginDialog.value = true
            }
        }

        /*ProfileItem(icon = Icons.Default.Payment, name = "Payment Methods") {
            navController.navigate(Screen.Address.route)
        }*/

        ProfileItem(icon = R.drawable.coupon, name = "Coupon Codes") {
            if (isLoggedIn.value) {
                navController.navigate(Screen.PromoCode.route)
            }else{
                showLoginDialog.value = true
            }
        }
        ProfileItem(icon = R.drawable.blog, name = "Blogs") {
            navController.navigate(Screen.BlogsScreen.route)
            }
        ProfileItem(icon = R.drawable.bell, name = "Notifications") {
            navController.navigate(Screen.Notification.route)
        }
        ProfileItem(icon = R.drawable.coin, name = "Refer & Earn") {
            if (isLoggedIn.value) {
                navController.navigate(Screen.ReferralScreen.route)
            }else{
                showLoginDialog.value = true
            }
        }

        ProfileItem(icon = R.drawable.help, name = "Help") {
            navController.navigate(Screen.HelpScreen.route)
        }


        ProfileItem(icon = R.drawable.about, name = "About") {
            //  navController.navigate(Screen.Orders.route)
            showAboutDialog = true
        }
        ProfileItem(icon = R.drawable.setting, name = "Settings") {
              navController.navigate(Screen.SettingScreen.route)
        }


        Spacer_24dp()
        Text(
            text = "v."+getAppVersionName(context),
            color = Color.Black,
            textAlign= TextAlign.Center,
            style = AppMainTypography.subHeader,
            modifier = Modifier
                .align(Alignment.CenterHorizontally) // ✅ Center horizontally in the Column
                .padding(bottom = 16.dp)
        )

        if (showAboutDialog) {
            IncludeApp().ShowAbout {
                showAboutDialog = false
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
    val context = LocalContext.current
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
    val privacyUrl = remember { mutableStateOf("") }
    val termsUrl = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        isLoggedIn.value = dataStoreHelper.isUserLoggedIn(context)
        val appInfo = dataStoreHelper.getAppInfoObject()
        appInfo?.let {
            privacyUrl.value = it.privacyUrl
            termsUrl.value = it.termsUrl
        }
    }
    Scaffold(
        topBar = {
            ToolbarWithBackButtonAndTitle("Settings",
                onBackClick = { navController.popBackStack() })
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {

            item {
                IncludeApp().CustomDivider()
                ProfileItem(
                    icon = R.drawable.bell,
                    name = "Notifications",
                    showArrow = false,
                    trailing = {
                        Switch(
                            checked = viewModel.notificationEnabled.value,
                            onCheckedChange = { viewModel.toggleNotification(it) }
                        )
                    }
                )
            }

            item {
                ProfileItem(
                    icon = R.drawable.mop,
                    name ="Clear Cache",
                    subtitle = cache,
                    onClick = {
                        isClearing = true
                        viewModel.clearCache {
                            isClearing = false
                            Toast.makeText(context, "Cache cleared", Toast.LENGTH_SHORT).show()
                        }
                    },
                    trailing = {
                        Text(text = "clear cache")
                    }
                )
            }

            item {
                ProfileItem(
                    icon = R.drawable.theme,
                    name = "App Theme (\uD83D\uDEE0 Development)",
                    subtitle = themeState.appTheme.name,
                    onClick = { showDialog = true }
                )
            }

            item {
                ProfileItem(
                    icon = R.drawable.privacy,
                    name = "Privacy Policy",
                    onClick = {
                        val url = URLEncoder.encode(privacyUrl.value, "UTF-8")
                        navController.navigate(Screen.WebViewScreen.route+"/Privacy Policy/$url")
                    }
                )

            }

            item {
                ProfileItem(
                    icon = R.drawable.terms,
                    name = "Terms of Uses",
                    onClick = {
                        val url = URLEncoder.encode(termsUrl.value, "UTF-8")
                        navController.navigate(Screen.WebViewScreen.route+"/Terms & Conditions/$url")
                    }
                )
            }
            item {
                // ✅ Logout Button
                if (isLoggedIn.value) {

                    LogoutSettingItem {
                        CoroutineScope(Dispatchers.IO).launch {
                            dataStoreHelper.clearUserData()
                            dataStoreHelper.saveLoginState(context,false)
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
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onLogoutConfirmed()
                }) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    ProfileItem(
        icon = R.drawable.logout,
        name = "Logout",
        showArrow = false,
        onClick = { showDialog = true }
    )
}


@Composable
fun ThemeSelectorDialog2025(
    currentTheme: AppThemeOption,
    onDismiss: () -> Unit,
    onThemeSelected: (AppThemeOption) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose App Theme") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AppThemeOption.values().forEach { theme ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onThemeSelected(theme)
                                onDismiss()
                            }
                            .padding(8.dp)
                    ) {
                        RadioButton(
                            selected = theme == currentTheme,
                            onClick = {
                                onThemeSelected(theme)
                                onDismiss()
                            }
                        )
                        Text(
                            text = theme.name,
                            style = AppMainTypography.subHeader,
                            modifier = Modifier.padding(start = 0.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {}
    )
}
