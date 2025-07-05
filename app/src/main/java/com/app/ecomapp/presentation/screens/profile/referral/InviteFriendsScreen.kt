package com.app.ecomapp.presentation.screens.profile.referral

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.compose.jetshop.R
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.auth.UserData
import com.app.ecomapp.data.models.refer.ReferResponse
import com.app.ecomapp.data.models.refer.Referral
import com.app.ecomapp.data.models.wallet.WalletHistoryResponse
import com.app.ecomapp.presentation.components.Spacer_12dp
import com.app.ecomapp.presentation.components.Spacer_16dp
import com.app.ecomapp.presentation.components.Spacer_4dp
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.components.TitleSmall
import com.app.ecomapp.ui.theme.AppMainTypography
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.CommonFunction.CircularCheckboxWithLabel
import com.app.ecomapp.utils.CommonFunction.LottieAnimationView
import com.app.ecomapp.utils.Constants.Companion.Ruppes
import com.app.ecomapp.utils.HandleApiState
import com.app.ecomapp.utils.UserDataStore
@Composable
fun InviteFriendsScreen(
    navController: NavHostController,
    viewModel: ReferViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val userDataStore = UserDataStore(context)
    val user by userDataStore.getUserData.collectAsState(initial = null)

    val contactPermission = Manifest.permission.READ_CONTACTS
    val permissionState = remember { mutableStateOf(false) }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            permissionState.value = isGranted
        }

    // 🟡 Tab index
    val tabTitles = listOf("Invite Friends", "Wallet History", "Referral History")
    var selectedTabIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            contactPermission
        ) == PackageManager.PERMISSION_GRANTED
        permissionState.value = isGranted
    }

    Scaffold(
        topBar = {
            user?.walletBalance?.let {
                ToolbarWallet(
                    title = "Refer & Earn",
                    onBackClick = { navController.popBackStack() },
                    balance = Ruppes + it
                )
            }
        },
        bottomBar = {
            if (selectedTabIndex == 0 && permissionState.value) {
                Button(
                    onClick = { viewModel.inviteSelectedContacts() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DD75B)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .navigationBarsPadding()
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer_8dp()
                    Text(
                        "Invite Selected (${viewModel.selectedContacts.size})",
                        color = Color.White,
                        style = AppMainTypography.bodyText
                    )
                }
            }
        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {
            // ✅ Tab Row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        selectedContentColor = Color(0xFF1DD75B),
                        unselectedContentColor = Color.Black
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontFamily = Montserrat,
                                modifier = Modifier.padding(12.dp),
                                fontSize = 16.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                }
            }

            // ✅ Content per tab
            when (selectedTabIndex) {
                0 -> InviteFriendsTab(permissionState, launcher, contactPermission, viewModel, user)
                1 -> WalletHistoryTab(viewModel,navController) // Replace with your real content
                2 -> ReferralHistoryTab(viewModel,navController) // Replace with your real content
            }
        }
    }
}
@Composable
fun InviteFriendsTab(
    permissionState: MutableState<Boolean>,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    contactPermission: String,
    viewModel: ReferViewModel,
    user: UserData?
) {
    LazyColumn(
        modifier = Modifier.background(Color(0xFFF3FDF9)),
        contentPadding = PaddingValues(bottom = 50.dp),
    ) {
        item {
            user?.referralCode?.let {
                InviteHeaderCard(it)
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("Invited Friends", style = MaterialTheme.typography.titleMedium)
                Spacer_12dp()

                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .heightIn(max = 600.dp)
                    ) {
                        if (permissionState.value) {
                            LaunchedEffect(Unit) {
                                viewModel.loadContacts()
                            }

                            val contactItems = viewModel.contactPager.collectAsLazyPagingItems()
                            LazyColumn(
                                contentPadding = PaddingValues(vertical = 8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(contactItems.itemCount) { index ->
                                    val contact = contactItems[index]
                                    if (contact != null) {
                                        ContactInviteItem(
                                            contact = contact,
                                            isSelected = viewModel.selectedContacts.contains(contact),
                                            onClick = { viewModel.toggleContactSelection(contact) }
                                        )
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(onClick = { launcher.launch(contactPermission) }) {
                                    Text("Load Contacts")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ReferralHistoryTab(viewModel: ReferViewModel, navController: NavHostController) {
    var referResponse by remember { mutableStateOf<ReferResponse?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchRefer()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(16.dp)
    ) {
        Text(
            text = "Referral History",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        HandleApiState(
            apiState = viewModel.referList,
            showLoader = true,
            navController = navController,
            onSuccess = { data ->
                referResponse = data
            }
        ) {
            val state = viewModel.referList.collectAsState().value

            val referrals = referResponse?.referrals

            if (!referrals.isNullOrEmpty()) {
                Column {
                    referrals.forEach { referral ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            ListItem(
                                headlineContent = { Text(referral.name) },
                                supportingContent = { Text("Joined: ${referral.joinedAt}") },
                                trailingContent = {
                                    Text(
                                        text = "+ ₹${referral.bonusEarned}",
                                        color = Color(0xFF1DD75B)
                                    )
                                }
                            )
                        }
                    }
                }
            } else if (state is Resource.Error) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LottieAnimationView(
                        animationResId = R.raw.empty_box,
                        text = "Data not available"
                    )
                }
            }
        }

    }
}
@Composable
fun WalletHistoryTab(viewModel: ReferViewModel, navController: NavHostController) {
    var walletResponse by remember { mutableStateOf<WalletHistoryResponse?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchWalletHistory()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(16.dp)
    ) {
        Text(
            text = "Wallet History",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        HandleApiState(
            apiState = viewModel.getWalletHistory,
            showLoader = true,
            navController = navController,
            onSuccess = { data ->
                walletResponse = data
            }
        ) {
            val state = viewModel.referList.collectAsState().value

            val walletHistoryResponse = walletResponse?.transactions

            if (!walletHistoryResponse.isNullOrEmpty()) {
                Column {
                    walletHistoryResponse.forEach { referral ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            ListItem(
                                headlineContent = { Text(referral.type) },
                                supportingContent = { Text( referral.description) },
                                trailingContent = {
                                    Text(
                                        text = "+ ₹${referral.amount}",
                                        color = Color(0xFF1DD75B)
                                    )
                                }
                            )
                        }
                    }
                }
            } else if (state is Resource.Error) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LottieAnimationView(
                        animationResId = R.raw.empty_box,
                        text = "Data not available"
                    )
                }
            }
        }

    }
}

@Composable
fun InviteHeaderCard(code: String) {
    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimationView(
                    animationResId = R.raw.refer_earn,
                    contentScale = ContentScale.Crop
                )
            }
            Spacer_16dp()
            Text("Invite Friends & Get Rewards", style = AppMainTypography.subHeader)
            Spacer_8dp()
            Text(
                "Invite your friends to earn with JetShop and get up to ₹100",
                style = AppMainTypography.bodyText,
                textAlign = TextAlign.Center
            )
            Spacer_12dp()
            DashedShareBox(code)
        }
    }
}

@Composable
fun ContactInviteItem(
    contact: ContactUiModel,
    isSelected: Boolean,
    inviteStatus: String = "Pending",
    onClick: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (contact.photoUri != null) {
                AsyncImage(
                    model = contact.photoUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.1f))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.3f))
                )
            }

            Spacer_12dp()

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${contact.firstName} ${contact.lastName}",
                    style = AppMainTypography.subHeader
                )
                Spacer_4dp()
                Text(
                    text = maskPhoneNumber(contact.number),
                    style = AppMainTypography.caption
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                // InviteStatusChip(inviteStatus)
                Spacer_4dp()
                CircularCheckboxWithLabel(
                    isChecked = isSelected,
                    onCheckedChange = null
                )
            }
        }

        HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.3f))
    }
}


@Composable
fun InviteStatusChip(status: String) {
    val (bgColor, textColor) = when (status) {
        "Invited" -> Color(0xFFDBF6E5) to Color(0xFF28C76F)
        "Pending" -> Color(0xFFFFF5CF) to Color(0xFFE4A700)
        "Rejected" -> Color(0xFFFEE1E1) to Color(0xFFD10000)
        else -> Color.LightGray to Color.DarkGray
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(bgColor, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(status, color = textColor, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun DashedShareBox(
    referenceCode: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

    val shareMessage = """
    🛒 Join me on Jetshop – your one-stop electronics shopping app! ⚡

    💸 Register using referral code: 👉 ${referenceCode} 👈 and get ₹100 bonus!
    📲 Download now: https://pixeldev.in

    🛍️ Shop smart. Save big. JetShop it!
""".trimIndent()


    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(horizontal = 28.dp)
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                val cornerRadius = size.height / 2
                val paint = Paint().apply {
                    color = Color(0xFF1DD75B)
                    style = PaintingStyle.Stroke
                    this.strokeWidth = strokeWidth
                    pathEffect = dashEffect
                }
                drawRoundRect(
                    color = Color(0xFF1DD75B),
                    topLeft = Offset(0f, 0f),
                    size = size,
                    style = Stroke(width = strokeWidth, pathEffect = dashEffect),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )
            }
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .clickable {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareMessage)
                }
                val chooser = Intent.createChooser(intent, "Share via")
                context.startActivity(chooser)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = referenceCode,
                style = AppMainTypography.subHeader,
                color = Color(0xFF1DD75B)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                /*Icon(
                    painter = painterResource(id = R.drawable.ic_whatsapp),
                    contentDescription = "WhatsApp",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )*/
                Spacer_12dp()
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

fun maskPhoneNumber(phone: String): String {
// Remove non-digits just for clean formatting
    val digits = phone.filter { it.isDigit() }

    return if (digits.length >= 10) {
        val countryCode = if (digits.length > 10) "+${digits.dropLast(10)} " else ""
        val last4 = digits.takeLast(4)
        "$countryCode***-***-$last4"
    } else {
        "***-***-" + digits.takeLast(4)
    }
}

@Composable
fun InviteFriendItem(name: String, date: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.user), // Replace with profile or avatar
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Spacer_12dp()

        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.bodyMedium)
            Text(date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }

        InviteStatusChip(status)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarWallet(
    title: String,
    onBackClick: () -> Unit = {},
    showBackButton: Boolean = true,
    balance: String = "0"
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)) {
        CenterAlignedTopAppBar(
            title = {
                TitleSmall(title)
            },
            navigationIcon = {
                if (showBackButton) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = White
            ),
            actions = {
                Row(
                    modifier = Modifier
                        .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(6.dp)) // light gray with rounded edges
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Wallet,
                        contentDescription = "Wallet",
                        modifier = Modifier.size(25.dp),
                        tint = Color(0xFFFFD700),
                    )
                    Text(
                        text = balance,
                        style = AppMainTypography.subHeader,
                        modifier = Modifier.padding(start = 4.dp),
                        color = Color.Black
                    )
                }
            }
            ,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
