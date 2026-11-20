package com.app.ecomapp.presentation.screens.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class AddressViewModel : ViewModel() {
    var address by mutableStateOf("")
    var city by mutableStateOf("")
    var state by mutableStateOf("")
    var zipCode by mutableStateOf("")
    var latitude by mutableStateOf(0.0)
    var longitude by mutableStateOf(0.0)
    var googleMapAddress by mutableStateOf("")
    var selectedType by mutableStateOf("")
    var defaultAddress by mutableStateOf(false) // Checkbox state

    var isFormVisible by mutableStateOf(false) // Manage form visibility

    fun setSelectedAddress(markerPosition: LatLng, addressFromLatLng: String) {
        Log.d("TAG", "setSelectedAddress: $markerPosition, $addressFromLatLng")
        googleMapAddress = addressFromLatLng
        latitude = markerPosition.latitude
        longitude = markerPosition.longitude
        Log.d("TAG", "AddressForm data set: googleMapAddress=$googleMapAddress, latitude=$latitude, longitude=$longitude")
    }

    fun resetForm() {
        address = ""
        city = ""
        state = ""
        zipCode = ""
        latitude = 0.0
        longitude = 0.0
        googleMapAddress = ""
        selectedType = ""
        defaultAddress = false
    }

    fun setFormData(
        address: String,
        city: String,
        state: String,
        zipCode: String,
        latitude: Double,
        longitude: Double,
        googleMapAddress: String,
        selectedType: String,
        defaultAddress: Boolean
    ) {
        this.address = address
        this.city = city
        this.state = state
        this.zipCode = zipCode
        this.latitude = latitude
        this.longitude = longitude
        this.googleMapAddress = googleMapAddress
        this.selectedType = selectedType
        this.defaultAddress = defaultAddress
    }
    fun toggleFormVisibility() {
        isFormVisible = !isFormVisible
    }
}
