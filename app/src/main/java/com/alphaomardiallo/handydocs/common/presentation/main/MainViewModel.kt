package com.alphaomardiallo.handydocs.common.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.alphaomardiallo.handydocs.common.domain.model.ImageDoc
import com.alphaomardiallo.handydocs.common.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.common.domain.repository.ImageDocRepository
import com.alphaomardiallo.handydocs.common.presentation.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(
    appNavigator: AppNavigator,
    private val imageDocRepository: ImageDocRepository
) : BaseViewModel(appNavigator) {

    val navigationChannel = appNavigator.navigationChannel

    var state by mutableStateOf(MainState())
        private set

    private var job: Job? = null

    override fun onCleared() {
        job = null
        super.onCleared()
    }

    fun savePdfInDatabase(pdf: ImageDoc) {
        viewModelScope.launch {
            imageDocRepository.upsertImage(pdf)
        }
    }

    fun searchDoc(hint: String) {
        job?.cancel()

        job = viewModelScope.launch(Dispatchers.IO) {
            imageDocRepository.searchImageDoc(hint).collect {
                state = state.copy(searchList = it)
            }
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
}

data class MainState(val searchList: List<ImageDoc> = emptyList())
