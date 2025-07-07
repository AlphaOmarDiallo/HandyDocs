package com.alphaomardiallo.handydocs.feature.altgenerator.domain.model

data class GeminiPrompt(
    val candidates: List<Candidate>,
    val modelVersion: String,
    val usageMetadata: UsageMetadata
)

data class Candidate(
    val avgLogprobs: Double? = null,
    val content: Content,
    val finishReason: String
)

data class Content(
    val parts: List<Part>,
    val role: String
)

data class Part(
    val text: String
)

data class UsageMetadata(
    val candidatesTokenCount: Int,
    val candidatesTokensDetails: List<CandidatesTokensDetail> = emptyList(),
    val promptTokenCount: Int,
    val promptTokensDetails: List<PromptTokensDetail>,
    val totalTokenCount: Int
)

data class CandidatesTokensDetail(
    val modality: String,
    val tokenCount: Int
)

data class PromptTokensDetail(
    val modality: String,
    val tokenCount: Int
)
