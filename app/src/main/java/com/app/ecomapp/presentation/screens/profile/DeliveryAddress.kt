package com.app.ecomapp.presentation.screens.profile

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.IndeterminateCheckBox
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

import com.compose.jetshop.R
import com.app.ecomapp.data.models.address.AddressRequest
import com.app.ecomapp.data.models.address.AddressResponse
import com.app.ecomapp.data.models.address.AddressUpdateRequest
import com.app.ecomapp.data.models.address.AddresseData
import com.app.ecomapp.presentation.components.CustomOutlinedTextField

import com.app.ecomapp.presentation.components.TitleSmall
import com.app.ecomapp.presentation.navigation.Screen
import com.app.ecomapp.ui.theme.AppMainTypography
import com.app.ecomapp.ui.theme.DarkBlue
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.ui.theme.ProductTypography
import com.app.ecomapp.utils.CommonFunction.LottieAnimationView
import com.app.ecomapp.utils.HandleApiState
import com.app.ecomapp.utils.UserDataStore

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedAddressScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel(),
    addressViewModel: AddressViewModel = hiltViewModel()
) {
    var addressesListState by remember { mutableStateOf<AddressResponse?>(null) } // Store success data
    var addressToUpdate by remember { mutableStateOf<AddresseData?>(null) } // State to hold address for update

    val context = LocalContext.current
    val dataStoreHelper = remember { UserDataStore(context) } // Instantiate DataStoreHelper
    val userId by dataStoreHelper.getValue(UserDataStore.USER_ID).collectAsState(initial = "")
    val isFormVisible = addressViewModel.isFormVisible

    // HandleApiState for getting addresses
    HandleApiState(apiState = viewModel.addressesList, // Pass the API state
        showLoader = true, // Enable/disable loader
        navController = navController, onSuccess = { data ->
            // Save the list of addresses when the API call is successful
            addressesListState = data
        }) {
        LaunchedEffect(Unit) {
            // Call the getAddresses function when the screen is launched
            viewModel.getAddresses() // Fetch addresses from the repository
        }

        // HandleApiState for Add address submission
        HandleApiState(apiState = viewModel.addressResponse, // Pass the API state
            showLoader = true, // Enable/disable loader
            navController = navController, onSuccess = { data ->
                addressViewModel.resetForm()
                Toast.makeText(context, "Address Submitted", Toast.LENGTH_LONG).show()
                addressViewModel.toggleFormVisibility()
            }) {

            Scaffold(topBar = {
                CenterAlignedTopAppBar(title = { TitleSmall(text = "Address") }, navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                }, actions = {
                    IconButton(onClick = {
                        addressViewModel.toggleFormVisibility()
                        addressViewModel.resetForm()
                        addressToUpdate = null // Reset the address data when closing form
                    }) {
                        if (isFormVisible) Icon(
                            Icons.Default.IndeterminateCheckBox, contentDescription = "Back"
                        ) else Icon(Icons.Default.AddBox, contentDescription = "Back")
                    }
                })
            }, bottomBar = {
                Column(
                    Modifier
                        .padding(
                            WindowInsets.navigationBars.asPaddingValues() // ✅ Prevents overlap
                        )
                        .background(DarkBlue)
                ) {
                    Text(
                        "\uD83D\uDCCC Note: Tap on an address to set it as your default ✅",
                        Modifier
                            .padding(8.dp)
                            .basicMarquee(),
                        color = Color.White,
                        maxLines = 1,
                        style = AppMainTypography.subHeader
                    )
                }
            }) { padding ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Safe check for null or empty address list
                    val addresses = addressesListState?.addresses?.reversed() ?: emptyList()

                    if (addresses.isEmpty()) {
                        LottieAnimationView(animationResId = R.raw.empty_box, text = "Address not available!")

                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            items(addresses) { address ->
                                AddressCard(
                                    address = address,
                                    onDeleteClick = { addressId ->
                                        viewModel.deleteAddress(addressId)
                                    }, onEditClick = { addressId ->
                                        // Set the selected address to addressToUpdate for editing
                                        addressToUpdate = address // Set address data for updating
                                        addressViewModel.toggleFormVisibility() // Show the form
                                    }, onClickCardView = { addressId ->
                                        viewModel.setDefaultAddress(addressId)
                                    })
                            }
                        }
                    }
                }

                // Dim background when form is visible
                if (isFormVisible) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)) // Semi-transparent overlay
                    )
                }

                // Animated Visibility for the Form
                AnimatedVisibility(
                    visible = isFormVisible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState()), // Allow scrolling if needed
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AddressForm(
                                context = context,
                                onSubmit = { address ->
                                    viewModel.addAddress(address) // Assuming you add the address to a repository or database
                                },
                                onCancel = {
                                    addressViewModel.resetForm()
                                    addressViewModel.toggleFormVisibility() // Hide form when cancel is clicked
                                    addressToUpdate = null // Reset the address data
                                },
                                onOpenGoogleMaps = {
                                    // Navigate to GoogleMapScreen and expect a result
                                    navController.navigate(Screen.GoogleLocation.route) {
                                        launchSingleTop = true
                                    }
                                },
                                userId = userId,
                                viewModel = viewModel,
                                navController = navController,
                                addressToUpdate = addressToUpdate // Pass the address data here
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressForm(
    context: Context,
    onSubmit: (AddressRequest) -> Unit,
    onCancel: () -> Unit,
    onOpenGoogleMaps: () -> Unit,
    userId: String?,
    viewModel: ProfileViewModel,
    navController: NavController,
    addressToUpdate: AddresseData? = null // Add address to update parameter
) {
    val address = rememberSaveable { mutableStateOf(addressToUpdate?.address ?: "") }
    val city = rememberSaveable { mutableStateOf(addressToUpdate?.city ?: "") }
    val state = rememberSaveable { mutableStateOf(addressToUpdate?.state ?: "") }
    val zipCode = rememberSaveable { mutableStateOf(addressToUpdate?.zipCode ?: "") }
    val googleMapAddress =
        rememberSaveable { mutableStateOf(addressToUpdate?.googleMapAddress ?: "") }
    val defaultAddress =
        rememberSaveable { mutableStateOf(addressToUpdate?.default_address?.toBoolean() ?: false) }
    val selectedType = rememberSaveable { mutableStateOf(addressToUpdate?.type ?: "") }
    val latitude = rememberSaveable { mutableStateOf(addressToUpdate?.latitude ?: 0.0) }
    val longitude = rememberSaveable { mutableStateOf(addressToUpdate?.longitude ?: 0.0) }
    val addressTypes = listOf("Home", "Office", "Other")

    // In SavedAddressScreen
    LaunchedEffect(key1 = Unit) {
        val selectedAddressResult =
            navController.currentBackStackEntry?.savedStateHandle?.get<String>(
                "selectedAddress"
            )
        val googleMapAddressResult =
            navController.currentBackStackEntry?.savedStateHandle?.get<String>(
                "googleMapAddress"
            )

        if (selectedAddressResult != null) {
            val (lat, lng) = selectedAddressResult.split(",")
                .map { it.trim().toDoubleOrNull() ?: 0.0 }
            latitude.value = lat
            longitude.value = lng
        }

        if (googleMapAddressResult != null) {
            googleMapAddress.value = googleMapAddressResult
        }

        navController.currentBackStackEntry?.savedStateHandle?.remove<String>("selectedAddress")
        navController.currentBackStackEntry?.savedStateHandle?.remove<String>("googleMapAddress")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TitleSmall(if (addressToUpdate != null) "Update Address" else "Add Address")

        CustomOutlinedTextField(
            value = address.value,
            onValueChange = { address.value = it },
            label = "Address",
            leadingIcon = Icons.Default.Home,
            modifier = Modifier.fillMaxWidth()
        )

        CustomOutlinedTextField(
            value = city.value,
            onValueChange = { city.value = it },
            label = "City",
            leadingIcon = Icons.Default.LocationCity,
            modifier = Modifier.fillMaxWidth()
        )

        CustomOutlinedTextField(
            value = state.value,
            onValueChange = { state.value = it },
            label = "State",
            leadingIcon = Icons.Default.Map,
            modifier = Modifier.fillMaxWidth()
        )

        CustomOutlinedTextField(
            value = zipCode.value,
            onValueChange = { if (it.all { char -> char.isDigit() }) zipCode.value = it },
            label = "Zip Code",
            leadingIcon = Icons.Default.Pin,
            modifier = Modifier.fillMaxWidth()
        )

        CustomOutlinedTextField(
            value = googleMapAddress.value,
            onValueChange = {},
            label = "Google Map Address",
            leadingIcon = Icons.Default.Place,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onOpenGoogleMaps()
                },
            isEnabled = false
        )

        // Latitude and Longitude fields
        /*  CustomOutlinedTextField(
              value = latitude.value.toString(),
              onValueChange = {
              },
              label = "Latitude",
              leadingIcon = Icons.Default.Place,
              modifier = Modifier.fillMaxWidth()
          )

          CustomOutlinedTextField(
              value = longitude.value.toString(),
              onValueChange = {

              },
              label = "Longitude",
              leadingIcon = Icons.Default.Place,
              modifier = Modifier.fillMaxWidth()
          )*/

        Column(modifier = Modifier.padding(top = 5.dp)) {
            Text(text = "Select Address Type", style = MaterialTheme.typography.bodyMedium)
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                addressTypes.forEach { addressType ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        RadioButton(
                            selected = selectedType.value == addressType,
                            onClick = { selectedType.value = addressType }
                        )
                        Text(
                            text = addressType,
                            fontFamily = Montserrat,
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = defaultAddress.value,
                onCheckedChange = { defaultAddress.value = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Default Address")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                        val request = if (addressToUpdate == null) {
                            // Add new address
                            AddressRequest(
                                userId = userId!!,
                                address = address.value,
                                city = city.value,
                                state = state.value,
                                zip_code = zipCode.value,
                                country = "India",
                                latitude = latitude.value as Double,
                                longitude = longitude.value as Double,
                                type = selectedType.value,
                                google_address = googleMapAddress.value,
                                default_address = (if (defaultAddress.value) 1 else 0).toString()
                            )
                        } else {
                            // Update existing address
                            AddressUpdateRequest(
                                addressId = addressToUpdate.id.toString(),
                                userId = userId!!,
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
                        }
                        // Submit either add or update
                        if (addressToUpdate == null) {
                            onSubmit(request as AddressRequest) // Add new address
                        } else {
                            viewModel.updateAddress(request as AddressUpdateRequest) // Update existing address
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text(if (addressToUpdate == null) "Submit" else "Update")
            }

            OutlinedButton(
                onClick = { onCancel() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                border = BorderStroke(1.dp, Color.Red)
            ) {
                Text("Cancel")
            }
        }
    }
}


// Validation Function
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
        address.isEmpty() -> showToast(context, "Address is required")
        city.isEmpty() -> showToast(context, "City is required")
        state.isEmpty() -> showToast(context, "State is required")
        zipCode.isEmpty() -> showToast(context, "Zip Code is required")
        latitude.isEmpty() || longitude.isEmpty() -> showToast(
            context, "Latitude & Longitude are required"
        )

        googleMapAddress.isEmpty() -> showToast(context, "Google Map Address is required")
        type.isEmpty() -> showToast(context, "Select Address Type")
        defaultAddress.isEmpty() -> showToast(context, "Select default Address")
        else -> true
    }
}

// Show Toast Message
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = {
                onClickCardView(address.id.toString())
            }),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            Column {
                // Address details
                Text(text = address.address, style = ProductTypography.prodTitleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${address.city}, ${address.state}, ${address.zipCode}",
                    style = ProductTypography.prodTitleMedium,
                    color = Color.Gray
                )
                Text(text = address.country,  style = ProductTypography.prodTitleMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Google Map Address: ${address.googleMapAddress}",
                    style = ProductTypography.prodTitleMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Type: ${address.type}",  style = ProductTypography.prodTitleMedium, color = Color.Black)
            }

            // Top Right Icons (Edit & Delete) placed at the end of the card
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(0.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { onEditClick(address.id.toString()) }) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Address",
                        tint = Color.Blue
                    )
                }
                IconButton(onClick = { onDeleteClick(address.id.toString()) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Address",
                        tint = Color.Red
                    )
                }
            }

            // Bottom Right Green Checkmark for Default Address
            if (address.default_address == "1") {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Default Address",
                    tint = DarkBlue,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                )
            }
        }
    }
}

