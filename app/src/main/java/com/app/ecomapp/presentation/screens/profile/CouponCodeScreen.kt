package com.app.ecomapp.presentation.screens.profile

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.compose.jetshop.R
import com.app.ecomapp.data.models.coupon.CouponData
import com.app.ecomapp.data.models.coupon.CouponsResponse

import com.app.ecomapp.presentation.components.TitleSmall
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.ui.theme.BackgroundContent
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.CommonFunction.LottieAnimationView
import com.app.ecomapp.utils.HandleApiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouponCodeScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var apiData by remember { mutableStateOf<CouponsResponse?>(null) } // ✅ Store success data

    // ✅ Trigger API call when screen loads
    LaunchedEffect(Unit) {
        viewModel.getCouponCode()
    }

    HandleApiState(
        apiState = viewModel.couponsResponse,
        showLoader = true, // ✅ Enable loader
        navController = navController,
        onSuccess = { data -> apiData = data }
    ) {
        Scaffold(
            topBar = {
                ToolbarWithBackButtonAndTitle("Coupons",
                    onBackClick = { navController.popBackStack() })
            }
        ) { paddingValues ->
            Column(
                Modifier
                    .padding(paddingValues)
                    .background(BackgroundContent)
            ) {
                // ✅ Check if apiData is not null before using it
                apiData?.data?.let { promoCodes ->
                    CouponScreen(promoCodes = promoCodes)
                } ?: run {
                    LottieAnimationView(animationResId = R.raw.empty_box, text = "Coupons not available")
                }
            }
        }
    }
}



// Main Coupon Screen
@Composable
fun CouponScreen(promoCodes: List<CouponData>) {
    val context = LocalContext.current

    if (promoCodes.isEmpty()) {
        EmptyState()
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(promoCodes) { promo ->
                CouponCard(promo, context)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun CouponCard(promoCode: CouponData, context: Context) {

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { copyToClipboard(context, promoCode.couponCode) }
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2196F3), Color(0xFF64B5F6))
                    )
                )
                .padding(16.dp)
        ) {
            // Promo Code Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = promoCode.couponCode,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = Montserrat,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = { copyToClipboard(context, promoCode.couponCode) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(
                        text = "Apply",
                        color = Color(0xFF2196F3),
                        fontSize = 14.sp,
                        fontFamily = Montserrat
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Promo Code Title
            Text(
                text = promoCode.description,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Montserrat,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Expiry Date
            Text(
                text = "Expires on: ${promoCode.expiryDate}",
                fontSize = 14.sp,
                fontFamily = Montserrat,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

// Extension function for dashed border
fun Modifier.drawDashedBorder(
    color: Color,
    strokeWidth: Float,
    dashLength: Float,
) = this.then(Modifier.drawBehind {
    val stroke = Stroke(
        width = strokeWidth,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, dashLength))
    )
    drawRect(
        color = color, style = stroke, size = size
    )
})

// Empty State Design
@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No Promo Codes Available",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray
        )
    }
}

// Function to Copy Code and Show Toast
fun copyToClipboard(context: Context, code: String) {
    val clipboard = android.content.Context.CLIPBOARD_SERVICE
    val clip = android.content.ClipData.newPlainText("Coupon Code", code)
    (context.getSystemService(clipboard) as android.content.ClipboardManager).setPrimaryClip(
        clip
    )
    Toast.makeText(context, "Code Copied: $code", Toast.LENGTH_SHORT).show()
}
