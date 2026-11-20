package com.app.ecomapp.data.models.details

import android.os.Parcelable
import android.os.Parcel
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReviewsData(
    @SerializedName("fiveStarReviewCount")
    val fiveStarReviewCount: Int = 0,

    @SerializedName("fourStarReviewCount")
    val fourStarReviewCount: Int = 0,

    @SerializedName("threeStarReviewCount")
    val threeStarReviewCount: Int = 0,

    @SerializedName("twoStarReviewCount")
    val twoStarReviewCount: Int = 0,

    @SerializedName("oneStarReviewCount")
    val oneStarReviewCount: Int = 0,

    @SerializedName("totalReviewCount")
    val totalReviewCount: Int = 0,

    @SerializedName("reviewList")
    val reviewList: List<Review> = emptyList()
) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }
}