package com.app.ecomapp.utils

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import androidx.annotation.RawRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.app.ecomapp.ui.theme.AppMainTypography
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

object CommonFunction {
    /* CustomCheckbox(
                            label = "I agree to the Terms & Conditions",
                            checked = isChecked,
                            onCheckedChange = { isChecked = it }
                        )
                        CircularCheckboxWithLabel()*/

    @Composable
    fun RegisterCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        onTermsClick: () -> Unit,
        onPrivacyClick: () -> Unit
    ) {
        val annotatedText = buildAnnotatedString {
            append("I agree to the ")

            pushStringAnnotation(tag = "TERMS", annotation = "terms")
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                append("Terms and Conditions")
            }
            pop()

            append(" and ")

            pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                append("Privacy Policy")
            }
            pop()
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .clickable { onCheckedChange(!checked) }
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    checkmarkColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            ClickableText(
                text = annotatedText,
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                onClick = { offset ->
                    annotatedText.getStringAnnotations(start = offset, end = offset)
                        .firstOrNull()?.let { annotation ->
                            when (annotation.tag) {
                                "TERMS" -> onTermsClick()
                                "PRIVACY" -> onPrivacyClick()
                            }
                        }
                }
            )
        }
    }

    @Composable
    fun CustomCheckbox(
        label: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(8.dp)
                .clickable { onCheckedChange(!checked) } // Toggles on text click too
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = null, // Handled by Row click
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    checkmarkColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
    @Composable
    fun CircularCheckbox(
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        size: Dp = 24.dp,
        color: Color = MaterialTheme.colorScheme.primary,
        borderColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(if (checked) color else Color.Transparent)
                .border(
                    width = 2.dp,
                    color = if (checked) color else borderColor,
                    shape = CircleShape
                )
                .clickable { onCheckedChange(!checked) },
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Checked",
                    tint = Color.White,
                    modifier = Modifier.size(size / 1.5f)
                )
            }
        }
    }
    @Composable
    fun CircularCheckboxWithLabel(isChecked: Boolean, onCheckedChange: Boolean?) {
        //var isChecked by remember { mutableStateOf(false) }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            CircularCheckbox(
                checked = isChecked,
                onCheckedChange = { onCheckedChange }
            )
            /*Spacer_8dp()
            Text(
                text = "Subscribe to updates",
                style = MaterialTheme.typography.bodyLarge
            )*/
        }
    }
    @Composable
    fun GradientLoader(
        modifier: Modifier = Modifier.size(60.dp),
        strokeWidth: Dp = 6.dp
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "rotation_transition")

        val angle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 800, // faster rotation
                    easing = LinearEasing // no bounce or slow effect
                )
            ),
            label = "angle"
        )
        val CrystalMintTealGradient = listOf(
            Color(0xFF00F260), // Fresh Mint Green
            Color(0xFF00C9A7), // Teal Core
            Color(0xFF0575E6)  // Cool Blue Accent
        )

        val SoftMintGradient = listOf(
            Color(0xFFACEBCA), // Light Leafy Green
            Color(0xFF00C9A7), // Signature Teal
            Color(0xFF1ABC9C)  // Deeper Green-Teal
        )


        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    brush = Brush.linearGradient(
                        colors = SoftMintGradient,
                        start = Offset.Zero,
                        end = Offset(size.width, size.height)
                    ),
                    startAngle = angle,
                    sweepAngle = 270f, // portion of the arc
                    useCenter = false,
                    style = Stroke(
                        width = strokeWidth.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }
        }
    }



    @Composable
    fun LottieAnimationView(
        @RawRes animationResId: Int,
        text: String? = null,
        textStyle: TextStyle = AppMainTypography.subHeader,
        textModifier: Modifier = Modifier.padding(top = 8.dp),
        contentScale: ContentScale = ContentScale.Fit,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val screenWidth = maxWidth
            val animationSize = screenWidth * 1f // 40% of screen width (adjust if needed)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationResId))
                val progress by animateLottieCompositionAsState(
                    composition,
                    iterations = LottieConstants.IterateForever
                )

                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(animationSize),
                    contentScale = contentScale
                )

                text?.let {
                    Text(
                        text = it,
                        style = textStyle,
                        modifier = textModifier
                    )
                }
            }
        }
    }






    fun String.toRequestBody(): RequestBody =
        RequestBody.create("text/plain".toMediaTypeOrNull(), this)
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        connectivityManager?.let {
            val network = it.activeNetwork
            val networkCapabilities = it.getNetworkCapabilities(network)
            return networkCapabilities != null &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
        return false
    }
    fun openLink(mContext: Context, url: String) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(url)
        mContext.startActivity(openURL)
    }

    @Composable
    fun hideKeyboard() {
        val keyboardController = LocalSoftwareKeyboardController.current
        keyboardController?.hide() // Hide the keyboard
    }
    fun getAppVersionName(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }
    }


}
