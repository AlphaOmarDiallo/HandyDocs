package com.alphaomardiallo.handydocs.feature.altgenerator.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.alphaomardiallo.handydocs.common.domain.model.DomainResponse
import com.alphaomardiallo.handydocs.common.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.common.presentation.base.BaseViewModel
import com.alphaomardiallo.handydocs.feature.altgenerator.data.repository.ImageConversionResult
import com.alphaomardiallo.handydocs.feature.altgenerator.domain.usecase.AltTextGeneratorUseCase
import com.alphaomardiallo.handydocs.feature.altgenerator.domain.usecase.ImageToBase64UseCase
import com.alphaomardiallo.handydocs.feature.altgenerator.presentation.model.Language
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class AltGeneratorViewModel(
    appNavigator: AppNavigator,
    private val imageToBase64UseCase: ImageToBase64UseCase,
    private val altTextGeneratorUseCase: AltTextGeneratorUseCase
) : BaseViewModel(appNavigator) {

    var state by mutableStateOf(AltGenUiState())
        private set

    fun imageToBase64(source: String) {
        viewModelScope.launch(Dispatchers.IO) {
            state = state.copy(isLoading = false)
            state = when (val response = imageToBase64UseCase.invoke(source)) {
                is ImageConversionResult.Success -> {
                    state.copy(
                        base64String = response.base64,
                        error = false,
                        errorMessage = null,
                        isLoading = false
                    )
                }

                is ImageConversionResult.Error -> {
                    state.copy(
                        base64String = null,
                        error = true,
                        errorMessage = response.message,
                        isLoading = false
                    )
                }

                is ImageConversionResult.ExceptionThrown -> {
                    state.copy(
                        base64String = null,
                        error = false,
                        errorMessage = response.exception.localizedMessage,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun getAltText(source: String, language: Language = Language.ENGLISH, maxChar: Int = 2000) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val prompt =
                    "Can you write me the most detailed alt text for this image in a maximum of $maxChar char in ${language.code}?"

                altTextGeneratorUseCase.invoke(prompt = prompt, imageBase64 = source)
                    .collect { response ->
                        when (response) {
                            is DomainResponse.Error -> state = state.copy(
                                altText = null,
                                error = true,
                                errorMessage = response.description,
                                isLoading = false
                            )

                            is DomainResponse.Loading -> state = state.copy(
                                altText = null,
                                error = false,
                                errorMessage = null,
                                isLoading = true
                            )

                            is DomainResponse.Success -> state = state.copy(
                                altText = response.response.candidates[0].content.parts[0].text,
                                error = false,
                                errorMessage = null,
                                isLoading = false
                            )
                        }
                    }
            } catch (e: Exception) {
                Timber.e(e.localizedMessage)
            }
        }
    }
}

data class AltGenUiState(
    val altText: String? = null,
    val base64String: String? = null,
    val error: Boolean = false,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)
