package com.alphaomardiallo.handydocs.feature.ocr.data

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.alphaomardiallo.handydocs.feature.ocr.domain.OcrRepository
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

    override suspend fun getRecognizer(textRecognitionType: TextRecognitionType): TextRecognizer {
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

    @OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override suspend fun analyseImageFromMedia(imageProxy: ImageProxy): String {
        var textAn = ""
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            getRecognizer().process(image)
                .addOnSuccessListener { text -> textAn = text.text }
                .addOnFailureListener { e -> Timber.e(e.toString()) }
                .addOnCompleteListener { imageProxy.close() }
        } else {
            imageProxy.close()
        }

        return textAn
    }

    override suspend fun analyseFileFromUri(uri: Uri, context: Context): String {
        return try {
            val image = InputImage.fromFilePath(context, uri)
            val recognizer = getRecognizer()

            suspendCoroutine { continuation ->
                recognizer.process(image)
                    .addOnSuccessListener { text ->
                        continuation.resume(text.text)
                    }
                    .addOnFailureListener { e ->
                        Timber.e("Error: ${e.message}")
                        continuation.resume("")
                    }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }
}
