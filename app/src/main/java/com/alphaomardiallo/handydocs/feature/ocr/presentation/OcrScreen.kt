package com.alphaomardiallo.handydocs.feature.ocr.presentation

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.alphaomardiallo.handydocs.R
import com.alphaomardiallo.handydocs.common.domain.model.ImageDoc
import com.alphaomardiallo.handydocs.feature.ocr.domain.TextRecognitionType
import com.alphaomardiallo.handydocs.feature.ocr.presentation.model.OcrAction
import com.alphaomardiallo.handydocs.feature.ocr.presentation.util.saveTextAsPdf
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import org.koin.androidx.compose.koinViewModel

@Composable
fun OcrScreen(viewModel: OcrViewModel = koinViewModel()) {
    val uiState = viewModel.state
    val context = LocalContext.current
    val activity = context as? Activity
    OcrScreenContent(
        context = context,
        activity = activity,
        state = uiState,
        analyzeImage = viewModel::analyse,
        saveImage = viewModel::saveImageDoc,
        updateScript = viewModel::updateScript,
        updateLoading = viewModel::updateLoadingState
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OcrScreenContent(
    context: Context? = null,
    activity: Activity? = null,
    state: OcrUiState = OcrUiState(),
    analyzeImage: (Uri, Context) -> Unit = { _, _ -> },
    saveImage: (ImageDoc) -> Unit = {},
    updateScript: (TextRecognitionType) -> Unit = {},
    updateLoading: (Boolean) -> Unit = {}
) {
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var textFieldValue by remember(state.text) {
        mutableStateOf(TextFieldValue(state.text ?: ""))
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
    var docName by remember { mutableStateOf("") }
    var showDialogChooseName by remember { mutableStateOf(false) }

    LaunchedEffect(selectedFileUri) {
        selectedFileUri?.let { uri ->
            context?.let { analyzeImage(uri, it) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.ocr_open_script),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            style = MaterialTheme.typography.titleLarge
        )
        var expanded by remember { mutableStateOf(false) }
        var selectedOption by remember { mutableStateOf(TextRecognitionType.LATIN) }

        LaunchedEffect(selectedOption) { updateScript.invoke(selectedOption) }

        val menuOptions = listOf(
            TextRecognitionType.LATIN,
            TextRecognitionType.KOREAN,
            TextRecognitionType.JAPANESE,
            TextRecognitionType.CHINESE,
            TextRecognitionType.DEVANAGARI
        )

        Column {
            // The box that triggers the dropdown
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                    .padding(16.dp)
                    .clickable { expanded = true }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = selectedOption.label))
                    Icon(
                        imageVector = if (expanded)
                            Icons.Default.KeyboardArrowUp
                        else
                            Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(id = R.string.dropdown_arrow_cd)
                    )
                }
            }

            // The dropdown menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 8.dp)
            ) {
                menuOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = option.label)) },
                        onClick = {
                            selectedOption = option
                            expanded = false
                        }
                    )
                }
            }
        }

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
                    onClick = {
                        filePickerLauncher.launch(arrayOf("*/*"))
                        updateLoading.invoke(false)
                    }
                )
            ).forEachIndexed { index: Int, ocrAction: OcrAction ->
                Card(
                    modifier = Modifier
                        .clickable {
                            selectedFileUri = null
                            ocrAction.onClick.invoke()
                        }
                        .size(180.dp)
                        .weight(1f)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors().copy(
                        containerColor = if (index == 0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary,
                        contentColor = if (index == 0) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onTertiary
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
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

        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), progress = { 1f })
        }

        if (state.fileError || state.recognitionError || state.invalidImage) {
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = Icons.Default.Warning.name,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = stringResource(id = R.string.ocr_analysis_error),
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        state.text?.let {
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
                                getString(context, R.string.ocr_toast_copied),
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
                        contentDescription = stringResource(id = R.string.ocr_copy_cd)
                    )
                }
                IconButton(
                    onClick = {
                        context?.let { showDialogChooseName = true }
                    },
                    colors = IconButtonDefaults.iconButtonColors()
                        .copy(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.rounded_save_24),
                        contentDescription = stringResource(id = R.string.ocr_copy_cd)
                    )
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

    if (showDialogChooseName) {
        AlertDialog(
            onDismissRequest = { showDialogChooseName = false },
            title = { Text(stringResource(id = R.string.ocr_dialog_doc_name_title)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = docName,
                        onValueChange = { docName = it },
                        label = { Text(stringResource(id = R.string.ocr_dialog_doc_name_label)) }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (docName.isNotBlank()) {
                        context?.let {
                            saveTextAsPdf(
                                context = context,
                                activity = activity,
                                docName = docName,
                                text = textFieldValue.text,
                                saveImage = saveImage
                            )
                            showDialogChooseName = false
                        }
                    }
                }) {
                    Text(stringResource(id = R.string.ocr_dialog_doc_name_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialogChooseName = false }) {
                    Text(stringResource(id = R.string.ocr_dialog_doc_name_cancel))
                }
            }
        )
    }
}
