package com.app.ecomapp.utils
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import androidx.annotation.RawRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.app.ecomapp.ui.theme.AppMainTypography
import com.app.ecomapp.ui.theme.Montserrat
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
        onPrivacyClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val primaryColor = Color(0xFF2563EB)
        val textColor = Color(0xFF64748B)

        val annotatedText = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = textColor
                )
            ) {
                append("I agree to the ")
            }

            pushStringAnnotation(tag = "TERMS", annotation = "terms")
            withStyle(
                style = SpanStyle(
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = primaryColor
                )
            ) {
                append("Terms and Conditions")
            }
            pop()

            withStyle(
                style = SpanStyle(
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = textColor
                )
            ) {
                append(" and ")
            }

            pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
            withStyle(
                style = SpanStyle(
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = primaryColor
                )
            ) {
                append("Privacy Policy")
            }
            pop()
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .padding(vertical = 4.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onCheckedChange(!checked) }
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = primaryColor,
                    uncheckedColor = Color(0xFF94A3B8),
                    checkmarkColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(6.dp))

            ClickableText(
                text = annotatedText,
                style = TextStyle(
                    fontFamily = Montserrat,
                    lineHeight = 16.sp
                ),
                onClick = { offset ->
                    val tappedAnnotation = annotatedText.getStringAnnotations(start = offset, end = offset).firstOrNull()
                    when (tappedAnnotation?.tag) {
                        "TERMS" -> onTermsClick()
                        "PRIVACY" -> onPrivacyClick()
                        else -> onCheckedChange(!checked)
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
        modifier: Modifier = Modifier.size(54.dp),
        strokeWidth: Dp = 5.dp,
        trackColor: Color = Color(0xFFEFF6FF),
        gradientColors: List<Color> = listOf(
            Color(0xFF60A5FA), // Light Brand Blue
            Color(0xFF2563EB), // Primary Blue
            Color(0xFF1D4ED8)  // Deep Accent Blue
        )
    ) {
        val transition = rememberInfiniteTransition(label = "gradient_loader_transition")

        // Continuous spin
        val rotation by transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1100, easing = LinearEasing)
            ),
            label = "rotation"
        )

        // Breathing sweep angle (expands & contracts like modern Android loaders)
        val sweepAngle by transition.animateFloat(
            initialValue = 45f,
            targetValue = 275f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "sweep_angle"
        )

        Box(
            modifier = modifier.rotate(rotation),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = strokeWidth.toPx()
                val diameter = size.minDimension - stroke
                val topLeft = Offset(stroke / 2f, stroke / 2f)
                val arcSize = Size(diameter, diameter)

                // 1. Full 360° Background Track
                drawArc(
                    color = trackColor,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )

                // 2. Animated Foreground Gradient Arc
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = gradientColors,
                        center = center
                    ),
                    startAngle = 0f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
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
