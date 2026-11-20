package com.app.ecomapp.utils
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecomapp.ui.theme.Montserrat
// -------------------------------------------------------------
// CUSTOM BRANDED PULL TO REFRESH INDICATOR
// -------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomPullToRefreshIndicator(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier
) {
    val distanceFraction = state.distanceFraction.coerceIn(0f, 1.5f)
    val isVisible = isRefreshing || distanceFraction > 0.1f

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(200)) + scaleIn(spring(dampingRatio = 0.6f)),
        exit = fadeOut(tween(250)) + scaleOut(targetScale = 0.7f),
        modifier = modifier
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulse_radar")

        // Continuous rotation for loader
        val spinAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(900, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "spin_angle"
        )

        // Shimmer gradient for text
        val shimmerTranslate by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 300f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer_badge"
        )

        val animatedScale by animateFloatAsState(
            targetValue = if (distanceFraction >= 1f || isRefreshing) 1.05f else 0.9f + (distanceFraction * 0.1f),
            animationSpec = spring(dampingRatio = 0.5f),
            label = "scale_anim"
        )

        val isTriggered = distanceFraction >= 1f && !isRefreshing

        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFF0F172A), // Premium obsidian dark slate
            shadowElevation = 12.dp,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = if (isTriggered || isRefreshing) {
                        listOf(Color(0xFF38BDF8), Color(0xFF2563EB), Color(0xFF818CF8))
                    } else {
                        listOf(Color(0xFF334155), Color(0xFF1E293B))
                    }
                )
            ),
            modifier = Modifier
                .graphicsLayer {
                    scaleX = animatedScale
                    scaleY = animatedScale
                }
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Interactive Radar Dial
                Box(
                    modifier = Modifier.size(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(28.dp)) {
                        val strokeWidth = 2.8.dp.toPx()

                        // Background track circle
                        drawCircle(
                            color = Color(0xFF1E293B),
                            style = Stroke(width = strokeWidth)
                        )

                        if (isRefreshing) {
                            // Dual-color rotating radar arc
                            drawArc(
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        Color(0xFF2563EB).copy(alpha = 0.1f),
                                        Color(0xFF38BDF8),
                                        Color(0xFF60A5FA)
                                    )
                                ),
                                startAngle = spinAngle,
                                sweepAngle = 270f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        } else {
                            // Interactive progress arc based on pull distance
                            drawArc(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF38BDF8), Color(0xFF2563EB))
                                ),
                                startAngle = -90f,
                                sweepAngle = (distanceFraction * 360f).coerceIn(0f, 360f),
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }

                    // Center Dynamic Icon
                    if (isRefreshing) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8))
                        )
                    } else if (isTriggered) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(15.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.ShoppingBag,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier
                                .size(14.dp)
                                .rotate(distanceFraction * 25f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Text status with slide and crossfade animation
                AnimatedContent(
                    targetState = when {
                        isRefreshing -> "Refreshing Cart..."
                        isTriggered -> "Release to Refresh"
                        else -> "Pull Down to Refresh"
                    },
                    transitionSpec = {
                        fadeIn(tween(180)) togetherWith fadeOut(tween(180))
                    },
                    label = "text_morph"
                ) { statusText ->
                    val textBrush = if (isRefreshing || isTriggered) {
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFE2E8F0),
                                Color(0xFF38BDF8),
                                Color(0xFFE2E8F0)
                            ),
                            start = Offset.Zero,
                            end = Offset(x = shimmerTranslate, y = 0f)
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFE2E8F0), Color(0xFF94A3B8))
                        )
                    }

                    Text(
                        text = statusText,
                        fontFamily = Montserrat,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        style = androidx.compose.ui.text.TextStyle(brush = textBrush)
                    )
                }
            }
        }
    }
}
