package com.alphaomardiallo.handydocs.feature.altgenerator.data.remote.datasource

import com.alphaomardiallo.handydocs.common.data.DataResponse
import com.alphaomardiallo.handydocs.feature.altgenerator.data.remote.api.GenerateAltApi
import com.alphaomardiallo.handydocs.feature.altgenerator.data.remote.model.GeminiPromptDto
import com.alphaomardiallo.handydocs.feature.altgenerator.data.remote.model.GenerateContentRequest
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode

class AltGeneratorDataSource(private val api: GenerateAltApi) {

    suspend fun getAltText(prompt: String, imageBase64: String): DataResponse<GeminiPromptDto> {
        val response = api.generateAltText(prompt, imageBase64)

        return when (response?.status) {
            HttpStatusCode.OK -> DataResponse.Success(response = response.body<GeminiPromptDto>())
            else -> DataResponse.Error(
                errorCode = response?.status?.value,
                description = response?.status?.description
            )
        }
    }
}
