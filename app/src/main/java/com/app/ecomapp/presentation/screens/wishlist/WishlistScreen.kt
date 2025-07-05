package com.app.ecomapp.presentation.screens.wishlist

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.app.ecomapp.data.Resource
import com.compose.jetshop.R
import com.app.ecomapp.data.models.entity.WishlistProduct

import com.app.ecomapp.ui.theme.AppMainTypography
import com.app.ecomapp.ui.theme.Montserrat
import com.app.ecomapp.utils.CommonFunction.LottieAnimationView
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    navHostController: NavHostController,
    wishlistViewModel: WishlistViewModel = hiltViewModel()
) {
    val wishlist by wishlistViewModel.wishlist.collectAsState()

    Scaffold(
        topBar = {
            MyTopAppBar("Wishlist", onDeleteClick = {
                wishlistViewModel.clearWishlist()
            })
        }
    ) {
        Box(
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {

            if (wishlist.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LottieAnimationView(
                        animationResId = R.raw.empty_wishlist,
                        text =  "No items in Wishlist",)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(wishlist, key = { it.product_id }) { product ->
                        SwipeToDeleteItem(
                            product = product,
                            onRemove = { wishlistViewModel.removeFromWishlist(product) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SwipeToDeleteItem(
    product: WishlistProduct,
    onRemove: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val dismissThreshold = 200f

    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        label = "Swipe Animation"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (offsetX < -dismissThreshold) Color.Red else Color.White,
        label = "Background Color Animation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (offsetX < -dismissThreshold) {
                            onRemove()
                        }
                        offsetX = 0f
                    }
                ) { _, dragAmount ->
                    offsetX = (offsetX + dragAmount).coerceIn(-500f, 0f)
                }
            }
    ) {
        WishlistItem(
            product = product,
            modifier = Modifier.offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
        )
    }
}

@Composable
fun WishlistItem(
    product: WishlistProduct,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ Product Image
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(product.product_image_url)
                        .apply {
                            placeholder(R.drawable.ic_launcher_foreground)
                            crossfade(true)
                        }.build()
                ),
                contentDescription = product.product_name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(12.dp))

            // ✅ Product Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.product_name,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "₹${product.product_price}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // ✅ Remove Button
            IconButton(
                onClick = {},
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Remove from Wishlist",
                    tint = Color.Red
                )
            }
        }
    }
}
@Composable
fun MyTopAppBar(title: String, onDeleteClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        // Title centered
        Text(
            text = title,
           style = AppMainTypography.subHeader,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )

        // Delete icon aligned to the end
        IconButton(
            onClick = { onDeleteClick() },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.Red
            )
        }
    }
}
