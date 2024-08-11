package com.alphaomardiallo.handydocs.presentation.main

import androidx.lifecycle.viewModelScope
import com.alphaomardiallo.handydocs.domain.model.ImageDoc
import com.alphaomardiallo.handydocs.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.domain.repository.ImageDocRepository
import com.alphaomardiallo.handydocs.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

class MainViewModel(
    appNavigator: AppNavigator,
    private val imageDocRepository: ImageDocRepository
): BaseViewModel(appNavigator) {

    val navigationChannel = appNavigator.navigationChannel

    fun savePdfInDatabase(pdf: ImageDoc) {
        viewModelScope.launch {
            imageDocRepository.upsertImage(pdf)
        }
    }
}
