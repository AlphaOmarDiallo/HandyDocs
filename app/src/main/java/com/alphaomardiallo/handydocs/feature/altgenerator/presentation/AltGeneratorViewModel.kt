package com.alphaomardiallo.handydocs.feature.altgenerator.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.alphaomardiallo.handydocs.common.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.common.presentation.base.BaseViewModel
import com.alphaomardiallo.handydocs.feature.altgenerator.data.repository.ImageConversionResult
import com.alphaomardiallo.handydocs.feature.altgenerator.domain.repository.AltGeneratorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AltGeneratorViewModel(
    appNavigator: AppNavigator,
    private val altGeneratorRepository: AltGeneratorRepository
) : BaseViewModel(appNavigator) {

    var state by mutableStateOf(AltGenUiState())
        private set

    fun imageToBase64(source: String) {
        viewModelScope.launch(Dispatchers.IO) {
            state = state.copy(isLoading = false)
            state = when (val response = altGeneratorRepository.imageToBase64(source)) {
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
}

data class AltGenUiState(
    val altText: String? = null,
    val base64String: String? = null,
    val error: Boolean = false,
    val errorMessage: String? = null,
    val isLoading: Boolean = false
)
