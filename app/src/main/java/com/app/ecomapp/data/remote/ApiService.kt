package com.app.ecomapp.data.remote

import com.app.ecomapp.data.models.cart.CartResponse
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.coupon.CouponsResponse
import com.app.ecomapp.data.models.ProductResponse
import com.app.ecomapp.data.models.auth.RegisterRequest
import com.app.ecomapp.data.models.auth.RegisterResponse
import com.app.ecomapp.data.models.auth.UserResponse
import com.app.ecomapp.data.models.address.AddressResponse
import com.app.ecomapp.data.models.appInfo.AppInfoResponse
import com.app.ecomapp.data.models.blogs.BlogResponse
import com.app.ecomapp.data.models.checkout.OrderDetailsResponse
import com.app.ecomapp.data.models.coupon.ApplyCouponCodeResponse
import com.app.ecomapp.data.models.details.ProductDetailsResponse
import com.app.ecomapp.data.models.home.CategoryDetailsResponse
import com.app.ecomapp.data.models.home.HomeResponse
import com.app.ecomapp.data.models.order.UserOrderResponse
import com.app.ecomapp.data.models.placeOrder.OrderRequest
import com.app.ecomapp.data.models.placeOrder.PlaceOrderDetailsResponse
import com.app.ecomapp.data.models.placeOrder.PlaceOrderResponse
import com.app.ecomapp.data.models.placeOrder.PurchasePrimeRequest
import com.app.ecomapp.data.models.refer.ReferResponse
import com.app.ecomapp.data.models.wallet.WalletHistoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @POST("register.php")
    suspend fun registerUser(@Body registerRequest: RegisterRequest): RegisterResponse

    @GET("get_app_info.php")
    suspend fun getAppInfo(): AppInfoResponse

    @FormUrlEncoded
    @POST("verify_otp.php")
    suspend fun verifyOtp(
        @Field("email") email: String,
        @Field("otp") otp: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login.php")
    suspend fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("forgot_password.php")
    suspend fun forgotPassword(
        @Field("email") email: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("reset_password.php")
    suspend fun resetPassword(
        @Field("email") email: String,
        @Field("otp") otp: String,
        @Field("new_password") new_password: String
    ): RegisterResponse

    @Multipart
    @POST("update_user.php")
    suspend fun updateUser(
        @Part("user_id") userId: RequestBody,
        @Part("first_name") fname: RequestBody,
        @Part("last_name") lname: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part profile_image: MultipartBody.Part? // optional
    ): CommonResponse

    @FormUrlEncoded
    @POST("update_fcm_token.php")
    suspend fun updateFcmToken(
        @Field("user_id") userId: String,
        @Field("fcm_token") fcmToken: String
    ): CommonResponse

    @FormUrlEncoded
    @POST("get_user.php")
    suspend fun getUserData(
        @Field("user_id") user_id: String
    ): UserResponse

    @GET("get_slider_category_product.php")
    suspend fun getSliderCategoryProduct(
        @Query("user_id") userId: String?
    ): HomeResponse
    @FormUrlEncoded
    @POST("get_productDetails.php")
    suspend fun getProductDetails(
        @Field("user_id") user_id: String,
        @Field("product_id") product_id: String,
    ): ProductDetailsResponse


    @FormUrlEncoded
    @POST("add_to_cart.php")
    suspend fun addToCart(
        @Field("user_id") user_id: String,
        @Field("product_id") product_id: String,
        @Field("quantity") quantity: String
    ): CommonResponse

    @FormUrlEncoded
    @POST("remove_from_cart.php")
    suspend fun removeFromCart(
        @Field("user_id") user_id: String,
        @Field("product_id") product_id: String,
        @Field("quantity") quantity: String
    ): CommonResponse

    @FormUrlEncoded
    @POST("get_cart_items.php")
    suspend fun getCartList(
        @Field("user_id") user_id: String
    ): CartResponse

    @FormUrlEncoded
    @POST("delete_cart_item.php")
    suspend fun deleteCart(
        @Field("cart_id") cartId: String
    ): CommonResponse


    @GET("getCouponCode.php")
    suspend fun getCouponCode(): CouponsResponse


    @FormUrlEncoded
    @POST("add_address.php")
    suspend fun addAddress(
        @Field("user_id") userId: String,
        @Field("address") address: String,
        @Field("city") city: String,
        @Field("state") state: String,
        @Field("zip_code") zipCode: String,
        @Field("country") country: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("type") type: String,
        @Field("google_address") google_address: String,
        @Field("default_address") default_address: String
    ): CommonResponse

    @FormUrlEncoded
    @POST("update_address.php")
    suspend fun updateAddress(
        @Field("address_id") addressId: String,
        @Field("user_id") userId: String,
        @Field("address") address: String,
        @Field("city") city: String,
        @Field("state") state: String,
        @Field("zip_code") zipCode: String,
        @Field("country") country: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("google_address") google_address: String,
        @Field("type") type: String
    ):CommonResponse

    @FormUrlEncoded
    @POST("list_addresses.php")
    suspend fun listAddresses(
        @Field("user_id") userId: String
    ): AddressResponse

    @FormUrlEncoded
    @POST("delete_address.php")
    suspend fun deleteAddress(
        @Field("address_id") address_id: String,
        @Field("user_id") user_id: String
    ): CommonResponse

    @FormUrlEncoded
    @POST("setDefaultAddress.php")
    suspend fun setDefaultAddress(
        @Field("address_id") address_id: String,
        @Field("user_id") user_id: String
    ): CommonResponse

    @FormUrlEncoded
    @POST("add_update_review.php")
    suspend fun add_update_review(
        @Field("user_id") userId: String,
        @Field("product_id") productId: String,
        @Field("rating") rating: Float,
        @Field("title") title: String,
        @Field("comment") comment: String?
    ): CommonResponse

    @GET("search.php")
    suspend fun searchProducts(
        @Query("product_name") productName: String,
        @Query("rating") rating: String
    ): ProductResponse

    @GET("list_products_by_category.php")
    suspend fun listProductsByCategory(
        @Query("category_id") category_id: String): CategoryDetailsResponse

    @GET("list_products_by_brand.php")
    suspend fun listProductsByBrand(
        @Query("brand_id") brand_id: String): CategoryDetailsResponse

    @GET("all_product_list.php")
    suspend fun getAllProducts(@Query("page") page: Int): ProductResponse

    @FormUrlEncoded
    @POST("GetOrderDetails.php")
    suspend fun getOrderDetails(
        @Field("user_id") userId: String,
        @Field("coupon_code") couponCode: String?
    ): OrderDetailsResponse

    @POST("place_order.php") // Change to your actual API endpoint
    suspend fun placeOrder(@Body orderRequest: OrderRequest):PlaceOrderResponse


    @FormUrlEncoded
    @POST("get_placeorder_details.php") // Change to your actual API endpoint
    suspend fun getPlaceOrderDetails(
        @Field("order_id") order_id: String,
        @Field("coupon_code") coupon_code: String,
    ): PlaceOrderDetailsResponse

    @FormUrlEncoded
    @POST("apply_coupon.php") // Change to your actual API endpoint
    suspend fun applyCoupon(
        @Field("order_id") order_id: String,
        @Field("coupon_code") coupon_code: String,
    ): ApplyCouponCodeResponse


    @FormUrlEncoded
    @POST("remove_coupon.php") // Change to your actual API endpoint
    suspend fun removeCoupon(
        @Field("order_id") order_id: String,
        @Field("coupon_code") coupon_code: String,
    ): ApplyCouponCodeResponse

    @FormUrlEncoded
    @POST("initiate_payment.php")
    suspend fun initiatePayment(
        @Field("order_id") orderId: String,
        @Field("user_id") userId: String,
        @Field("payment_method") paymentMethod: String,
        @Field("amount") amount: String,  // Convert to String to ensure correct format
        @Field("currency") currency: String
    ): CommonResponse

    @FormUrlEncoded
    @POST("verify_payment.php")
    suspend fun verifyPayment(
        @Field("user_id") user_id: String,
        @Field("order_id") orderId: String,
        @Field("transaction_id") transactionId: String,
        @Field("status") status: String,
        @Field("gateway_response") gatewayResponse: String,
        @Field("planName") planName: String,
        @Field("planPrice") planPrice: String,
    ): CommonResponse


    @FormUrlEncoded
    @POST("cancel_order.php")
    suspend fun cancelOrder(
        @Field("user_id") userId: String,
        @Field("order_id") orderId: String
    ): CommonResponse

    @FormUrlEncoded
    @POST("getUserOrder.php")
    suspend fun getUserOrders(
        @Field("user_id") userId: String,
        @Field("order_id") orderId: String
    ): UserOrderResponse

    @GET("get_blogs.php")
    suspend fun getBlogs(): BlogResponse

    @Headers("Content-Type: application/json")
    @POST("purchase_prime_membership.php")
    suspend fun purchasePrimeMembership(
        @Body request: PurchasePrimeRequest
    ):CommonResponse

    //referral_history
    @FormUrlEncoded
    @POST("referral_history.php")
    suspend fun getReferralHistory(
        @Field("user_id") userId: String
    ): ReferResponse

    @FormUrlEncoded
    @POST("wallet_history.php")
    suspend fun getWalletHistory(
        @Field("user_id") userId: String
    ): WalletHistoryResponse


}
