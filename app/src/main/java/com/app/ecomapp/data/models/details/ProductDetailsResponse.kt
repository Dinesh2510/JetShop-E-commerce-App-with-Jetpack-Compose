package com.app.ecomapp.data.models.details


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.app.ecomapp.data.models.home.ProductData

@Parcelize
data class ProductDetailsResponse(
    @SerializedName("cartData")
    val cartData: CartData?,
    @SerializedName("message")
    val message: String,
    @SerializedName("product")
    val product: ProductData,
    @SerializedName("related_products")
    val relatedProducts: List<RelatedProduct>,
    @SerializedName("reviewsData")
    val reviewsData: ReviewsData,
    @SerializedName("status")
    val status: String
) : Parcelable