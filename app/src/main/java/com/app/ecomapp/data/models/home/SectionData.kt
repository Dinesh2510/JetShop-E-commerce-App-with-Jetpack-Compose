package com.app.ecomapp.data.models.home


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class SectionData(
    @SerializedName("products")
    val productData: List<ProductData>,
    @SerializedName("section_id")
    val sectionId: String,
    @SerializedName("section_name")
    val sectionName: String
) : Parcelable