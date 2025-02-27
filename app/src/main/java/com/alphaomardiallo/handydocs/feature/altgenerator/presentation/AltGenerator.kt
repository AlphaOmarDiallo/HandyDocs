package com.alphaomardiallo.handydocs.feature.altgenerator.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun AltGenerator(viewModel: AltGeneratorViewModel = koinViewModel()) {

    val state = viewModel.state

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        state.altText?.let { Timber.e(it) }
    }
}
