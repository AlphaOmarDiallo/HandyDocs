package com.alphaomardiallo.handydocs.feature.altgenerator.domain.repository

import com.alphaomardiallo.handydocs.feature.altgenerator.data.repository.ImageConversionResult

interface AltGeneratorRepository {
    suspend fun imageToBase64(source: String): ImageConversionResult
}
