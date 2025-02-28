package com.alphaomardiallo.handydocs.feature.altgenerator.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import timber.log.Timber

class GenerateAltApi(private val httpClient: HttpClient, private val apiKey: String) {

    private companion object {
        const val GEMINI_BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"
    }

    suspend fun generateAltText(prompt: String, imageBase64: String): HttpResponse? {
        val requestBody = buildJsonObject {
            putJsonArray("contents") {
                addJsonObject {
                    putJsonArray("parts") {
                        // Text part
                        addJsonObject {
                            put("text", prompt)
                        }
                        // Image part
                        addJsonObject {
                            putJsonObject("inline_data") {
                                put("mime_type", "image/jpeg")
                                put("data", imageBase64)
                            }
                        }
                    }
                }
            }
        }

        val response = kotlin.runCatching {
            httpClient.post {
                url(GEMINI_BASE_URL)
                parameter("key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
        }.onFailure {
            Timber.e(it, "Error in generateAltText")
        }.getOrNull()

        return response
    }
}
