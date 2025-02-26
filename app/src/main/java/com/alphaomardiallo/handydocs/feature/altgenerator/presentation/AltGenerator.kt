package com.alphaomardiallo.handydocs.feature.altgenerator.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun AltGenerator(viewModel: AltGeneratorViewModel = koinViewModel()) {
    var text = ""
    LaunchedEffect(Unit) {
        text = viewModel.getString()
    }
    Text(text = text)
}
