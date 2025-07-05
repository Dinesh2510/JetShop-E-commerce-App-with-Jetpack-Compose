package com.app.ecomapp.data.models.appInfo


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class AppInfoData(
    @SerializedName("app_currency")
    val appCurrency: String,
    @SerializedName("app_language")
    val appLanguage: String,
    @SerializedName("contact_number")
    val contactNumber: String,
    @SerializedName("faq_url")
    val faqUrl: String,
    @SerializedName("home_notice_message")
    val homeNoticeMessage: String,
    @SerializedName("is_maintenance_mode")
    val isMaintenanceMode: Boolean,
    @SerializedName("is_update_required")
    val isUpdateRequired: Boolean,
    @SerializedName("maintenance_message")
    val maintenanceMessage: String,
    @SerializedName("privacy_url")
    val privacyUrl: String,
    @SerializedName("promo_banner_link")
    val promoBannerLink: String,
    @SerializedName("promo_banner_url")
    val promoBannerUrl: String,
    @SerializedName("show_login_popup")
    val showLoginPopup: Boolean,
    @SerializedName("support_email")
    val supportEmail: String,
    @SerializedName("terms_url")
    val termsUrl: String,
    @SerializedName("update_message")
    val updateMessage: String,
    @SerializedName("version_code")
    val versionCode: Int,
    @SerializedName("version_name")
    val versionName: String,
    @SerializedName("razorpay_key")
    val razorpay_key: String
) : Parcelable