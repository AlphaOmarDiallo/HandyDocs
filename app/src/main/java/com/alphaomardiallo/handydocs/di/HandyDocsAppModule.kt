package com.alphaomardiallo.handydocs.di

import com.alphaomardiallo.handydocs.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.domain.navigator.AppNavigatorImp
import com.alphaomardiallo.handydocs.presentation.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Main
    single<AppNavigator> { AppNavigatorImp() }
    viewModel { MainViewModel(get()) }
}
