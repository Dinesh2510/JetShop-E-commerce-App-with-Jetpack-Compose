package com.app.ecomapp.data.models.home


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import android.os.Parcel

@Parcelize
data class SectionData(
    @SerializedName("products")
    val productData: List<ProductData>,
    @SerializedName("section_id")
    val sectionId: String,
    @SerializedName("section_name")
    val sectionName: String
) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }
}