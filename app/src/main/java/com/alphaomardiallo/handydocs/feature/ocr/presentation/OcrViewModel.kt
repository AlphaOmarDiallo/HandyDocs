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
            state = state.copy(text = ocrRepository.analyseFileFromUri(uri, context))
        }
    }

    fun saveImageDoc(imageDoc: ImageDoc){
        viewModelScope.launch {
            imageDocRepository.upsertImage(imageDoc)
        }
    }
}

data class OcrUiState(
    val text: String? = null
)
