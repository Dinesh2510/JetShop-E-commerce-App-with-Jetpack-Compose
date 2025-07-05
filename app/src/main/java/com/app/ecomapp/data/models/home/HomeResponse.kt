package com.app.ecomapp.data.models.home


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

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
) : Parcelable