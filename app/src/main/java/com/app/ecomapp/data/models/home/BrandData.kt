package com.app.ecomapp.data.models.home


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import android.os.Parcel

@Parcelize
data class BrandData(
    @SerializedName("brand_description")
    val brandDescription: String,
    @SerializedName("brand_id")
    val brandId: String,
    @SerializedName("brand_logo")
    val brandLogo: String,
    @SerializedName("brand_name")
    val brandName: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("website_url")
    val websiteUrl: String
) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }
}