package com.alphaomardiallo.handydocs.feature.altgenerator.domain.repository

import com.alphaomardiallo.handydocs.common.data.DataResponse
import com.alphaomardiallo.handydocs.feature.altgenerator.data.remote.model.GeminiPromptDto
import com.alphaomardiallo.handydocs.feature.altgenerator.data.repository.ImageConversionResult

interface AltGeneratorRepository {
    suspend fun imageToBase64(source: String): ImageConversionResult
    suspend fun generateAltText(prompt: String, imageBase64: String): DataResponse<GeminiPromptDto>
}
