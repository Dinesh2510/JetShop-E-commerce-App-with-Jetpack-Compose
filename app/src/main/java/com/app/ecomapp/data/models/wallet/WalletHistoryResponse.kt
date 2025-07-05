package com.app.ecomapp.data.models.wallet


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class WalletHistoryResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("transactions")
    val transactions: List<Transaction>
) : Parcelable