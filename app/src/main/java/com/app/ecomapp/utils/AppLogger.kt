package com.app.ecomapp.utils

import android.util.Log
import androidx.compose.ui.res.stringResource
import com.compose.jetshop.R

object AppLogger {

    private const val DEFAULT_TAG = "JetShop" // ✅ Common Tag for all logs

    // ✅ Debug Log
    fun d(tag: String = DEFAULT_TAG, message: String) {
        Log.d(tag, message)
    }

    // ✅ Error Log
    fun e(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
    }

    // ✅ Warning Log
    fun w(tag: String = DEFAULT_TAG, message: String) {
        Log.w(tag, message)
    }

    // ✅ Info Log
    fun i(tag: String = DEFAULT_TAG, message: String) {
        Log.i(tag, message)
    }

    // ✅ Verbose Log
    fun v(tag: String = DEFAULT_TAG, message: String) {
        Log.v(tag, message)
    }
}
