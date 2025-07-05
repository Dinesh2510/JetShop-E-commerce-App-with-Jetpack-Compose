package com.app.ecomapp.data.models.wallet


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Transaction(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("balance_after_transaction")
    val balanceAfterTransaction: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("expiry_date")
    val expiryDate: String,
    @SerializedName("referred_user_id")
    val referredUserId: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("wallet_id")
    val walletId: Int,
    @SerializedName("wallet_transactions_id")
    val walletTransactionsId: Int
) : Parcelable