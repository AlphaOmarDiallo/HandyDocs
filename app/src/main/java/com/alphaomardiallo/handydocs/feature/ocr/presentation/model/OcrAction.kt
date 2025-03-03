package com.alphaomardiallo.handydocs.feature.ocr.presentation.model

data class OcrAction(
    val name: Int,
    val cd: Int,
    val icon: Int,
    val lottie: Int,
    val onClick: () -> Unit
)
