package com.alphaomardiallo.handydocs.feature.docviewer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.alphaomardiallo.handydocs.common.domain.model.ImageDoc
import com.alphaomardiallo.handydocs.common.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.common.domain.repository.ImageDocRepository
import com.alphaomardiallo.handydocs.common.presentation.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DocViewerViewModel(
    appNavigator: AppNavigator,
    private val imageDocRepository: ImageDocRepository
) : BaseViewModel(appNavigator) {

    var state by mutableStateOf(DocViewerImageDoc())
        private set

    init {
        getSelectedImageDoc()
    }

    fun updateDocumentName(imageDoc: ImageDoc, newName: String) {
        viewModelScope.launch {
            imageDocRepository.upsertImage(
                imageDoc.copy(displayName = newName)
            )
        }
    }

    fun deleteDocument(imageDoc: ImageDoc) {
        viewModelScope.launch {
            imageDocRepository.deleteImage(imageDoc)
        }
    }

    fun selectedImageToNull(){
        viewModelScope.launch(Dispatchers.IO) {
            imageDocRepository.selectedImageToNull()
        }
    }

    private fun getSelectedImageDoc() {
        viewModelScope.launch {
            imageDocRepository.getSelectedImage().collect {
                state = state.copy(selectedImage = it)
            }
        }
    }
}

data class DocViewerImageDoc(val selectedImage: ImageDoc? = null)
