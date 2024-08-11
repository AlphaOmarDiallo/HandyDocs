package com.alphaomardiallo.handydocs.presentation.home

import androidx.lifecycle.viewModelScope
import com.alphaomardiallo.handydocs.domain.model.ImageDoc
import com.alphaomardiallo.handydocs.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.domain.repository.ImageDocRepository
import com.alphaomardiallo.handydocs.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    appNavigator: AppNavigator,
    private val imageDocRepository: ImageDocRepository
) : BaseViewModel(appNavigator) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    fun getAllImages() {
        viewModelScope.launch {
            imageDocRepository.getAllImages().collect { imageList ->
                _uiState.update { state ->
                    state.copy(allImageDoc = imageList)
                }
            }
        }
    }
}

data class HomeUiState(
    val allImageDoc: List<ImageDoc> = emptyList()
)
