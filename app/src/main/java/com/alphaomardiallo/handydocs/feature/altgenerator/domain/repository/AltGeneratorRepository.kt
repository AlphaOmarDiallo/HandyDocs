package com.alphaomardiallo.handydocs.feature.altgenerator.domain.repository

interface AltGeneratorRepository {
    suspend fun imageUrlToBase64(url: String): String
}
