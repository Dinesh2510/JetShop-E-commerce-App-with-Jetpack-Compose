package com.app.ecomapp.data.models.blogs


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class BlogResponse(
    @SerializedName("data")
    val `data`: List<BlogData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) : Parcelable