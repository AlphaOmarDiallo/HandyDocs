package com.alphaomardiallo.handydocs.feature.ocr.domain

import android.content.Context
import android.net.Uri

interface OcrRepository {
    suspend fun analyseFileFromUri(
        uri: Uri,
        context: Context,
        recognitionType: TextRecognitionType
    ): TextAnalysisResult
}
