package com.alphaomardiallo.handydocs.feature.altgenerator.data.repository

import com.alphaomardiallo.handydocs.feature.altgenerator.domain.repository.AltGeneratorRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readRawBytes
import java.util.Base64

class AltGeneratorRepositoryImpl(private val httpClient: HttpClient): AltGeneratorRepository {
    override suspend fun imageUrlToBase64(url: String): String {
        return try {
            val response: HttpResponse = httpClient.get(url)
            val imageBytes = response.readRawBytes()
            Base64.getEncoder().encodeToString(imageBytes)
        } catch (e: Exception) {
            throw Exception("Failed to convert image to base64: ${e.message}")
        }
    }
}
