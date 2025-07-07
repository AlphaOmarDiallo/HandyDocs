package com.alphaomardiallo.handydocs.feature.altgenerator.data.remote.model

import com.alphaomardiallo.handydocs.feature.altgenerator.domain.model.Candidate
import com.alphaomardiallo.handydocs.feature.altgenerator.domain.model.CandidatesTokensDetail
import com.alphaomardiallo.handydocs.feature.altgenerator.domain.model.Content
import com.alphaomardiallo.handydocs.feature.altgenerator.domain.model.GeminiPrompt
import com.alphaomardiallo.handydocs.feature.altgenerator.domain.model.Part
import com.alphaomardiallo.handydocs.feature.altgenerator.domain.model.PromptTokensDetail
import com.alphaomardiallo.handydocs.feature.altgenerator.domain.model.UsageMetadata
import kotlinx.serialization.Serializable


@Serializable
data class GeminiPromptDto(
    val candidates: List<CandidateDto>,
    val modelVersion: String,
    val usageMetadata: UsageMetadataDto
) {
    fun toDomain() = GeminiPrompt(
        candidates = candidates.map { it.toDomain() },
        modelVersion = modelVersion,
        usageMetadata = usageMetadata.toDomain()
    )
}

@Serializable
data class CandidateDto(
    val avgLogprobs: Double? = null,
    val content: ContentDto,
    val finishReason: String
) {
    fun toDomain() = Candidate(
        avgLogprobs = avgLogprobs,
        content = content.toDomain(),
        finishReason = finishReason
    )
}

@Serializable
data class ContentDto(
    val parts: List<PartDto>,
    val role: String
) {
    fun toDomain() = Content(
        parts = parts.map { it.toDomain() },
        role = role
    )
}

@Serializable
data class PartDto(
    val text: String
) {
    fun toDomain() = Part(
        text = text
    )
}

@Serializable
data class UsageMetadataDto(
    val candidatesTokenCount: Int,
    val candidatesTokensDetails: List<CandidatesTokensDetailDto> = emptyList(),
    val promptTokenCount: Int,
    val promptTokensDetails: List<PromptTokensDetailDto>,
    val totalTokenCount: Int
) {
    fun toDomain() = UsageMetadata(
        candidatesTokenCount = candidatesTokenCount,
        candidatesTokensDetails = candidatesTokensDetails.map { it.toDomain() },
        promptTokenCount = promptTokenCount,
        promptTokensDetails = promptTokensDetails.map { it.toDomain() },
        totalTokenCount
    )
}

@Serializable
data class CandidatesTokensDetailDto(
    val modality: String,
    val tokenCount: Int
) {
    fun toDomain() = CandidatesTokensDetail(
        modality = modality,
        tokenCount = tokenCount
    )
}

@Serializable
data class PromptTokensDetailDto(
    val modality: String,
    val tokenCount: Int
) {
    fun toDomain() = PromptTokensDetail(
        modality = modality,
        tokenCount = tokenCount
    )
}
