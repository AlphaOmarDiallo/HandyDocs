package com.alphaomardiallo.handydocs.feature.ocr.presentation

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.alphaomardiallo.handydocs.feature.pdfsafe.HomeViewModel.HomeUiState
import org.koin.androidx.compose.koinViewModel

@Composable
fun OcrScreen(viewModel: OcrViewModel = koinViewModel()){
    val uiState = viewModel.state
    val context = LocalContext.current
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var textFieldValue by remember(uiState.text) {
        mutableStateOf(TextFieldValue(uiState.text ?: ""))
    }

    val filePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            selectedFileUri = uri
        }

    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
        Button(onClick = { filePickerLauncher.launch(arrayOf("*/*")) }) {
            Text("Select File")
        }

        selectedFileUri?.let {
            Text("Selected: ${it.path}", modifier = Modifier.padding(top = 8.dp))
            viewModel.analyse(it, context)
        }

        uiState.text?.let {
            TextField(
                value = textFieldValue,
                onValueChange = { newText ->
                    textFieldValue = newText
                }
            )
            Log.e("OCR", "OCR : $it")
        }
    }
}
