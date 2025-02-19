package com.alphaomardiallo.handydocs.feature.ocr.presentation

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
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
import androidx.core.content.ContextCompat.getString
import com.alphaomardiallo.handydocs.R
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun OcrScreen(viewModel: OcrViewModel = koinViewModel()) {
    val uiState = viewModel.state
    val context = LocalContext.current
    val activity = context as? Activity
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var textFieldValue by remember(uiState.text) {
        mutableStateOf(TextFieldValue(uiState.text ?: ""))
    }

    val filePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            selectedFileUri = uri
        }

    val options = GmsDocumentScannerOptions.Builder()
        .setGalleryImportAllowed(false)
        .setResultFormats(RESULT_FORMAT_JPEG)
        .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
        .build()

    val scanner = GmsDocumentScanning.getClient(options)
    val scannerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) { rawResult ->
        val result = GmsDocumentScanningResult.fromActivityResultIntent(rawResult.data)
        if (rawResult.resultCode == RESULT_OK) {
            activity?.let { nonNullActivity ->
                result?.pages?.mapNotNull { page ->
                    viewModel.analyse(page.imageUri, context)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Button(onClick = { filePickerLauncher.launch(arrayOf("*/*")) }) {
            Text("Select File")
        }
        Button(onClick = {
            activity?.let { nonNullActivity ->
                scanner.getStartScanIntent(nonNullActivity)
                    .addOnSuccessListener {
                        scannerLauncher.launch(
                            IntentSenderRequest.Builder(it).build()
                        )
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            String.format(
                                getString(context, R.string.toast_error_message),
                                it.localizedMessage
                            ),
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }) {
            Text("Take picture")
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
        }
    }
}
