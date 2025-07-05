package com.app.ecomapp.data.models.blogs


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class BlogData(
    @SerializedName("blog_id")
    val blogId: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("published_date")
    val publishedDate: String,
    @SerializedName("subtitle")
    val subtitle: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("view_count")
    val viewCount: Int
) : Parcelable