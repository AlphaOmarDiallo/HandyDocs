package com.alphaomardiallo.handydocs.presentation.camera

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.alphaomardiallo.handydocs.domain.model.ImageDoc
import com.alphaomardiallo.handydocs.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.domain.repository.ImageDocRepository
import com.alphaomardiallo.handydocs.presentation.base.BaseViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

class CameraViewModel(
    appNavigator: AppNavigator,
    private val imageDocRepository: ImageDocRepository
) : BaseViewModel(appNavigator) {

    fun saveImageInfoInDatabase(
        name: String,
        uri: Uri
    ) {
        Timber.d("name: $name - uri: $uri")
        viewModelScope.launch {
            imageDocRepository.upsertImage(
                ImageDoc(
                    name = name,
                    uri = uri
                )
            )
        }
    }
}
