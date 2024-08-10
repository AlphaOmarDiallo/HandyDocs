package com.alphaomardiallo.handydocs.presentation.camera

import android.net.Uri
import com.alphaomardiallo.handydocs.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.presentation.base.BaseViewModel
import timber.log.Timber

class CameraViewModel(private val appNavigator: AppNavigator): BaseViewModel(appNavigator) {

    fun saveImageInfoInDatabase(
        name: String,
        uri: Uri
    ){
        Timber.d("name: $name - uri: $uri")
    }
}