package com.alphaomardiallo.handydocs.feature.altgenerator.data.remote.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>
)

@Serializable
data class Content(
    val parts: List<Map<String, @Contextual Any>>
)

// Helper functions
fun createTextPart(text: String): Map<String, Any> = mapOf("text" to text)

fun createImagePart(base64Image: String, mimeType: String = "image/jpeg"): Map<String, Any> =
    mapOf("inline_data" to mapOf(
        "mime_type" to mimeType,
        "data" to base64Image
    ))
