package com.alphaomardiallo.handydocs.feature.ocr.presentation

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.alphaomardiallo.handydocs.common.domain.model.ImageDoc
import com.alphaomardiallo.handydocs.common.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.common.domain.repository.ImageDocRepository
import com.alphaomardiallo.handydocs.common.presentation.base.BaseViewModel
import com.alphaomardiallo.handydocs.feature.ocr.domain.OcrRepository
import com.alphaomardiallo.handydocs.feature.ocr.domain.TextAnalysisResult
import com.alphaomardiallo.handydocs.feature.ocr.domain.TextRecognitionType
import kotlinx.coroutines.launch

class OcrViewModel(
    appNavigator: AppNavigator,
    private val ocrRepository: OcrRepository,
    private val imageDocRepository: ImageDocRepository
) : BaseViewModel(appNavigator) {

    var state by mutableStateOf(OcrUiState())
        private set

    fun analyse(uri: Uri, context: Context) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            when (val result = ocrRepository.analyseFileFromUri(uri, context, state.script)) {
                is TextAnalysisResult.Success -> {
                    state = state.copy(
                        text = result.text,
                        fileError = false,
                        recognitionError = false,
                        invalidImage = false,
                        isLoading = false
                    )
                }

                is TextAnalysisResult.Error.FileError -> {
                    state = state.copy(
                        text = null,
                        fileError = true,
                        recognitionError = false,
                        invalidImage = false,
                        isLoading = false
                    )
                }

                is TextAnalysisResult.Error.RecognitionError -> {
                    state = state.copy(
                        text = null,
                        fileError = false,
                        recognitionError = true,
                        invalidImage = false,
                        isLoading = false
                    )
                }

                is TextAnalysisResult.Error.InvalidImage -> {
                    state = state.copy(
                        text = null,
                        fileError = false,
                        recognitionError = true,
                        invalidImage = false,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun saveImageDoc(imageDoc: ImageDoc) {
        viewModelScope.launch {
            imageDocRepository.upsertImage(imageDoc)
        }
    }

    fun updateScript(textRecognitionType: TextRecognitionType) {
        viewModelScope.launch {
            state = state.copy(script = textRecognitionType)
        }
    }

    fun updateLoadingState() {
        state = state.copy(isLoading = !state.isLoading)
    }
}

data class OcrUiState(
    val text: String? = null,
    val script: TextRecognitionType = TextRecognitionType.LATIN,
    val fileError: Boolean = false,
    val recognitionError: Boolean = false,
    val invalidImage: Boolean = false,
    val isLoading: Boolean = false
)
