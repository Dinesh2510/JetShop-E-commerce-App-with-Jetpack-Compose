package com.app.ecomapp.data.models.home


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class SliderImageData(
    @SerializedName("description")
    val description: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("redirect_url")
    val redirectUrl: String,
    @SerializedName("slider_id")
    val sliderId: String,
    @SerializedName("title")
    val title: String
) : Parcelable