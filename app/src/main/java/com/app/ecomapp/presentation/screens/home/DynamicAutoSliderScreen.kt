package com.app.ecomapp.presentation.screens.home

import android.graphics.BitmapFactory
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import com.compose.jetshop.R

// 1. A simple data class for your items
data class CarouselItem(val title: String, val imageRes: Int)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DynamicAutoSliderScreen() {
    val context = LocalContext.current
    val items = remember {
        listOf(
            CarouselItem("Summer Deals", R.drawable.sample_image_1),
            CarouselItem("Electronics", R.drawable.sample_image_1),
            CarouselItem("Electronics", R.drawable.sample_image_1),
            CarouselItem("Fashion", R.drawable.sample_image_1),
            CarouselItem("Groceries", R.drawable.sample_image_1),
            CarouselItem("Groceries", R.drawable.sample_image_1),
            CarouselItem("Groceries", R.drawable.sample_image_1),
            CarouselItem("Groceries", R.drawable.sample_image_1),
            CarouselItem("Groceries", R.drawable.sample_image_1),
            CarouselItem("Groceries", R.drawable.sample_image_1),
            CarouselItem("Groceries", R.drawable.sample_image_1),
        )
    }

    // 1. INFINITE SCROLL LOGIC
    // We set a massive number of pages. 10,000 / 2 starts us in the middle
    // so the user can swipe left or right "forever".
    val startIndex = Int.MAX_VALUE / 2
    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { Int.MAX_VALUE }
    )

    val pageColors = remember { mutableStateMapOf<Int, Color>() }

    // Use modulo (%) to map the massive page index back to your list size
    val actualIndex = pagerState.currentPage % items.size
    val currentTargetColor = pageColors[actualIndex] ?: MaterialTheme.colorScheme.surface

    val animatedBackgroundColor by animateColorAsState(
        targetValue = currentTargetColor,
        animationSpec = tween(durationMillis = 800),
        label = "backgroundColorAnim"
    )

    // 2. UNSTOPPABLE AUTO-SCROLL
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            // Removed the !isScrollInProgress check so it triggers regardless of touch
            // Wrapped in try-catch because rapid manual swipes during animation can
            // occasionally throw a CancellationException which would kill your loop.
            try {
                pagerState.animateScrollToPage(
                    page = pagerState.currentPage + 1,
                    animationSpec = tween(1000, easing = FastOutSlowInEasing)
                )
            } catch (e: Exception) {
                // If animation is interrupted, loop continues to the next delay
            }
        }
    }

    // Extract colors (Optimized to only process unique items once)
    LaunchedEffect(items) {
        withContext(Dispatchers.Default) {
            items.forEachIndexed { index, item ->
                val bitmap = BitmapFactory.decodeResource(context.resources, item.imageRes)
                bitmap?.let {
                    val palette = Palette.from(it).generate()
                    val extractedColor = palette.vibrantSwatch?.rgb ?: palette.dominantSwatch?.rgb
                    if (extractedColor != null) {
                        pageColors[index] = Color(extractedColor)
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        animatedBackgroundColor.copy(alpha = 0.8f),
                        MaterialTheme.colorScheme.background
                    ),
                    startY = 0f, endY = 1500f
                )
            )
    ) {
        Column(modifier = Modifier.padding(top = 100.dp)) {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 32.dp),
                pageSpacing = 16.dp,
                modifier = Modifier.height(500.dp)
            ) { page ->
                // Map the infinite 'page' index to the 0..items.size range
                val item = items[page % items.size]
                val cardColor = pageColors[page % items.size] ?: MaterialTheme.colorScheme.surfaceVariant

                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Image(
                        painter = painterResource(id = item.imageRes),
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}