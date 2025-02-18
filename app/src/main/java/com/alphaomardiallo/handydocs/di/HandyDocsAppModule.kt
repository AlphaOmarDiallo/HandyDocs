package com.alphaomardiallo.handydocs.di

import com.alphaomardiallo.handydocs.common.data.ImageDocRepositoryImp
import com.alphaomardiallo.handydocs.common.data.provideDataBase
import com.alphaomardiallo.handydocs.common.data.provideImageDao
import com.alphaomardiallo.handydocs.common.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.common.domain.navigator.AppNavigatorImp
import com.alphaomardiallo.handydocs.common.domain.repository.ImageDocRepository
import com.alphaomardiallo.handydocs.feature.docviewer.DocViewerViewModel
import com.alphaomardiallo.handydocs.feature.pdfsafe.HomeViewModel
import com.alphaomardiallo.handydocs.common.presentation.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Main
    single<AppNavigator> { AppNavigatorImp() }
    viewModel { MainViewModel(get(), get()) }

    // Home
    viewModel { HomeViewModel(get(), get()) }

    // Doc viewer
    viewModel { DocViewerViewModel(get(), get()) }

    // Database
    single { provideDataBase(get()) }
    single { provideImageDao(get()) }

    // ImageDoc repository
    single<ImageDocRepository> { ImageDocRepositoryImp(get()) }
}
