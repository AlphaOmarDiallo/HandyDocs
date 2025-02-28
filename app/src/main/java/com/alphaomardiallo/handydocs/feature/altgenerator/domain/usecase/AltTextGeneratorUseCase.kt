package com.alphaomardiallo.handydocs.feature.altgenerator.domain.usecase

import com.alphaomardiallo.handydocs.common.data.DataResponse
import com.alphaomardiallo.handydocs.common.domain.model.DomainResponse
import com.alphaomardiallo.handydocs.feature.altgenerator.domain.model.GeminiPrompt
import com.alphaomardiallo.handydocs.feature.altgenerator.domain.repository.AltGeneratorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AltTextGeneratorUseCase(private val repository: AltGeneratorRepository) {

    suspend fun invoke(prompt: String, imageBase64: String): Flow<DomainResponse<GeminiPrompt>> =
        flow {
            emit(DomainResponse.Loading())

            when (val result = repository.generateAltText(prompt, imageBase64)) {
                is DataResponse.Error -> {
                    emit(
                        DomainResponse.Error(
                            errorCode = result.errorCode,
                            description = result.description
                        )
                    )
                }

                is DataResponse.Success -> {
                    emit(
                        DomainResponse.Success(
                            response = result.response.toDomain()
                        )
                    )
                }
            }
        }
}
