package com.app.ecomapp.presentation.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun getCurrentLocation(context: Context, fusedLocationClient: FusedLocationProviderClient): LatLng? {
    return suspendCoroutine { continuation ->
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            continuation.resume(null)
            return@suspendCoroutine
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                continuation.resume(LatLng(location.latitude, location.longitude))
            } else {
                continuation.resume(null)
            }
        }.addOnFailureListener {
            continuation.resume(null)
        }
    }
}
