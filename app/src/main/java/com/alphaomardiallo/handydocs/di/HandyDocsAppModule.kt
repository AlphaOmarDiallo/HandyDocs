package com.alphaomardiallo.handydocs.di

import com.alphaomardiallo.handydocs.data.ImageDocRepositoryImp
import com.alphaomardiallo.handydocs.data.provideDataBase
import com.alphaomardiallo.handydocs.data.provideImageDao
import com.alphaomardiallo.handydocs.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.domain.navigator.AppNavigatorImp
import com.alphaomardiallo.handydocs.domain.repository.ImageDocRepository
import com.alphaomardiallo.handydocs.presentation.camera.CameraViewModel
import com.alphaomardiallo.handydocs.presentation.home.HomeViewModel
import com.alphaomardiallo.handydocs.presentation.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Main
    single<AppNavigator> { AppNavigatorImp() }
    viewModel { MainViewModel(get()) }

    // Home
    viewModel { HomeViewModel(get(), get()) }

    // Camera
    viewModel { CameraViewModel(get(), get()) }

    // Database
    single { provideDataBase(get()) }
    single { provideImageDao(get()) }

    // ImageDoc repository
    single<ImageDocRepository> { ImageDocRepositoryImp(get()) }
}
