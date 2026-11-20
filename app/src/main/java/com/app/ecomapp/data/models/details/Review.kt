package com.app.ecomapp.data.models.details


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import android.os.Parcel

@Parcelize
data class Review(
    @SerializedName("review_comment")
    val reviewComment: String,
    @SerializedName("review_created_at")
    val reviewCreatedAt: String,
    @SerializedName("review_id")
    val reviewId: String,
    @SerializedName("review_rating")
    val reviewRating: String,
    @SerializedName("review_title")
    val reviewTitle: String,
    @SerializedName("review_user_id")
    val reviewUserId: String ,
    @SerializedName("user_name")
    val user_name: String?
) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }
}