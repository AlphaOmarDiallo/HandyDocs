package com.alphaomardiallo.handydocs.feature.altgenerator.domain.usecase

import com.alphaomardiallo.handydocs.feature.altgenerator.data.repository.ImageConversionResult
import com.alphaomardiallo.handydocs.feature.altgenerator.domain.repository.AltGeneratorRepository

class ImageToBase64UseCase(private val repository: AltGeneratorRepository) {
    suspend fun invoke(source: String): ImageConversionResult {
        return repository.imageToBase64(source)
    }
}
