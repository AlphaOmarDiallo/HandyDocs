package com.alphaomardiallo.handydocs.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.alphaomardiallo.handydocs.domain.model.ImageDoc
import com.alphaomardiallo.handydocs.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.domain.repository.ImageDocRepository
import com.alphaomardiallo.handydocs.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

class HomeViewModel(
    appNavigator: AppNavigator,
    private val imageDocRepository: ImageDocRepository
) : BaseViewModel(appNavigator) {

var state by mutableStateOf(HomeUiState())
        private set

    init {
        getAllImages()
    }

    fun updateDocument(imageDoc: ImageDoc, newName: String) {
        viewModelScope.launch {
            imageDocRepository.upsertImage(
                imageDoc.copy(displayName = newName)
            )
        }
    }

    private fun getAllImages() {
        viewModelScope.launch {
            imageDocRepository.getAllImages().collect { imageList ->
                state = state.copy(allImageDoc = imageList)
            }
        }
    }
}

data class HomeUiState(
    val allImageDoc: List<ImageDoc> = emptyList()
)
