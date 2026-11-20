package com.app.ecomapp.presentation.screens.home

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.ecomapp.ui.theme.Montserrat
import com.compose.jetshop.R

// --- PIXEL DEV BRAND COLORS ---
private val PixelDevOrange = Color(0xFFFF6B4A)
private val PixelDevGold = Color(0xFFFFAE33)
private val PixelDevPink = Color(0xFFFF2D6C)
private val PixelDevDark = Color(0xFF1E1233)
private val PixelDevCyan = Color(0xFF0284C7)

@Composable
fun DeveloperShowcaseCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val openLink: (String) -> Unit = { url ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 14.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = PixelDevPink.copy(alpha = 0.12f),
                spotColor = PixelDevOrange.copy(alpha = 0.20f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = 1.2.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    PixelDevPink.copy(alpha = 0.45f),
                    PixelDevOrange.copy(alpha = 0.30f),
                    Color(0xFFE2E8F0).copy(alpha = 0.5f)
                )
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
        ) {
            // Ambient Sunset Blur behind top right
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-35).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PixelDevPink.copy(alpha = 0.22f),
                                PixelDevOrange.copy(alpha = 0.10f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Ambient Cyan Glow on bottom left
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-30).dp, y = 30.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PixelDevCyan.copy(alpha = 0.18f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(13.dp),
                                    ambientColor = PixelDevOrange.copy(alpha = 0.35f),
                                    spotColor = PixelDevPink.copy(alpha = 0.45f)
                                )
                                .clip(RoundedCornerShape(13.dp))
                                .border(
                                    1.8.dp,
                                    Brush.linearGradient(listOf(PixelDevGold, PixelDevPink)),
                                    RoundedCornerShape(13.dp)
                                )
                                .background(PixelDevDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.pixeldev_logo),
                                contentDescription = "Pixel Dev Logo",
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Dinesh Chavan",
                                    fontFamily = Montserrat,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.5.sp,
                                    color = PixelDevDark
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Creator",
                                    tint = PixelDevPink,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "Pixel Developer  •  @pixeldesigndeveloper",
                                fontFamily = Montserrat,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    /*Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PixelDevPink.copy(alpha = 0.08f),
                        border = BorderStroke(0.8.dp, PixelDevPink.copy(alpha = 0.25f))
                    ) {
                        Text(
                            text = "Creator",
                            color = PixelDevPink,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Montserrat,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }*/
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CompactActionChip(
                        title = "GitHub",
                        icon = Icons.Default.Code,
                        gradient = listOf(PixelDevDark, Color(0xFF2C1E45)),
                        modifier = Modifier.weight(1f),
                        onClick = { openLink("https://github.com/Dinesh2510") }
                    )

                    CompactActionChip(
                        title = "YouTube",
                        icon = Icons.Default.PlayArrow,
                        gradient = listOf(PixelDevOrange, PixelDevPink),
                        modifier = Modifier.weight(1f),
                        onClick = { openLink("https://www.youtube.com/@pixeldesigndeveloper") }
                    )

                    CompactActionChip(
                        title = "Website",
                        icon = Icons.Default.Language,
                        gradient = listOf(PixelDevCyan, Color(0xFF1E3A8A)),
                        modifier = Modifier.weight(1f),
                        onClick = { openLink("https://pixeldev.in") }
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactActionChip(
    title: String,
    icon: ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
        label = "chipPress"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = gradient.first().copy(alpha = 0.25f),
                spotColor = gradient.last().copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(Brush.horizontalGradient(gradient))
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}