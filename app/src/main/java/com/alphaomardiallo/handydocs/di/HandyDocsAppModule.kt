package com.alphaomardiallo.handydocs.di

import com.alphaomardiallo.handydocs.common.data.ImageDocRepositoryImp
import com.alphaomardiallo.handydocs.common.data.provideDataBase
import com.alphaomardiallo.handydocs.common.data.provideImageDao
import com.alphaomardiallo.handydocs.common.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.common.domain.navigator.AppNavigatorImp
import com.alphaomardiallo.handydocs.common.domain.repository.ImageDocRepository
import com.alphaomardiallo.handydocs.common.presentation.main.MainViewModel
import com.alphaomardiallo.handydocs.feature.docviewer.DocViewerViewModel
import com.alphaomardiallo.handydocs.feature.pdfsafe.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Main
    single<AppNavigator> { AppNavigatorImp() }
    viewModel { MainViewModel(appNavigator = get(), imageDocRepository = get()) }

    // Home
    viewModel { HomeViewModel(appNavigator = get(), imageDocRepository = get()) }

    // Doc viewer
    viewModel { DocViewerViewModel(appNavigator = get(), imageDocRepository = get()) }

    // Database
    single { provideDataBase(application = get()) }
    single { provideImageDao(appDataBase = get()) }

    // ImageDoc repository
    single<ImageDocRepository> { ImageDocRepositoryImp(appDataBase = get()) }
}
