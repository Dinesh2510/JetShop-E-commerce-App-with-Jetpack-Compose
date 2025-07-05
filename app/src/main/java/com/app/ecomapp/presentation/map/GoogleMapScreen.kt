package com.app.ecomapp.presentation.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.ecomapp.presentation.components.Spacer_8dp
import com.app.ecomapp.presentation.screens.profile.AddressViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleMapScreen(navController: NavController,    addressViewModel: AddressViewModel = hiltViewModel() // Using ViewModel for shared state
) {
    val context = LocalContext.current

    // Store camera position
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.7749, -122.4194), 15f) // Default location
    }

    // Store marker position
    var markerPosition by remember { mutableStateOf(LatLng(37.7749, -122.4194)) }

    // Marker state
    val markerState = rememberMarkerState(position = markerPosition)

    // Store selected address
    var selectedAddress by remember { mutableStateOf("Select a Location") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedAddress, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    // Pass the selected address back to the previous screen
                    val result = "${markerPosition.latitude}, ${markerPosition.longitude}"
                    navController.previousBackStackEntry?.savedStateHandle?.set("selectedAddress", result)
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "googleMapAddress", getAddressFromLatLng(context, markerPosition)
                    )
                    navController.popBackStack() // Navigate back to the previous screen
                    Log.d(
                        "GoogleMapScreen",
                        "Marker Position: $markerPosition, Address: ${getAddressFromLatLng(
                            context,
                            markerPosition
                        )}")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Confirm Location")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true), // Enable My Location
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false, // Disable default zoom buttons
                    myLocationButtonEnabled = false // Hide default location button
                )
            ) {
                Marker(
                    state = markerState,
                    title = "Selected Location",
                    draggable = true, // Enable dragging
                    onClick = { false }
                )
            }

            // Floating Action Button for moving to current location
            FloatingActionButton(
                onClick = {
                    getCurrentLocation(context) { location ->
                        location?.let {
                            markerPosition = LatLng(it.latitude, it.longitude)
                            markerState.position = markerPosition
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(markerPosition, 15f)
                            selectedAddress = getAddressFromLatLng(context, markerPosition)
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 80.dp), // Position above button
                containerColor = Color.White
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location", tint = Color.Black)
            }

            // Custom Zoom In/Out Buttons
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 80.dp)
            ) {
                IconButton(
                    onClick = {
                        cameraPositionState.move(CameraUpdateFactory.zoomIn())
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Zoom In")
                }

                Spacer_8dp()

                IconButton(
                    onClick = {
                        cameraPositionState.move(CameraUpdateFactory.zoomOut())
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
                }
            }
        }

        // Update marker position when dragging stops
        LaunchedEffect(markerState.position) {
            markerPosition = markerState.position
            selectedAddress = getAddressFromLatLng(context, markerPosition)  // Get address from new position
            Log.d("GoogleMapScreen", "New Position: ${markerPosition.latitude}, ${markerPosition.longitude}")
        }
    }
}

// Function to get current location
private fun getCurrentLocation(context: Context, callback: (Location?) -> Unit) {
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        callback(null)
        return
    }
    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
        callback(location)
    }
}


