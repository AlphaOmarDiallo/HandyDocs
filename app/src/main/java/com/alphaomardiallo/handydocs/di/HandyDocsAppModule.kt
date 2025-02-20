package com.alphaomardiallo.handydocs.di

import com.alphaomardiallo.handydocs.common.data.ImageDocRepositoryImp
import com.alphaomardiallo.handydocs.common.data.provideDataBase
import com.alphaomardiallo.handydocs.common.data.provideImageDao
import com.alphaomardiallo.handydocs.common.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.common.domain.navigator.AppNavigatorImp
import com.alphaomardiallo.handydocs.common.domain.repository.ImageDocRepository
import com.alphaomardiallo.handydocs.common.presentation.main.MainViewModel
import com.alphaomardiallo.handydocs.feature.docviewer.DocViewerViewModel
import com.alphaomardiallo.handydocs.feature.ocr.data.OcrRepositoryImpl
import com.alphaomardiallo.handydocs.feature.ocr.domain.OcrRepository
import com.alphaomardiallo.handydocs.feature.ocr.presentation.OcrViewModel
import com.alphaomardiallo.handydocs.feature.pdfsafe.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    // Main
    single<AppNavigator> { AppNavigatorImp() }
    viewModelOf(::MainViewModel)

    // Home
    viewModelOf(::HomeViewModel)

    // Doc viewer
    viewModelOf(::DocViewerViewModel)

    viewModelOf(::OcrViewModel)

    // Database
    single { provideDataBase(application = get()) }
    single { provideImageDao(appDataBase = get()) }

    // ImageDoc repository
    single<ImageDocRepository> { ImageDocRepositoryImp(appDataBase = get()) }
    single<OcrRepository> { OcrRepositoryImpl() }
}
