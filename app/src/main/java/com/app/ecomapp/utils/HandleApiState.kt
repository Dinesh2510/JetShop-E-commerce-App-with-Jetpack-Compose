package com.app.ecomapp.utils

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.app.ecomapp.data.Resource
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator
import com.app.ecomapp.presentation.components.ErrorState
import kotlinx.coroutines.flow.StateFlow

private enum class ErrorKind { Network, Server, Other }

private fun classifyError(raw: String?): ErrorKind {
    val m = raw?.lowercase().orEmpty()
    return when {
        "unable to resolve host" in m || "no address associated" in m ||
                "network is unreachable" in m || "network error" in m ||
                "timeout" in m || "timed out" in m || "failed to connect" in m ||
                "software caused connection abort" in m || "connection reset" in m -> ErrorKind.Network

        "http 5" in m || "expected begin_" in m || "unexpected end of stream" in m ||
                "malformed json" in m || "unexpected response" in m ||
                "http 404" in m || "http 403" in m -> ErrorKind.Server

        else -> ErrorKind.Other
    }
}

@Composable
fun <T> HandleApiState(
    apiState: StateFlow<Resource<T?>?>,
    onSuccess: (T?) -> Unit,
    onError: ((String) -> Unit)? = null, // 👈 Local error callback
    navController: NavController,
    modifier: Modifier = Modifier,
    showLoader: Boolean = true,
    showErrorScreen: Boolean = true,     // set false for forms (login, register...)
    onRetry: (() -> Unit)? = null,       // called when "Try Again" is tapped
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val state by apiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var hasLoadedOnce by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        when (val s = state) {
            is Resource.Loading -> {
                isLoading = true
                errorMessage = null
            }
            is Resource.Success -> {
                isLoading = false
                errorMessage = null
                hasLoadedOnce = true
                onSuccess(s.data)
            }
            is Resource.Error -> {
                isLoading = false
                val errorMsg = s.message ?: "Unknown error occurred"

                if (onError != null) {
                    onError(errorMsg) // 👈 Consumed by form UI
                } else {
                    val kind = classifyError(s.message)
                    val fullScreenShown = showErrorScreen && !hasLoadedOnce && kind != ErrorKind.Other
                    if (!fullScreenShown) {
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else -> Log.e("HandleApiState", "state is NULL")
        }
    }

    val currentState = state
    val kind = (currentState as? Resource.Error)?.let { classifyError(it.message) }
    val showError = showErrorScreen && !hasLoadedOnce &&
            currentState is Resource.Error && kind != ErrorKind.Other

    Box(modifier = modifier.fillMaxSize()) {
        if (showError) {
            val isNetwork = kind == ErrorKind.Network
            ErrorState(
                title = if (isNetwork) "No internet connection" else "Something went wrong",
                message = if (isNetwork)
                    "Please check your connection and try again."
                else
                    "We couldn't load this right now. Please try again in a moment.",
                isNetworkError = isNetwork,
                onRetry = onRetry
            )
        } else {
            content()
            if (isLoading && showLoader) {
                CenteredCircularProgressIndicator()
            }
        }
    }
}