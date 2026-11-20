package com.app.ecomapp.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private fun isNetworkMessage(raw: String?): Boolean {
    val m = raw?.lowercase().orEmpty()
    return "unable to resolve host" in m || "no address associated" in m ||
            "network is unreachable" in m || "network error" in m ||
            "timeout" in m || "timed out" in m || "failed to connect" in m ||
            "connection abort" in m || "connection reset" in m
}

fun friendlyErrorMessage(raw: String?, fallback: String = "Something went wrong. Please try again."): String {
    val m = raw?.lowercase().orEmpty()
    return when {
        "unable to resolve host" in m || "no address associated" in m ||
                "network is unreachable" in m || "network error" in m ||
                "failed to connect" in m || "connection abort" in m ||
                "connection reset" in m -> "No internet connection. Please check your network and try again."

        "timeout" in m || "timed out" in m -> "The request timed out. Please try again."

        "http 5" in m || "expected begin_" in m || "unexpected end of stream" in m ||
                "malformed json" in m -> "Server is not responding right now. Please try again later."

        raw.isNullOrBlank() -> fallback
        else -> raw   // business message from the API, show as is
    }
}
@Composable
fun ApiErrorView(
    message: String?,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    val isNetwork = isNetworkMessage(message)
    ErrorState(
        title = if (isNetwork) "No internet connection" else "Something went wrong",
        message = if (isNetwork)
            "Please check your connection and try again."
        else
            "We couldn't load this right now. Please try again in a moment.",
        isNetworkError = isNetwork,
        modifier = modifier,
        onRetry = onRetry
    )
}