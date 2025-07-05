package com.app.ecomapp.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.app.ecomapp.presentation.components.CenteredCircularProgressIndicator

@Composable
fun NetworkImage(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    imageSize: Dp = 100.dp
) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .size(imageSize)
            /*.clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)*/,
                contentAlignment = Alignment.Center
    ) {
        val painter = rememberAsyncImagePainter(
            model = url,
            imageLoader = ImageLoader(LocalContext.current),
            onState = { state ->
                isLoading = state is AsyncImagePainter.State.Loading
                hasError = state is AsyncImagePainter.State.Error
            }
        )

        if (isLoading) {
            CenteredCircularProgressIndicator()
        }

        if (hasError) {
            Icon(
                imageVector = Icons.Default.BrokenImage,
                contentDescription = "Image Load Failed",
                modifier = Modifier.align(Alignment.Center),
                tint = Color.Gray
            )
        }

        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = Modifier.matchParentSize().alpha(if (isLoading || hasError) 0f else 1f)
        )
    }

}
