package com.alphaomardiallo.handydocs.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.alphaomardiallo.handydocs.domain.model.ImageDoc
import com.alphaomardiallo.handydocs.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.domain.repository.ImageDocRepository
import com.alphaomardiallo.handydocs.presentation.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(
    appNavigator: AppNavigator,
    private val imageDocRepository: ImageDocRepository
) : BaseViewModel(appNavigator) {

    var state by mutableStateOf(HomeUiState())
        private set

    private var job: Job? = null

    init {
        getAllImageTest()
    }

    fun updateDocumentName(imageDoc: ImageDoc, newName: String) {
        viewModelScope.launch {
            imageDocRepository.upsertImage(
                imageDoc.copy(displayName = newName)
            )
        }
    }

    fun updateDocumentSelected(imageDoc: ImageDoc) {
        viewModelScope.launch {
            imageDocRepository.getAllImages().first().map {
                if (it.id == imageDoc.id) {
                    imageDocRepository.upsertImage(imageDoc.copy(isSelected = true))
                } else {
                    imageDocRepository.upsertImage(it.copy(isSelected = false))
                }
            }
        }
    }

    fun getAllImageTest(filterType: ListFilter = ListFilter.None) {
        job?.cancel()

        viewModelScope.launch {
            when (filterType) {
                is ListFilter.NameAsc -> getAllImagesNameAsc()
                is ListFilter.NameDesc -> getAllImagesNameDesc()
                is ListFilter.TimeAsc -> getAllImagesTimeAsc()
                is ListFilter.TimeDesc -> getAllImagesTimeDesc()
                is ListFilter.None -> getAllImagesUnfiltered()
            }
        }
    }

    private fun getAllImagesUnfiltered() {
        job = viewModelScope.launch {
            imageDocRepository.getAllImages().collect { imageList ->
                state = state.copy(allImageDoc = imageList)
            }
        }
    }

    private fun getAllImagesNameAsc() {
        job = viewModelScope.launch {
            imageDocRepository.getAllImageNameAsc().collect { imageList ->
                state = state.copy(allImageDoc = imageList)
            }
        }
    }

    private fun getAllImagesNameDesc() {
        job = viewModelScope.launch {
            imageDocRepository.getAllImageNameDesc().collect { imageList ->
                state = state.copy(allImageDoc = imageList)
            }
        }
    }

    private fun getAllImagesTimeAsc() {
        job = viewModelScope.launch {
            imageDocRepository.getAllImageTimeAsc().collect { imageList ->
                state = state.copy(allImageDoc = imageList)
            }
        }
    }

    private fun getAllImagesTimeDesc() {
        job = viewModelScope.launch {
            imageDocRepository.getAllImageTimeDesc().collect { imageList ->
                state = state.copy(allImageDoc = imageList)
            }
        }
    }

    data class HomeUiState(
        val allImageDoc: List<ImageDoc> = emptyList()
    )
}
