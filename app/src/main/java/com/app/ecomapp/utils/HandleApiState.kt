package com.app.ecomapp.utils

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.compose.jetshop.R
import com.app.ecomapp.data.Resource
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator
import kotlinx.coroutines.flow.StateFlow

@Composable
fun <T> HandleApiState(
    apiState: StateFlow<Resource<T?>?>, // API response state
    onSuccess: (T?) -> Unit, // ✅ Callback to return success data to the UI
    navController: NavController,
    modifier: Modifier = Modifier,
    showLoader: Boolean = true, // ✅ User decides whether to show a loader
    content: @Composable () -> Unit // UI Content
) {
    val context = LocalContext.current
    val state by apiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state) {
        when (state) {
            is Resource.Loading -> {
                isLoading = true
                errorMessage = null
            }

            is Resource.Success -> {
                isLoading = false
                val data = (state as Resource.Success<T?>).data
                onSuccess(data) // ✅ Send the API response data back to the screen
            }

            is Resource.Error -> {
                isLoading = false
                errorMessage = (state as Resource.Error).message
                // Show error message in a Toast
                Toast.makeText(context, errorMessage ?: "Unknown error occurred", Toast.LENGTH_SHORT).show()
            }

            else -> {
                Log.e("HandleApiState", "HandleApiState: state is NULL.....")
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        content() // ✅ Render UI
        // ✅ Show loader only if enabled by the user
        if (isLoading && showLoader) {
            CenteredCircularProgressIndicator()
        }
    }
}
