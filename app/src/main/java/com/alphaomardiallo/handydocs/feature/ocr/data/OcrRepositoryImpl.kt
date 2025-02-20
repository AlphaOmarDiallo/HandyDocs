package com.alphaomardiallo.handydocs.feature.ocr.data

import android.content.Context
import android.net.Uri
import com.alphaomardiallo.handydocs.feature.ocr.domain.OcrRepository
import com.alphaomardiallo.handydocs.feature.ocr.domain.TextAnalysisResult
import com.alphaomardiallo.handydocs.feature.ocr.domain.TextRecognitionType
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import timber.log.Timber
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class OcrRepositoryImpl : OcrRepository {

    override suspend fun analyseFileFromUri(
        uri: Uri,
        context: Context,
        recognitionType: TextRecognitionType
    ): TextAnalysisResult {
        return try {
            val image = InputImage.fromFilePath(context, uri)
            val recognizer = getRecognizer(recognitionType)

            suspendCoroutine { continuation ->
                recognizer.process(image)
                    .addOnSuccessListener { text ->
                        continuation.resume(
                            if (text.text.isNotBlank()) TextAnalysisResult.Success(
                                text.text
                            ) else TextAnalysisResult.Error.InvalidImage
                        )
                    }
                    .addOnFailureListener { e ->
                        Timber.e("Error: ${e.localizedMessage}")
                        continuation.resume(TextAnalysisResult.Error.RecognitionError(e))
                    }
            }
        } catch (e: IOException) {
            Timber.e(e.localizedMessage)
            TextAnalysisResult.Error.FileError(e)
        }
    }

    private fun getRecognizer(textRecognitionType: TextRecognitionType): TextRecognizer {
        return when (textRecognitionType) {
            TextRecognitionType.LATIN -> TextRecognition.getClient(
                TextRecognizerOptions.DEFAULT_OPTIONS
            )

            TextRecognitionType.CHINESE -> TextRecognition.getClient(
                ChineseTextRecognizerOptions.Builder().build()
            )

            TextRecognitionType.DEVANAGARI -> TextRecognition.getClient(
                DevanagariTextRecognizerOptions.Builder().build()
            )

            TextRecognitionType.JAPANESE -> TextRecognition.getClient(
                JapaneseTextRecognizerOptions.Builder().build()
            )

            TextRecognitionType.KOREAN -> TextRecognition.getClient(
                KoreanTextRecognizerOptions.Builder().build()
            )
        }
    }
}
