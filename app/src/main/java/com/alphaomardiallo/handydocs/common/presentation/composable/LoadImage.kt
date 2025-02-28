package com.alphaomardiallo.handydocs.common.presentation.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.CachePolicy
import coil.request.ImageRequest
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@Composable
fun LoadImage(url: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val imageLoader = ImageLoader.Builder(context)
        .components { add(SvgDecoder.Factory()) } // Enables SVG support
        .okHttpClient {
            OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
        }
        .build()

    Card(
        modifier = Modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors()
            .copy(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(1.dp, Color.Gray),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(url)
                .crossfade(true)
                .diskCachePolicy(CachePolicy.ENABLED)
                .networkCachePolicy(CachePolicy.ENABLED)
                .build(),
            imageLoader = imageLoader,
            contentDescription = "Loaded Image",
            modifier = modifier
        )
    }
}
