package com.app.ecomapp.presentation.screens.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.app.ecomapp.data.models.coupon.CouponData
import com.app.ecomapp.data.models.coupon.CouponsResponse
import com.app.ecomapp.presentation.components.ToolbarWithBackButtonAndTitle
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.CommonFunction.LottieAnimationView
import com.app.ecomapp.utils.Constants.Companion.Ruppes
import com.app.ecomapp.utils.HandleApiState
import com.compose.jetshop.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouponCodeScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    var apiData by remember { mutableStateOf<CouponsResponse?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getCouponCode()
    }

    HandleApiState(
        apiState = viewModel.couponsResponse,
        showLoader = true,
        onRetry = { viewModel.getCouponCode() },
        navController = navController,
        onSuccess = { data -> apiData = data }
    ) {
        Scaffold(
            containerColor = Color(0xFFF8FAFC),
            topBar = {
                ToolbarWithBackButtonAndTitle(
                    title = "Available Coupons",
                    onBackClick = { navController.popBackStack() }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .navigationBarsPadding()
            ) {
                val promoCodes = apiData?.data

                if (promoCodes != null && promoCodes.isNotEmpty()) {
                    CouponScreen(promoCodes = promoCodes)
                } else if (apiData != null && promoCodes.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        LottieAnimationView(
                            animationResId = R.raw.empty_box,
                            text = "No coupons available right now"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CouponScreen(promoCodes: List<CouponData>) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(promoCodes) { promo ->
            ModernTicketCouponCard(promoCode = promo, context = context)
        }
    }
}

@Composable
fun ModernTicketCouponCard(promoCode: CouponData, context: Context) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { copyToClipboard(context, promoCode.couponCode) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Voucher Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dashed Code Chip
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                    modifier = Modifier.drawDashedBorder(
                        color = Color(0xFF3B82F6),
                        strokeWidth = 2f,
                        dashLength = 8f,
                        cornerRadius = 10f
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = promoCode.couponCode.uppercase(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = Montserrat,
                            color = Color(0xFF1D4ED8),
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Copy Action Button
                Button(
                    onClick = { copyToClipboard(context, promoCode.couponCode) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "COPY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Montserrat,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Offer Title / Value
            Text(
                text = if (!promoCode.title.isNullOrBlank()) promoCode.title else "Special Discount",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Montserrat,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = promoCode.description,
                fontSize = 13.sp,
                fontFamily = Montserrat,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF475569),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dashed Divider for Ticket Effect
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .drawBehind {
                        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        drawLine(
                            color = Color(0xFFE2E8F0),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.5f,
                            cap = StrokeCap.Round,
                            pathEffect = pathEffect
                        )
                    }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Metadata (Min spend & Expiry)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Minimum Order Tag
                if (!promoCode.minOrderAmount.isNullOrBlank() && promoCode.minOrderAmount != "0") {
                    Text(
                        text = "Min order: $Ruppes${promoCode.minOrderAmount}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF059669),
                        fontFamily = Montserrat
                    )
                } else {
                    Text(
                        text = "No minimum spend",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B),
                        fontFamily = Montserrat
                    )
                }

                // Expiry Date Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Valid till ${promoCode.expiryDate}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Montserrat,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

fun Modifier.drawDashedBorder(
    color: Color,
    strokeWidth: Float,
    dashLength: Float,
    cornerRadius: Float = 0f
) = this.then(
    Modifier.drawBehind {
        val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
            width = strokeWidth,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, dashLength))
        )
        drawRoundRect(
            color = color,
            style = stroke,
            size = size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
        )
    }
)

fun copyToClipboard(context: Context, code: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Coupon Code", code)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Coupon \"$code\" copied to clipboard!", Toast.LENGTH_SHORT).show()
}