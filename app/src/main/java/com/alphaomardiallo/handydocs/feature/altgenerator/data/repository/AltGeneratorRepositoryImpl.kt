package com.alphaomardiallo.handydocs.feature.altgenerator.data.repository

import android.content.Context
import android.net.Uri
import com.alphaomardiallo.handydocs.feature.altgenerator.domain.repository.AltGeneratorRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import io.ktor.http.isSuccess
import timber.log.Timber
import java.io.File
import java.util.Base64

class AltGeneratorRepositoryImpl(private val context: Context, private val httpClient: HttpClient) :
    AltGeneratorRepository {
    override suspend fun imageToBase64(source: String): ImageConversionResult {
        return try {
            Timber.d("Attempting to process: $source")

            val imageBytes = when {
                source.startsWith("content://") || source.startsWith("file://") -> {
                    Timber.d("Handling as URI")
                    val uri = Uri.parse(source)
                    context.contentResolver?.openInputStream(uri)?.use { it.readBytes() }
                        ?: return ImageConversionResult.Error("Failed to read from URI: $source")
                }

                source.startsWith("http://") || source.startsWith("https://") -> {
                    Timber.d("Handling as HTTP URL")
                    try {
                        val response: HttpResponse = httpClient.get(source)
                        if (!response.status.isSuccess()) {
                            return ImageConversionResult.Error(
                                message = "HTTP request failed with status: ${response.status}",
                                errorCode = response.status.value
                            )
                        }
                        response.readBytes()
                    } catch (e: Exception) {
                        Timber.e("HTTP request failed: ${e.message}")
                        return ImageConversionResult.ExceptionThrown(
                            exception = e,
                            errorMessage = e.localizedMessage
                        )
                    }
                }

                else -> {
                    Timber.d("Handling as file path")
                    val file = File(source)
                    if (!file.exists() || !file.canRead()) {
                        return ImageConversionResult.Error(message = "File not found or unreadable: $source")
                    }
                    file.readBytes()
                }
            }

            Timber.d("Converting ${imageBytes.size} bytes to Base64")
            val base64String = Base64.getEncoder().encodeToString(imageBytes)
            Timber.d("Base64 string length: ${base64String.length}")

            ImageConversionResult.Success(base64 = base64String)
        } catch (e: Exception) {
            Timber.e(e, "Error in imageToBase64")
            ImageConversionResult.ExceptionThrown(exception = e, errorMessage = e.localizedMessage)
        }
    }
}

sealed class ImageConversionResult {
    data class Success(val base64: String) : ImageConversionResult()
    data class Error(val message: String, val errorCode: Int? = null) : ImageConversionResult()
    data class ExceptionThrown(val exception: Throwable, val errorMessage: String? = null) :
        ImageConversionResult()
}
