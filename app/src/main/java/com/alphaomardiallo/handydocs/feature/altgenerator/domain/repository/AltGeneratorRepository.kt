package com.alphaomardiallo.handydocs.feature.altgenerator.domain.repository

interface AltGeneratorRepository {
    suspend fun imageToBase64(source: String): String
}
