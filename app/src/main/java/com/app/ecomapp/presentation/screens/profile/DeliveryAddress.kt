package com.app.ecomapp.presentation.screens.profile

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.address.AddressRequest
import com.app.ecomapp.data.models.address.AddressUpdateRequest
import com.app.ecomapp.data.models.address.AddresseData
import com.app.ecomapp.presentation.components.CustomOutlinedTextField
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.CommonFunction.LottieAnimationView
import com.app.ecomapp.utils.UserDataStore
import com.compose.jetshop.R

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedAddressScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel(),
    addressViewModel: AddressViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val dataStoreHelper = remember { UserDataStore(context) }
    val userId by dataStoreHelper.getValue(UserDataStore.USER_ID).collectAsState(initial = "")
    val isFormVisible = addressViewModel.isFormVisible

    var addressToUpdate by remember { mutableStateOf<AddresseData?>(null) }
    val currentAddresses = remember { mutableStateListOf<AddresseData>() }

    val addressesListState by viewModel.addressesList.collectAsState()
    val addressResponseState by viewModel.addressResponse.collectAsState()

    // 1. Initial Load - fetch once when screen opens
    LaunchedEffect(Unit) {
        viewModel.getAddresses()
    }

    // 2. Handle Addresses List Response without throwing "No Address" toasts
    LaunchedEffect(addressesListState) {
        when (val state = addressesListState) {
            is Resource.Success -> {
                val list = state.data?.addresses.orEmpty().reversed()
                currentAddresses.clear()
                currentAddresses.addAll(list)
            }
            is Resource.Error -> {
                currentAddresses.clear()
                val message = state.message.orEmpty()
                // Suppress toast for empty addresses so empty UI handles it gracefully
                if (!message.contains("no address", ignoreCase = true) &&
                    !message.contains("empty", ignoreCase = true) &&
                    !message.contains("not found", ignoreCase = true)
                ) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
            else -> {}
        }
    }

    // 3. Handle Add / Update / Delete Response cleanly (prevents repeat toasts on returning from maps)
    LaunchedEffect(addressResponseState) {
        when (val state = addressResponseState) {
            is Resource.Success -> {
                val message = state.data?.message ?: "Operation successful"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                // If form was visible, close and reset it
                if (addressViewModel.isFormVisible) {
                    addressViewModel.resetForm()
                    addressViewModel.toggleFormVisibility()
                    addressToUpdate = null
                }

                // Immediately re-fetch updated addresses from server
                viewModel.getAddresses()

                // Reset state in ViewModel if available to prevent re-triggering
                try {
                    val resetMethod = viewModel.javaClass.getMethod("resetAddressResponse")
                    resetMethod.invoke(viewModel)
                } catch (_: Exception) { }
            }
            is Resource.Error -> {
                Toast.makeText(context, state.message ?: "Failed to process request", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        contentWindowInsets = WindowInsets.statusBars,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Saved Addresses",
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0F172A)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isFormVisible) Color(0xFFF1F5F9) else Color(0xFFEFF6FF),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable {
                                addressViewModel.toggleFormVisibility()
                                addressViewModel.resetForm()
                                addressToUpdate = null
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (isFormVisible) Icons.Default.Close else Icons.Default.Add,
                                contentDescription = "Toggle Form",
                                tint = if (isFormVisible) Color(0xFF64748B) else Color(0xFF2563EB),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isFormVisible) "Close" else "Add New",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = Montserrat,
                                color = if (isFormVisible) Color(0xFF64748B) else Color(0xFF2563EB)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                color = Color(0xFF1E293B),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tip: Tap any address card to make it your primary delivery address",
                        color = Color.White,
                        maxLines = 1,
                        fontSize = 12.sp,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.basicMarquee()
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                addressesListState is Resource.Loading && currentAddresses.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF2563EB))
                    }
                }
                currentAddresses.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LottieAnimationView(
                            animationResId = R.raw.empty_box,
                            text = "No addresses saved yet!"
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = currentAddresses,
                            key = { it.id }
                        ) { address ->
                            AddressCard(
                                address = address,
                                onDeleteClick = { addressId ->
                                    // Optimistically remove from list immediately so UI updates instantly
                                    currentAddresses.removeAll { it.id.toString() == addressId }
                                    viewModel.deleteAddress(addressId)
                                },
                                onEditClick = { _ ->
                                    addressToUpdate = address
                                    addressViewModel.setFormData(
                                        address = address.address,
                                        city = address.city,
                                        state = address.state,
                                        zipCode = address.zipCode,
                                        latitude = address.latitude.toString().toDoubleOrNull() ?: 0.0,
                                        longitude = address.longitude.toString().toDoubleOrNull() ?: 0.0,
                                        googleMapAddress = address.googleMapAddress.orEmpty(),
                                        selectedType = address.type ?: "Home",
                                        defaultAddress = address.default_address == "1"
                                    )
                                    addressViewModel.toggleFormVisibility()
                                },
                                onClickCardView = { addressId ->
                                    viewModel.setDefaultAddress(addressId)
                                }
                            )
                        }
                    }
                }
            }

            // Dimmed Overlay
            if (isFormVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.55f))
                        .clickable {
                            addressViewModel.resetForm()
                            addressViewModel.toggleFormVisibility()
                            addressToUpdate = null
                        }
                )
            }

            // Form Container - Pinned to bottom to completely eliminate dead space above the keyboard
            AnimatedVisibility(
                visible = isFormVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter // Pins to bottom above keyboard
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .imePadding() // Adjusts directly with keyboard height
                            .navigationBarsPadding()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        AddressForm(
                            context = context,
                            onSubmit = { address ->
                                viewModel.addAddress(address)
                            },
                            onCancel = {
                                addressViewModel.resetForm()
                                addressViewModel.toggleFormVisibility()
                                addressToUpdate = null
                            },
                            onOpenGoogleMaps = {
                                navController.navigate(Screen.GoogleLocation.route) {
                                    launchSingleTop = true
                                }
                            },
                            userId = userId,
                            viewModel = viewModel,
                            navController = navController,
                            addressToUpdate = addressToUpdate,
                            addressViewModel = addressViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddressForm(
    context: Context,
    onSubmit: (AddressRequest) -> Unit,
    onCancel: () -> Unit,
    onOpenGoogleMaps: () -> Unit,
    userId: String?,
    viewModel: ProfileViewModel,
    navController: NavController,
    addressToUpdate: AddresseData? = null,
    addressViewModel: AddressViewModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val address = rememberSaveable { mutableStateOf(addressToUpdate?.address ?: addressViewModel.address) }
    val city = rememberSaveable { mutableStateOf(addressToUpdate?.city ?: addressViewModel.city) }
    val state = rememberSaveable { mutableStateOf(addressToUpdate?.state ?: addressViewModel.state) }
    val zipCode = rememberSaveable { mutableStateOf(addressToUpdate?.zipCode ?: addressViewModel.zipCode) }
    val googleMapAddress = rememberSaveable { mutableStateOf(addressToUpdate?.googleMapAddress ?: addressViewModel.googleMapAddress) }
    val defaultAddress = rememberSaveable { mutableStateOf(addressToUpdate?.default_address?.toBoolean() ?: addressViewModel.defaultAddress) }
    val selectedType = rememberSaveable { mutableStateOf(addressToUpdate?.type ?: if (addressViewModel.selectedType.isNotBlank()) addressViewModel.selectedType else "Home") }
    val latitude = rememberSaveable {
        mutableStateOf(addressToUpdate?.latitude?.toDoubleOrNull() ?: (addressViewModel.latitude ?: 0.0))
    }
    val longitude = rememberSaveable {
        mutableStateOf(addressToUpdate?.longitude?.toDoubleOrNull() ?: (addressViewModel.longitude ?: 0.0))
    }
    val addressTypes = listOf("Home", "Office", "Other")

    // Sync Google Maps result when returning from GoogleLocationScreen
    LaunchedEffect(Unit) {
        val selectedAddressResult = navController.currentBackStackEntry?.savedStateHandle?.get<String>("selectedAddress")
        val googleMapAddressResult = navController.currentBackStackEntry?.savedStateHandle?.get<String>("googleMapAddress")

        if (selectedAddressResult != null) {
            val coords = selectedAddressResult.split(",").map { it.trim().toDoubleOrNull() ?: 0.0 }
            if (coords.size >= 2) {
                latitude.value = coords[0]
                longitude.value = coords[1]
                addressViewModel.latitude = coords[0]
                addressViewModel.longitude = coords[1]
            }
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("selectedAddress")
        }

        if (googleMapAddressResult != null) {
            googleMapAddress.value = googleMapAddressResult
            addressViewModel.googleMapAddress = googleMapAddressResult
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("googleMapAddress")
        }
    }

    Card(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = false) {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (addressToUpdate != null) "Edit Address" else "New Address",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Montserrat,
                        color = Color(0xFF0F172A)
                    )
                }

                IconButton(
                    onClick = {
                        keyboardController?.hide()
                        onCancel()
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            HorizontalDivider(thickness = 0.8.dp, color = Color(0xFFF1F5F9))

            CustomOutlinedTextField(
                value = address.value,
                onValueChange = {
                    address.value = it
                    addressViewModel.address = it
                },
                label = "Flat / House No. / Building / Street",
                leadingIcon = Icons.Default.Home,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    CustomOutlinedTextField(
                        value = city.value,
                        onValueChange = {
                            city.value = it
                            addressViewModel.city = it
                        },
                        label = "City",
                        leadingIcon = Icons.Default.LocationCity,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    CustomOutlinedTextField(
                        value = zipCode.value,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() } && it.length <= 6) {
                                zipCode.value = it
                                addressViewModel.zipCode = it
                            }
                        },
                        label = "Pincode",
                        leadingIcon = Icons.Default.Pin,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            CustomOutlinedTextField(
                value = state.value,
                onValueChange = {
                    state.value = it
                    addressViewModel.state = it
                },
                label = "State",
                leadingIcon = Icons.Default.Map,
                modifier = Modifier.fillMaxWidth()
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        keyboardController?.hide()
                        onOpenGoogleMaps()
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Locate on Map",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontFamily = Montserrat
                        )
                        Text(
                            text = if (googleMapAddress.value.isNotBlank()) googleMapAddress.value else "Tap to choose exact GPS location",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (googleMapAddress.value.isNotBlank()) Color(0xFF0F172A) else Color(0xFF2563EB),
                            fontFamily = Montserrat,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Save Address As",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569),
                    fontFamily = Montserrat
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    addressTypes.forEach { type ->
                        val isSelected = selectedType.value.equals(type, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC),
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedType.value = type
                                    addressViewModel.selectedType = type
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (type) {
                                        "Home" -> Icons.Default.Home
                                        "Office" -> Icons.Default.Apartment
                                        else -> Icons.Default.LocationOn
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) Color(0xFF2563EB) else Color(0xFF64748B),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = type,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = Montserrat,
                                    color = if (isSelected) Color(0xFF1E3A8A) else Color(0xFF475569)
                                )
                            }
                        }
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8FAFC),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        defaultAddress.value = !defaultAddress.value
                        addressViewModel.defaultAddress = defaultAddress.value
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = defaultAddress.value,
                        onCheckedChange = {
                            defaultAddress.value = it
                            addressViewModel.defaultAddress = it
                        },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2563EB))
                    )
                    Text(
                        text = "Set as default shipping address",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E293B),
                        fontFamily = Montserrat
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        keyboardController?.hide()
                        onCancel()
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Cancel",
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B)
                    )
                }

                Button(
                    onClick = {
                        if (validateForm(
                                context,
                                address.value,
                                city.value,
                                state.value,
                                zipCode.value,
                                latitude.value.toString(),
                                longitude.value.toString(),
                                googleMapAddress.value,
                                selectedType.value,
                                defaultAddress.value.toString()
                            )
                        ) {
                            keyboardController?.hide()
                            if (addressToUpdate == null) {
                                val request = AddressRequest(
                                    userId = userId.orEmpty(),
                                    address = address.value,
                                    city = city.value,
                                    state = state.value,
                                    zip_code = zipCode.value,
                                    country = "India",
                                    latitude = latitude.value,
                                    longitude = longitude.value,
                                    type = selectedType.value,
                                    google_address = googleMapAddress.value,
                                    default_address = (if (defaultAddress.value) 1 else 0).toString()
                                )
                                onSubmit(request)
                            } else {
                                val request = AddressUpdateRequest(
                                    addressId = addressToUpdate.id.toString(),
                                    userId = userId.orEmpty(),
                                    address = address.value,
                                    city = city.value,
                                    state = state.value,
                                    zip_code = zipCode.value,
                                    country = "India",
                                    latitude = latitude.value.toString(),
                                    longitude = longitude.value.toString(),
                                    type = selectedType.value,
                                    google_address = googleMapAddress.value
                                )
                                viewModel.updateAddress(request)
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp)
                ) {
                    Text(
                        text = if (addressToUpdate == null) "Save Address" else "Update Address",
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

private fun validateForm(
    context: Context,
    address: String,
    city: String,
    state: String,
    zipCode: String,
    latitude: String,
    longitude: String,
    googleMapAddress: String,
    type: String,
    defaultAddress: String
): Boolean {
    return when {
        address.isBlank() -> showToast(context, "Address is required")
        city.isBlank() -> showToast(context, "City is required")
        state.isBlank() -> showToast(context, "State is required")
        zipCode.isBlank() || zipCode.length < 6 -> showToast(context, "Valid 6-digit Pincode is required")
        latitude.isBlank() || longitude.isBlank() || latitude == "0.0" -> showToast(
            context, "Please select location on map"
        )
        googleMapAddress.isBlank() -> showToast(context, "Google Map Address is required")
        type.isBlank() -> showToast(context, "Select Address Type")
        defaultAddress.isBlank() -> showToast(context, "Select default Address")
        else -> true
    }
}

private fun showToast(context: Context, message: String): Boolean {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    return false
}

@Composable
fun AddressCard(
    address: AddresseData,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onClickCardView: (String) -> Unit,
) {
    val isDefault = address.default_address == "1"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickCardView(address.id.toString()) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            width = if (isDefault) 1.5.dp else 1.dp,
            color = if (isDefault) Color(0xFF2563EB) else Color(0xFFE2E8F0)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFEFF6FF)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = when (address.type?.lowercase()) {
                                    "home" -> Icons.Default.Home
                                    "office" -> Icons.Default.Apartment
                                    else -> Icons.Default.LocationOn
                                },
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = (address.type ?: "Home").uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB),
                                fontFamily = Montserrat
                            )
                        }
                    }

                    if (isDefault) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFECFDF5),
                            border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "DEFAULT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF059669),
                                    fontFamily = Montserrat
                                )
                            }
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFF6FF))
                            .clickable { onEditClick(address.id.toString()) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Address",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFF1F2))
                            .clickable { onDeleteClick(address.id.toString()) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete Address",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = address.address.trim(),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A),
                fontFamily = Montserrat,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${address.city}, ${address.state} - ${address.zipCode}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF475569),
                fontFamily = Montserrat
            )

            Text(
                text = address.country,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                fontFamily = Montserrat
            )

            if (!address.googleMapAddress.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF8FAFC)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = address.googleMapAddress,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontFamily = Montserrat,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}