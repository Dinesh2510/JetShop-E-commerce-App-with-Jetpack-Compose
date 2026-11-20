package com.app.ecomapp.data.models.home


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import android.os.Parcel

@Parcelize
data class HomeResponse(
    @SerializedName("categories")
    val categories: List<CategoryData>,
    @SerializedName("sections")
    val sectionData: List<SectionData>,
    @SerializedName("brands")
    val brandsData: List<BrandData>,
    @SerializedName("slider_images")
    val sliderImageData: List<SliderImageData>,
    @SerializedName("status")
    val status: String
) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }
}