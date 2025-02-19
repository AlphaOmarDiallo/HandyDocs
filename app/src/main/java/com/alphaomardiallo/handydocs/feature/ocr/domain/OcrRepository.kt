package com.alphaomardiallo.handydocs.feature.ocr.domain

import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.text.TextRecognizer

interface OcrRepository {
    suspend fun getRecognizer(textRecognitionType: TextRecognitionType = TextRecognitionType.LATIN): TextRecognizer
    suspend fun analyseImageFromMedia(imageProxy: ImageProxy): String
    suspend fun analyseFileFromUri(uri: Uri, context: Context): String
}
