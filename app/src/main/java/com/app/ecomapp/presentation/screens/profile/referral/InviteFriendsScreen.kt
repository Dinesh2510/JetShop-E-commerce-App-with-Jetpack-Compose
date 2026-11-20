package com.app.ecomapp.presentation.screens.profile.referral

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.app.ecomapp.data.models.auth.UserData
import com.app.ecomapp.data.models.refer.ReferResponse
import com.app.ecomapp.data.models.refer.Referral
import com.app.ecomapp.data.models.wallet.Transaction
import com.app.ecomapp.data.models.wallet.WalletHistoryResponse
import com.app.ecomapp.presentation.components.Spacer_12dp
import com.app.ecomapp.presentation.components.Spacer_16dp
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.CommonFunction.LottieAnimationView
import com.app.ecomapp.utils.Constants.Companion.Ruppes
import com.app.ecomapp.utils.HandleApiState
import com.app.ecomapp.utils.UserDataStore
import com.compose.jetshop.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteFriendsScreen(
    navController: NavHostController,
    viewModel: ReferViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val userDataStore = remember { UserDataStore(context) }
    val user by userDataStore.getUserData.collectAsState(initial = null)

    val contactPermission = Manifest.permission.READ_CONTACTS
    val permissionState = remember { mutableStateOf(false) }

    val contactItems = viewModel.contactPager.collectAsLazyPagingItems()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionState.value = isGranted
        if (isGranted) {
            contactItems.refresh() // Reloads cursor immediately
        }
    }

    val tabTitles = listOf("Invite Friends", "Wallet History", "Referrals")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            contactPermission
        ) == PackageManager.PERMISSION_GRANTED
        permissionState.value = granted
        if (granted) {
            contactItems.refresh()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            ToolbarWallet(
                title = "Refer & Earn",
                balance = user?.walletBalance?.toString() ?: "0",
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            if (selectedTabIndex == 0 && permissionState.value && viewModel.selectedContacts.isNotEmpty()) {
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                        Button(
                            onClick = { viewModel.inviteSelectedContacts() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Invite Selected (${viewModel.selectedContacts.size})",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Montserrat
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Surface(
                color = Color.White,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.White,
                    contentColor = Color(0xFF2563EB),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Color(0xFF2563EB),
                            height = 3.dp
                        )
                    },
                    divider = {}
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        val isSelected = selectedTabIndex == index
                        Tab(
                            selected = isSelected,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontFamily = Montserrat,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFF2563EB) else Color(0xFF64748B)
                                )
                            }
                        )
                    }
                }
            }

            when (selectedTabIndex) {
                0 -> InviteFriendsTab(
                    permissionState = permissionState,
                    launcher = launcher,
                    contactPermission = contactPermission,
                    viewModel = viewModel,
                    user = user,
                    contactItems = contactItems
                )
                1 -> WalletHistoryTab(viewModel = viewModel, navController = navController)
                2 -> ReferralHistoryTab(viewModel = viewModel, navController = navController)
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
    user: UserData?,
    contactItems: LazyPagingItems<ContactUiModel>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            user?.referralCode?.let {
                InviteHeaderCard(it)
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Phonebook Contacts",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Montserrat,
                    color = Color(0xFF0F172A)
                )

                if (permissionState.value && contactItems.itemCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFEFF6FF)
                    ) {
                        Text(
                            text = "${contactItems.itemCount} Contacts",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB),
                            fontFamily = Montserrat,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        if (!permissionState.value) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEFF6FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer_12dp()
                        Text(
                            text = "Find Friends from Contacts",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Montserrat,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Grant contact permission to easily invite your friends and earn rewards together.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            fontFamily = Montserrat,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                        Spacer_16dp()
                        Button(
                            onClick = { launcher.launch(contactPermission) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                        ) {
                            Text("Grant Permission", fontFamily = Montserrat, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            if (contactItems.loadState.refresh is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF2563EB),
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 2.5.dp
                        )
                    }
                }
            } else if (contactItems.itemCount == 0 && contactItems.loadState.refresh is LoadState.NotLoading) {
                item {
                    Text(
                        text = "No contacts found on device.",
                        fontSize = 13.sp,
                        fontFamily = Montserrat,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(contactItems.itemCount) { index ->
                    val contact = contactItems[index]
                    if (contact != null) {
                        val isSelected = viewModel.selectedContacts.any { it.number == contact.number }
                        ContactInviteItem(
                            contact = contact,
                            isSelected = isSelected,
                            onClick = { viewModel.toggleContactSelection(contact) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InviteHeaderCard(code: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimationView(
                    animationResId = R.raw.refer_earn,
                    contentScale = ContentScale.Fit
                )
            }

            Spacer_12dp()

            Text(
                text = "Refer Friends & Earn Cash",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Montserrat,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Share your exclusive code with your network and earn up to ₹100 for each friend who registers!",
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                fontFamily = Montserrat,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer_16dp()
            DashedShareBox(code)
        }
    }
}

@Composable
fun ContactInviteItem(
    contact: ContactUiModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!contact.photoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = contact.photoUri,
                        contentDescription = null,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = contact.firstName.take(1).uppercase(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB),
                            fontFamily = Montserrat
                        )
                    }
                }

                Spacer_12dp()

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${contact.firstName} ${contact.lastName}".trim().ifEmpty { "No Name" },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A),
                        fontFamily = Montserrat,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = maskPhoneNumber(contact.number),
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        fontFamily = Montserrat
                    )
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFF2563EB) else Color.Transparent)
                        .then(
                            if (!isSelected) Modifier.drawBehind {
                                drawCircle(color = Color(0xFFCBD5E1), style = Stroke(width = 2.dp.toPx()))
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            HorizontalDivider(
                thickness = 0.8.dp,
                color = Color(0xFFF1F5F9),
                modifier = Modifier.padding(start = 72.dp)
            )
        }
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
    🛒 Join me on JetShop – your electronics shopping app! ⚡
    💸 Register using referral code: 👉 $referenceCode 👈 and claim your ₹100 welcome bonus!
    📲 Download now: https://pixeldev.in
""".trimIndent()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .drawBehind {
                val strokeWidth = 1.5.dp.toPx()
                drawRoundRect(
                    color = Color(0xFF2563EB),
                    topLeft = Offset(0f, 0f),
                    size = size,
                    style = Stroke(width = strokeWidth, pathEffect = dashEffect),
                    cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx())
                )
            }
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFEFF6FF))
            .clickable {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareMessage)
                }
                context.startActivity(Intent.createChooser(intent, "Share via"))
            }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "CODE: ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                fontFamily = Montserrat
            )
            Text(
                text = referenceCode,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2563EB),
                fontFamily = Montserrat,
                letterSpacing = 1.sp
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Referral Code", referenceCode))
                    Toast.makeText(context, "Code copied: $referenceCode", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Code",
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(18.dp)
            )
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
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
    ) {
        HandleApiState(
            apiState = viewModel.referList,
            showLoader = true,
            onRetry = { viewModel.fetchRefer() },
            navController = navController,
            onSuccess = { data ->
                referResponse = data
            }
        ) {
            val referrals = referResponse?.referrals

            if (!referrals.isNullOrEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(referrals.size) { index ->
                        ReferralHistoryCard(referrals[index])
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LottieAnimationView(
                        animationResId = R.raw.empty_box,
                        text = "No referral history available"
                    )
                }
            }
        }
    }
}

@Composable
fun ReferralHistoryCard(referral: Referral) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFECFDF5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer_12dp()

                Column {
                    Text(
                        text = referral.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A),
                        fontFamily = Montserrat
                    )
                    Text(
                        text = "Joined: ${referral.joinedAt}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        fontFamily = Montserrat
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFECFDF5),
                border = BorderStroke(1.dp, Color(0xFFA7F3D0))
            ) {
                Text(
                    text = "+ $Ruppes${referral.bonusEarned}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF047857),
                    fontFamily = Montserrat,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
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
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
    ) {
        HandleApiState(
            apiState = viewModel.getWalletHistory,
            showLoader = true,
            onRetry = { viewModel.fetchWalletHistory() },
            navController = navController,
            onSuccess = { data ->
                walletResponse = data
            }
        ) {
            val transactions = walletResponse?.transactions

            if (!transactions.isNullOrEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(transactions.size) { index ->
                        WalletTransactionCard(transactions[index])
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LottieAnimationView(
                        animationResId = R.raw.empty_box,
                        text = "No wallet history available"
                    )
                }
            }
        }
    }
}

@Composable
fun WalletTransactionCard(transaction: Transaction) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer_12dp()

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.type,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A),
                        fontFamily = Montserrat
                    )
                    Text(
                        text = transaction.description,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        fontFamily = Montserrat,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFECFDF5),
                border = BorderStroke(1.dp, Color(0xFFA7F3D0))
            ) {
                Text(
                    text = "+ $Ruppes${transaction.amount}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF047857),
                    fontFamily = Montserrat,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

fun maskPhoneNumber(phone: String): String {
    val digits = phone.filter { it.isDigit() }
    return if (digits.length >= 10) {
        val last4 = digits.takeLast(4)
        "•••• ••• $last4"
    } else {
        phone
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarWallet(
    title: String,
    onBackClick: () -> Unit = {},
    balance: String = "0"
) {
    Surface(
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Montserrat,
                    color = Color(0xFF0F172A)
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFEFF6FF),
                border = BorderStroke(1.dp, Color(0xFFDBEAFE)),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Wallet Balance",
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "$Ruppes$balance",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D4ED8),
                        fontFamily = Montserrat
                    )
                }
            }
        }
    }
}