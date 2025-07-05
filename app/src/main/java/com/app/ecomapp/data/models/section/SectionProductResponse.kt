package com.app.ecomapp.data.models.section


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class SectionProductResponse(
    @SerializedName("sections")
    val sectionData: List<SectionData>,
    @SerializedName("status")
    val status: String
) : Parcelable