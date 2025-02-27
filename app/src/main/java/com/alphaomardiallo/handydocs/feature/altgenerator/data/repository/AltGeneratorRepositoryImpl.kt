package com.alphaomardiallo.handydocs.feature.altgenerator.data.repository

import android.content.Context
import android.net.Uri
import com.alphaomardiallo.handydocs.feature.altgenerator.domain.repository.AltGeneratorRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readRawBytes
import kotlinx.io.IOException
import kotlinx.io.files.FileNotFoundException
import timber.log.Timber
import java.io.File
import java.util.Base64

class AltGeneratorRepositoryImpl(private val context: Context, private val httpClient: HttpClient): AltGeneratorRepository {
    override suspend fun imageToBase64(source: String): String {
        return try {
            println("Attempting to process: $source")

            val imageBytes = when {
                source.startsWith("content://") || source.startsWith("file://") -> {
                    Timber.d("Handling as URI")
                    // Note: context is missing from your function params
                    val uri = Uri.parse(source)
                    context.contentResolver.openInputStream(uri)?.readBytes()
                        ?: throw IOException("Failed to read from URI: $source")
                }
                source.startsWith("http://") || source.startsWith("https://") -> {
                    println("Handling as HTTP URL")
                    try {
                        val response: HttpResponse = httpClient.get(source)
                        Timber.d("Response status: ${response.status}")
                        val bytes = response.readRawBytes() // Use readBytes() instead of readRawBytes()
                        Timber.d("Read ${bytes.size} bytes from response")
                        bytes
                    } catch (e: Exception) {
                        Timber.e("HTTP request failed: ${e.message}")
                        throw e
                    }
                }
                else -> {
                    Timber.d("Handling as file path")
                    val file = File(source)
                    if (!file.exists()) {
                        Timber.e("File doesn't exist: $source")
                        throw FileNotFoundException("File not found: $source")
                    }
                    file.readBytes()
                }
            }

            println("Converting ${imageBytes.size} bytes to Base64")
            val base64String = Base64.getEncoder().encodeToString(imageBytes)
            Timber.d("Base64 string length: ${base64String.length}")
            base64String
        } catch (e: Exception) {
            Timber.e("Error in imageToBase64: ${e.message}")
            throw Exception("Failed to convert image to base64: ${e.message}")
        }
    }
}
