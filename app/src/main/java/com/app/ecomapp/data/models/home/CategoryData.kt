package com.app.ecomapp.data.models.home


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CategoryData(
    @SerializedName("brief")
    val brief: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("draft")
    val draft: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("priority")
    val priority: String
) : Parcelable