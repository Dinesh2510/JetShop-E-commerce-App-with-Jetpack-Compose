package com.app.ecomapp.presentation.screens.profile

import android.app.Application
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecomapp.presentation.screens.profile.getCacheSize
import com.app.ecomapp.utils.UserDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val _themeState = MutableStateFlow(ThemeState())
    val themeState = _themeState.asStateFlow()

    val notificationEnabled = mutableStateOf(true)

    private val _cacheSize = mutableStateOf("0 MB")
    val cacheSize: State<String> = _cacheSize

    fun loadCacheSize() {
        _cacheSize.value = getCacheSize(context)
    }
    private val context = application.applicationContext



    fun clearCache(onDone: () -> Unit) {
        viewModelScope.launch {
            delay(1000) // show loader briefly
            context.cacheDir.deleteRecursively()
            loadCacheSize()
            onDone()
        }
    }
    init {
        // Load cache size immediately
        loadCacheSize()

        // Collect all user preferences in parallel using coroutine scope
        viewModelScope.launch {
            launch {
                UserDataStore.getTheme(application).collect { theme ->
                    _themeState.value = ThemeState(theme)
                }
            }

            launch {
                UserDataStore.getNotificationEnabled(application).collect { isEnabled ->
                    notificationEnabled.value = isEnabled
                }
            }
        }
    }



    fun setTheme(theme: AppThemeOption) {
        viewModelScope.launch {
            UserDataStore.saveTheme(getApplication(), theme)
        }
    }
    fun toggleNotification(value: Boolean) {
        notificationEnabled.value = value
        viewModelScope.launch {
            UserDataStore.saveNotificationEnabled(getApplication(), value)
        }
    }


}

enum class AppThemeOption { LIGHT, DARK, SYSTEM }

data class ThemeState(
    val appTheme: AppThemeOption = AppThemeOption.SYSTEM
)
private fun getCacheSize(context: Context): String {
    fun getDirSize(dir: File?): Long {
        var size = 0L
        dir?.listFiles()?.forEach {
            size += if (it.isDirectory) getDirSize(it) else it.length()
        }
        return size
    }

    val internalCache = getDirSize(context.cacheDir)
    val externalCache = getDirSize(context.externalCacheDir)

    val totalSizeInBytes = internalCache + externalCache
    val sizeInMB = totalSizeInBytes / (1024.0 * 1024.0)
    return String.format("%.2f MB", sizeInMB)
}
