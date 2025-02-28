package com.alphaomardiallo.handydocs.di

import com.alphaomardiallo.handydocs.BuildConfig
import com.alphaomardiallo.handydocs.common.data.ImageDocRepositoryImp
import com.alphaomardiallo.handydocs.common.data.provideDataBase
import com.alphaomardiallo.handydocs.common.data.provideImageDao
import com.alphaomardiallo.handydocs.common.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.common.domain.navigator.AppNavigatorImp
import com.alphaomardiallo.handydocs.common.domain.repository.ImageDocRepository
import com.alphaomardiallo.handydocs.common.presentation.main.MainViewModel
import com.alphaomardiallo.handydocs.feature.altgenerator.data.remote.api.GenerateAltApi
import com.alphaomardiallo.handydocs.feature.altgenerator.data.remote.datasource.AltGeneratorDataSource
import com.alphaomardiallo.handydocs.feature.altgenerator.data.repository.AltGeneratorRepositoryImpl
import com.alphaomardiallo.handydocs.feature.altgenerator.domain.repository.AltGeneratorRepository
import com.alphaomardiallo.handydocs.feature.altgenerator.domain.usecase.AltTextGeneratorUseCase
import com.alphaomardiallo.handydocs.feature.altgenerator.domain.usecase.ImageToBase64UseCase
import com.alphaomardiallo.handydocs.feature.altgenerator.presentation.AltGeneratorViewModel
import com.alphaomardiallo.handydocs.feature.docviewer.DocViewerViewModel
import com.alphaomardiallo.handydocs.feature.ocr.data.OcrRepositoryImpl
import com.alphaomardiallo.handydocs.feature.ocr.domain.OcrRepository
import com.alphaomardiallo.handydocs.feature.ocr.presentation.OcrViewModel
import com.alphaomardiallo.handydocs.feature.pdfsafe.PdfSafeViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import timber.log.Timber

val appModule = module {
    // Network
    single<HttpClientEngine> { CIO.create() }
    single { createHttpClient(enableNetworkLogs = BuildConfig.DEBUG) }

    // Database
    single { provideDataBase(application = get()) }
    single { provideImageDao(appDataBase = get()) }

    // ImageDoc repository
    single<ImageDocRepository> { ImageDocRepositoryImp(appDataBase = get()) }

    // Main
    single<AppNavigator> { AppNavigatorImp() }
    viewModelOf(::MainViewModel)

    // Home
    viewModelOf(::PdfSafeViewModel)

    // Doc viewer
    viewModelOf(::DocViewerViewModel)

    // Ocr
    single<OcrRepository> { OcrRepositoryImpl() }
    viewModelOf(::OcrViewModel)

    // Alt generator
    single { GenerateAltApi(httpClient = get(), apiKey = "AIzaSyDTKEMGx3U7kxpVe0FY8M8d112X-SVSghY") }
    single { AltGeneratorDataSource(api = get()) }
    single<AltGeneratorRepository> {
        AltGeneratorRepositoryImpl(context = get(), httpClient = get(), generatorDataSource = get())
    }
    factory { ImageToBase64UseCase(repository = get()) }
    factory { AltTextGeneratorUseCase(repository = get()) }
    viewModelOf(::AltGeneratorViewModel)
}

fun createHttpClient(enableNetworkLogs: Boolean) =
    HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                    isLenient = true
                    coerceInputValues = true
                }
            )
        }
        expectSuccess = true
        if (enableNetworkLogs) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Timber.d("[HTTP CALL] $message")
                    }
                }
                level = LogLevel.ALL
            }
        }
    }
