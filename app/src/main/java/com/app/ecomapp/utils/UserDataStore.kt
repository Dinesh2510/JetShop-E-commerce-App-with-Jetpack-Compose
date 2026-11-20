package com.app.ecomapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.app.ecomapp.data.models.address.UserAddressData
import com.app.ecomapp.data.models.appInfo.AppInfoData
import com.app.ecomapp.data.models.auth.UserData
import com.app.ecomapp.presentation.screens.profile.AppThemeOption
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserDataStore(private val context: Context) {

    companion object {
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_FIRST_NAME = stringPreferencesKey("user_f_name")
        val USER_LAST_NAME = stringPreferencesKey("user_l_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PHONE = stringPreferencesKey("user_phone")
        val USER_PROFILE = stringPreferencesKey("profile_image")
        val isUserPrimeActive = stringPreferencesKey("isPrimeActive")
        val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val KEY_IS_SKIPPED = booleanPreferencesKey("is_skipped")
        val APP_INFO_KEY = stringPreferencesKey("app_info_json")
        val THEME_KEY = stringPreferencesKey("theme_mode")
        val REFERRAL_CODE = stringPreferencesKey("referral_code")
        val REFERRED_BY = stringPreferencesKey("referred_by")
        val WALLET_BALANCE = stringPreferencesKey("wallet_balance")

        // Key for storing default address
        val DEFAULT_ADDRESS_JSON = stringPreferencesKey("default_address_json")

        suspend fun saveTheme(context: Context, theme: AppThemeOption) {
            context.dataStore.edit {
                it[THEME_KEY] = theme.name
            }
        }

        fun getTheme(context: Context): Flow<AppThemeOption> =
            context.dataStore.data.map {
                AppThemeOption.valueOf(it[THEME_KEY] ?: AppThemeOption.SYSTEM.name)
            }

        suspend fun saveNotificationEnabled(context: Context, isEnabled: Boolean) {
            context.dataStore.edit { prefs ->
                prefs[booleanPreferencesKey("notifications_enabled")] = isEnabled
            }
        }

        fun getNotificationEnabled(context: Context): Flow<Boolean> {
            return context.dataStore.data.map { prefs ->
                prefs[booleanPreferencesKey("notifications_enabled")] ?: true
            }
        }
    }

    // Single source of truth for saving AppInfo
    suspend fun saveAppInfoJson(json: String) {
        context.dataStore.edit { preferences ->
            preferences[APP_INFO_KEY] = json
        }
    }

    // Reactive flow matching the exact key and model type
    val getAppInfoFlow: Flow<AppInfoData?> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            val json = preferences[APP_INFO_KEY]
            if (!json.isNullOrBlank()) {
                try {
                    Gson().fromJson(json, AppInfoData::class.java)
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }

    val appInfoJsonFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[APP_INFO_KEY] }

    suspend fun getAppInfoObject(): AppInfoData? {
        val json = appInfoJsonFlow.firstOrNull()
        return json?.let {
            try {
                Gson().fromJson(it, AppInfoData::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun saveLoginState(context: Context, isLoggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = isLoggedIn
            prefs[KEY_IS_SKIPPED] = false
        }
    }

    suspend fun saveSkipState(context: Context) {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_SKIPPED] = true
            prefs[KEY_IS_LOGGED_IN] = false
        }
    }

    suspend fun isUserLoggedIn(context: Context): Boolean {
        return context.dataStore.data.map { prefs ->
            prefs[KEY_IS_LOGGED_IN] ?: false
        }.first()
    }

    suspend fun isUserSkipped(context: Context): Boolean {
        return context.dataStore.data.map { prefs ->
            prefs[KEY_IS_SKIPPED] ?: false
        }.first()
    }

    suspend fun saveUser(
        userId: String,
        name: String,
        email: String,
        phone: String,
        first_name: String,
        last_name: String,
        user_profile: String?,
        isPrimeActive: String,
        referralCode: String?,
        referredBy: String?,
        walletBalance: Double,
        defaultAddress: UserAddressData? = null
    ) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            preferences[USER_NAME] = name
            preferences[USER_FIRST_NAME] = first_name
            preferences[USER_LAST_NAME] = last_name
            preferences[USER_EMAIL] = email
            preferences[USER_PHONE] = phone
            preferences[USER_PROFILE] = user_profile ?: ""
            preferences[isUserPrimeActive] = isPrimeActive
            preferences[REFERRAL_CODE] = referralCode ?: ""
            preferences[REFERRED_BY] = referredBy ?: ""
            preferences[WALLET_BALANCE] = walletBalance.toString()

            if (defaultAddress != null) {
                preferences[DEFAULT_ADDRESS_JSON] = Gson().toJson(defaultAddress)
            } else {
                preferences.remove(DEFAULT_ADDRESS_JSON)
            }
        }
    }

    suspend fun saveDefaultAddress(address: UserAddressData?) {
        context.dataStore.edit { preferences ->
            if (address != null) {
                preferences[DEFAULT_ADDRESS_JSON] = Gson().toJson(address)
            } else {
                preferences.remove(DEFAULT_ADDRESS_JSON)
            }
        }
    }

    val getUserData: Flow<UserData?> = context.dataStore.data.map { preferences ->
        val userId = preferences[USER_ID] ?: return@map null
        val name = preferences[USER_NAME].orEmpty()
        val email = preferences[USER_EMAIL].orEmpty()
        val phone = preferences[USER_PHONE].orEmpty()
        val fName = preferences[USER_FIRST_NAME].orEmpty()
        val lName = preferences[USER_LAST_NAME].orEmpty()
        val userProfile = preferences[USER_PROFILE].orEmpty()
        val isActivePrime = preferences[isUserPrimeActive]?.toIntOrNull() ?: 0
        val referralCode = preferences[REFERRAL_CODE]
        val referredBy = preferences[REFERRED_BY]
        val walletBalance = preferences[WALLET_BALANCE]?.toDoubleOrNull() ?: 0.0

        val addressJson = preferences[DEFAULT_ADDRESS_JSON]
        val defaultAddress = addressJson?.let {
            try {
                Gson().fromJson(it, UserAddressData::class.java)
            } catch (e: Exception) {
                null
            }
        }

        UserData(
            userId = userId,
            email = email,
            name = name,
            firstName = fName,
            lastName = lName,
            phone = phone,
            profileImage = userProfile,
            isPrimeActive = isActivePrime,
            referralCode = referralCode,
            referredBy = referredBy,
            walletBalance = walletBalance,
            defaultAddress = defaultAddress
        )
    }

    suspend fun clearUserData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun saveValue(key: Preferences.Key<String>, value: String) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    fun getValue(key: Preferences.Key<String>): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[key]
        }
    }

    suspend fun clearValue(key: Preferences.Key<String>) {
        context.dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }
}