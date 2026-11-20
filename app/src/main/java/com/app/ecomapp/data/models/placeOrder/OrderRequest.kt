package com.app.ecomapp.data.models.placeOrder

import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("Subtotal") val subtotal: Double,
    @SerializedName("Discount") val discount: Double,
    @SerializedName("Tax") val tax: Double,
    @SerializedName("Delivery Charges") val deliveryCharges: Double,
    @SerializedName("Final Total") val finalTotal: Double,
    @SerializedName("cart_items") val cartItems: List<CartItemJson>,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("shipping_address_id") val shippingAddressId: Int
)

data class CartItemJson(
    @SerializedName("product_id") val productId: String,
    val quantity: Int,
    @SerializedName("quantity_amount_price") val quantityAmountPrice: String,
    @SerializedName("tax_amount") val taxAmount: String,
    @SerializedName("quantity_amount_price_with_tax") val quantityAmountPriceWithTax: String
)
data class Product(
    val product_id: String,
    val quantity: String,
    val quantity_amount_price: String,
    val tax_amount: String,
    var quantity_amount_price_with_tax: String
)
