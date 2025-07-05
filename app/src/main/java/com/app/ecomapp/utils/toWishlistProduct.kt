package com.app.ecomapp.utils

import com.app.ecomapp.data.models.home.ProductData
import com.app.ecomapp.data.models.entity.WishlistProduct

fun ProductData.toWishlistProduct(): WishlistProduct {
    return WishlistProduct(
        product_id = this.productId,
        product_name = this.productName,
        product_description = this.productDescription,
        product_price = this.productPrice.toDoubleOrNull() ?: 0.0,  // Convert String to Double
        product_discount_price = this.productDiscountPrice.toDoubleOrNull() ?: 0.0,
        product_stock_quantity = this.productStockQuantity,
        product_category_id = this.productCategoryId,
        product_brand_id = this.productBrandId,
        product_weight = this.productWeight,
        product_dimensions = this.productDimensions,
        product_color = this.productColor,
        product_size = this.productSize,
        product_rating = this.productRating.toFloatOrNull(),
        product_total_reviews = this.productTotalReviews,
        product_image_url = this.productImageUrl,
        product_thumbnail_url = this.productThumbnailUrl,
        product_is_featured = this.productIsFeatured == 1,  // Convert Int to Boolean
        product_is_active = this.productIsActive == 1,  // Convert Int to Boolean
        product_created_at = this.productCreatedAt,
        product_updated_at = this.productUpdatedAt
    )
}
