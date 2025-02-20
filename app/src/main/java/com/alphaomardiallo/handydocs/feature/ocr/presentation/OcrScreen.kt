package com.alphaomardiallo.handydocs.feature.ocr.presentation

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.alphaomardiallo.handydocs.R
import com.alphaomardiallo.handydocs.feature.ocr.presentation.model.OcrAction
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import org.koin.androidx.compose.koinViewModel
import java.io.File

@Composable
fun OcrScreen(viewModel: OcrViewModel = koinViewModel()) {
    val uiState = viewModel.state
    val context = LocalContext.current.applicationContext
    val activity = context as? Activity
    OcrScreenContent(
        context = context,
        activity = activity,
        state = uiState,
        analyzeImage = viewModel::analyse
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OcrScreenContent(
    context: Context? = null,
    activity: Activity? = null,
    state: OcrUiState = OcrUiState(),
    analyzeImage: (Uri, Context) -> Unit = { _, _ -> }
) {
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var textFieldValue by remember(state.text) {
        mutableStateOf(TextFieldValue(state.text ?: ""))
    }
    var loaderVisibility by remember { mutableStateOf(false) }
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
    val scannerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) { rawResult ->
            val result = GmsDocumentScanningResult.fromActivityResultIntent(rawResult.data)
            if (rawResult.resultCode == RESULT_OK) {
                result?.pages?.mapNotNull { page ->
                    context?.let { analyzeImage.invoke(page.imageUri, it) }
                }
            }
        }
    val clipboardManager = LocalClipboardManager.current

    selectedFileUri?.let { uri -> context?.let { analyzeImage(uri, it) } }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf(
                OcrAction(
                    name = R.string.ocr_camera_button_label,
                    icon = R.drawable.rounded_photo_camera_24,
                    cd = R.string.ocr_camera_button_cd,
                    onClick = {
                        activity?.let { nonNullActivity ->
                            scanner.getStartScanIntent(nonNullActivity)
                                .addOnSuccessListener {
                                    scannerLauncher.launch(
                                        IntentSenderRequest.Builder(it).build()
                                    )
                                }
                                .addOnFailureListener {
                                    context?.let { nonNullContext ->
                                        Toast.makeText(
                                            nonNullContext,
                                            String.format(
                                                getString(context, R.string.toast_error_message),
                                                it.localizedMessage
                                            ),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        }
                    }
                ),
                OcrAction(
                    name = R.string.ocr_folder_button_label,
                    icon = R.drawable.rounded_folder_open_24,
                    cd = R.string.ocr_folder_button_cd,
                    onClick = { filePickerLauncher.launch(arrayOf("*/*")) }
                )
            ).forEachIndexed { index: Int, ocrAction: OcrAction ->
                Card(
                    modifier = Modifier
                        .clickable {
                            loaderVisibility = true
                            ocrAction.onClick.invoke()
                        }
                        .size(180.dp)
                        .weight(1f)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors().copy(
                        containerColor = if (index == 0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary,
                        contentColor = if (index == 0) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = ocrAction.icon),
                            contentDescription = stringResource(id = ocrAction.cd),
                            modifier = Modifier.size(100.dp)
                        )
                        Text(
                            text = stringResource(id = ocrAction.name),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        if (loaderVisibility) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        state.text?.let {
            loaderVisibility = false

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(textFieldValue.text))
                        context?.let { nonNullContext ->
                            Toast.makeText(
                                nonNullContext,
                                "Copied to clipboard",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors()
                        .copy(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.rounded_content_copy_24),
                        contentDescription = ""
                    )
                }
                IconButton(
                    onClick = {
                        context?.let {
                            saveTextAsPdf(
                                context,
                                activity,
                                textFieldValue.text
                            )
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors()
                        .copy(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.rounded_save_24),
                        contentDescription = ""
                    )
                    context?.let { nonNullContext ->
                        Toast.makeText(
                            nonNullContext,
                            "Copied to clipboard",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            TextField(
                value = textFieldValue,
                onValueChange = { newText ->
                    textFieldValue = newText
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

fun saveTextAsPdf(context: Context, activity: Activity?, text: String) {
    val pdfDocument = PdfDocument()

    val bitmap = Bitmap.createBitmap(600, 800, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint().apply {
        color = Color.BLACK
        textSize = 40f
    }
    canvas.drawColor(Color.WHITE)  // Background color
    canvas.drawText(text, 50f, 100f, paint)  // Draw text on the image

    val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val pdfCanvas = page.canvas

    pdfCanvas.drawBitmap(bitmap, 0f, 0f, null)
    pdfDocument.finishPage(page)

    try {
        activity?.let {
            val fileName = "HandyDocs${System.currentTimeMillis()}.pdf"
            val pdfFile = File(it.filesDir, fileName)
            pdfFile.outputStream().use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            Toast.makeText(context, "PDF saved to ${pdfFile.absolutePath}", Toast.LENGTH_LONG)
                .show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error saving PDF", Toast.LENGTH_SHORT).show()
    }

    pdfDocument.close()
}
