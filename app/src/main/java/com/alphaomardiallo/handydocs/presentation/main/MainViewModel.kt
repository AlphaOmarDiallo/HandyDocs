package com.alphaomardiallo.handydocs.presentation.main

import com.alphaomardiallo.handydocs.domain.destination.AppDestination
import com.alphaomardiallo.handydocs.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.presentation.base.BaseViewModel

class MainViewModel(appNavigator: AppNavigator): BaseViewModel(appNavigator) {

    val navigationChannel = appNavigator.navigationChannel

    fun onCameraPermissionGranted(){
        navigateTo(AppDestination.CameraPreview.route)
    }
}
