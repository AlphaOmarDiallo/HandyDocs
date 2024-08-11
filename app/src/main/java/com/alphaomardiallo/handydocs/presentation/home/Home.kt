package com.alphaomardiallo.handydocs.presentation.home

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.getAllImages()

    Timber.d(uiState.value.allImageDoc.size.toString())
}
