package com.alphaomardiallo.handydocs.feature.altgenerator.domain

interface AltGeneratorRepository {
    suspend fun imageUrlToBase64(url: String): String
}
