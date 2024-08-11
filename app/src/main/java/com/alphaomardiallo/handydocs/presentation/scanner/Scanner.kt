package com.alphaomardiallo.handydocs.presentation.scanner

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.alphaomardiallo.handydocs.domain.model.FormatType
import com.alphaomardiallo.handydocs.domain.model.ImageDoc
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

@Composable
fun ScannerLauncher(viewModel: ScannerViewModel = koinViewModel()) {

    val context = LocalContext.current
    val activity = context as? Activity

    val options = GmsDocumentScannerOptions.Builder()
        .setGalleryImportAllowed(true)
        .setPageLimit(5)
        .setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
        .setScannerMode(SCANNER_MODE_FULL)
        .build()
    val scanner = GmsDocumentScanning.getClient(options)

    val scannerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) { rawResult ->
            val result = GmsDocumentScanningResult.fromActivityResultIntent(rawResult.data)
            if (rawResult.resultCode == RESULT_OK) {
                activity?.let { nonNullActivity ->
                    result?.pdf?.let { pdf ->
                        val fos = FileOutputStream(
                            File(
                                nonNullActivity.filesDir,
                                "HandyDocs${System.currentTimeMillis()}.pdf"
                            )
                        )
                        nonNullActivity.contentResolver.openInputStream(pdf.uri)?.use {
                            it.copyTo(fos)
                            viewModel.savePdfInDatabase(
                                ImageDoc(
                                    name = "HandyDocs${System.currentTimeMillis()}.pdf",
                                    uri = pdf.uri,
                                    displayName = null,
                                    formatType = FormatType.PDF
                                )
                            )
                            Timber.d("Copied to fos")
                        }
                    }
                }
            } else {
                Toast.makeText(
                    context,
                    "Something went wrong: ${rawResult.resultCode}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    activity?.let { nonNullActivity ->
        scanner.getStartScanIntent(nonNullActivity)
            .addOnSuccessListener {
                Timber.d("Success: $it")
                scannerLauncher.launch(
                    IntentSenderRequest.Builder(it).build()
                )
            }
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Error: $it",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnCanceledListener {
                Toast.makeText(
                    context,
                    "Cancelled",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}
