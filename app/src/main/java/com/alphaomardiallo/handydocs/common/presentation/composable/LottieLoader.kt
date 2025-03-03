package com.alphaomardiallo.handydocs.common.presentation.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.alphaomardiallo.handydocs.R

@Composable
fun LottieWithCoilPlaceholder(
    modifier: Modifier = Modifier,
    iterateForever: Boolean = true,
    image: Int = R.drawable.rounded_folder_open_24,
    lottieJson: Int = R.raw.file_anim,
    size: Dp = 48.dp
) {
    var isLoading by remember { mutableStateOf(true) }
    var showPlaceholder by remember { mutableStateOf(true) }

    Box(modifier = modifier.wrapContentWidth(), contentAlignment = Alignment.Center) {
        if (showPlaceholder) {
            AsyncImage(
                model = image,
                contentDescription = "Placeholder Image",
                modifier = Modifier.fillMaxSize(),
                onLoading = {
                    isLoading = true
                    showPlaceholder = true
                },
                onSuccess = {
                    isLoading = false
                    showPlaceholder = false
                },
                onError = {
                    isLoading = false
                    showPlaceholder = true
                }
            )
        }

        if (!isLoading) {
            LottieAnimation(
                composition = rememberLottieComposition(
                    spec = LottieCompositionSpec.RawRes(lottieJson)
                ).value,
                iterations = if (iterateForever) LottieConstants.IterateForever else 1,
                modifier = Modifier.size(size),
            )
        }
    }
}
