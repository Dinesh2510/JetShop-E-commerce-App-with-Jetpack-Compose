package com.app.ecomapp.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

object ProductTypography {

    // ✅ Product Title (16sp, Medium/SemiBold)
    val prodTitleMedium = TextStyle(
        fontSize = 14.sp,
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold
    )

    // ✅ Product Subtitle (14sp, Normal)
    val prodSubtitle = TextStyle(
        fontSize = 14.sp,
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal
    )


    // ✅ Product Price (16sp, Bold) – Use with color = MaterialTheme.colorScheme.primary
    val prodPriceBold = TextStyle(
        fontSize = 14.sp,
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold
    )

    // ✅ Discount Price (14sp, Strikethrough)
    val prodDiscountPrice = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = Montserrat,
        textDecoration = TextDecoration.LineThrough
    )

    // ✅ Product Description (13–14sp, Light/Normal)
    val prodDescription = TextStyle(
        fontSize = 13.sp,
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal
    )

    // ✅ Rating Text (12sp)
    val prodRating = TextStyle(
        fontSize = 12.sp,
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal
    )
    // ✅ Product Quantity
    val productQuantity = TextStyle(
        fontSize = 14.sp,
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium)

    val categoryName = TextStyle(
        fontSize = 14.sp,
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium)
    val sectionTitleBold = TextStyle(
        fontSize = 16.sp,
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold
    )

    val labelSmall = TextStyle(
        fontSize = 12.sp,
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal
    )

}
/*Text("JetShop", style = AppMainTypography.appTitle)

Text("Featured Products", style = AppMainTypography.sectionHeader)

Text("Browse items below", style = AppMainTypography.bodyText)

Text("Limited time offer", style = AppMainTypography.caption)

Text("Almond Milk", style = ProductTypography.prodTitleMedium)

Text("1 Litre", style = ProductTypography.prodSubtitle)

Text("₹249", style = ProductTypography.prodPriceBold)

Text("₹299", style = ProductTypography.prodDiscountPrice)

Text("Organic and healthy", style = ProductTypography.prodDescription)

Text("4.8 ★", style = ProductTypography.prodRating)
*/

/*🛒 Product Listing Items
Element	Suggested Size	FontWeight	Style
Product Title	16sp	Medium–SemiBold	bodyLarge
Product Subtitle	14sp	Normal	bodyMedium
Price	16sp	Bold	bodyLarge + colorPrimary
Discount Price	14sp	Normal + Strikethrough	bodyMedium
Description	13–14sp	Light/Normal	bodySmall
Rating Text	12sp	Normal	labelSmall or caption





*/