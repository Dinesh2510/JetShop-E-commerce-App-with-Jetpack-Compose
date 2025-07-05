package com.app.ecomapp.data.models.details


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ReviewsData(
    @SerializedName("fiveStarReviewCount")
    val fiveStarReviewCount: Int,
    @SerializedName("fourStarReviewCount")
    val fourStarReviewCount: Int,
    @SerializedName("oneStarReviewCount")
    val oneStarReviewCount: Int,
    @SerializedName("reviewList")
    val reviewList: List<Review>,
    @SerializedName("threeStarReviewCount")
    val threeStarReviewCount: Int,
    @SerializedName("totalReviewCount")
    val totalReviewCount: Int,
    @SerializedName("twoStarReviewCount")
    val twoStarReviewCount: Int
) : Parcelable