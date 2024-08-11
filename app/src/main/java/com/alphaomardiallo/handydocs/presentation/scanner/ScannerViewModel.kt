package com.alphaomardiallo.handydocs.presentation.scanner

import androidx.lifecycle.viewModelScope
import com.alphaomardiallo.handydocs.domain.model.ImageDoc
import com.alphaomardiallo.handydocs.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.domain.repository.ImageDocRepository
import com.alphaomardiallo.handydocs.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

class ScannerViewModel(
    appNavigator: AppNavigator,
    private val imageDocRepository: ImageDocRepository
) : BaseViewModel(appNavigator) {

    fun savePdfInDatabase(pdf: ImageDoc) {
        viewModelScope.launch {
            imageDocRepository.upsertImage(pdf)
        }
    }
}
