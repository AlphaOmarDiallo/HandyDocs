package com.alphaomardiallo.handydocs.feature.altgenerator.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Response example
{
"candidates": [
{
"content": {
"parts": [
{
"text": "Here's a detailed alt text for the provided image:\n\n\"A political cartoon depicts a brick wall topped with barbed wire.  On the wall is a sign reading 'KEEP OUT YOU ARE NOT WELCOME' in large bold letters.  To the left of the sign, a small American flag is affixed to the wall.  Donald Trump, caricatured, sits atop the wall, holding a rifle.  On the right, a caricature of Elon Musk holds a sign that reads 'TECH BROS APPLY HERE,' with a downward-pointing arrow.  A section of the wall has been broken down, with rubble at its base and a pickaxe leaning against it.  The overall scene suggests commentary on immigration, technology, and exclusion, potentially highlighting the contrast between Trump's restrictive policies and Musk's apparent disregard for borders or perceived limitations.\"\n"
}
],
"role": "model"
},
"finishReason": "STOP",
"avgLogprobs": -0.32769023670869712
}
],
"usageMetadata": {
"promptTokenCount": 270,
"candidatesTokenCount": 170,
"totalTokenCount": 440,
"promptTokensDetails": [
{
"modality": "TEXT",
"tokenCount": 12
},
{
"modality": "IMAGE",
"tokenCount": 258
}
],
"candidatesTokensDetails": [
{
"modality": "TEXT",
"tokenCount": 170
}
]
},
"modelVersion": "gemini-1.5-flash"
}*/

@Serializable
data class GeminiPromptDto(
    val candidates: List<CandidateDto>,
    val modelVersion: String,
    val usageMetadata: UsageMetadataDto
)

@Serializable
data class CandidateDto(
    val avgLogprobs: Double,
    val content: ContentDto,
    val finishReason: String
)

@Serializable
data class ContentDto(
    val parts: List<PartDto>,
    val role: String
)

@Serializable
data class PartDto(
    val text: String
)

@Serializable
data class UsageMetadataDto(
    val candidatesTokenCount: Int,
    val candidatesTokensDetails: List<CandidatesTokensDetailDto>,
    val promptTokenCount: Int,
    val promptTokensDetails: List<PromptTokensDetailDto>,
    val totalTokenCount: Int
)

@Serializable
data class CandidatesTokensDetailDto(
    val modality: String,
    val tokenCount: Int
)

@Serializable
data class PromptTokensDetailDto(
    val modality: String,
    val tokenCount: Int
)
