package com.alphaomardiallo.handydocs.feature.altgenerator.presentation

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.alphaomardiallo.handydocs.R
import com.alphaomardiallo.handydocs.common.presentation.composable.LoadImage
import com.alphaomardiallo.handydocs.common.presentation.composable.LottieWithCoilPlaceholder
import com.alphaomardiallo.handydocs.common.presentation.composable.SourceCard
import com.alphaomardiallo.handydocs.feature.altgenerator.presentation.model.Language
import com.alphaomardiallo.handydocs.feature.altgenerator.presentation.model.Language.Companion.listOfLanguages
import com.alphaomardiallo.handydocs.feature.ocr.presentation.model.OcrAction
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun AltGenerator(viewModel: AltGeneratorViewModel = koinViewModel()) {
    val state = viewModel.state
    val context = LocalContext.current
    var selectedFileUri by remember { mutableStateOf<String?>(null) }
    val filePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            selectedFileUri = uri?.toString()
        }
    var showDialogPasteLink by remember { mutableStateOf(false) }
    var newUrl by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(Language.ENGLISH) }
    val menuOptions = listOfLanguages()
    var textFieldValue by remember(state.altText) {
        mutableStateOf(TextFieldValue(state.altText ?: ""))
    }
    val clipboardManager = LocalClipboardManager.current
    var showFileSelectionUI by remember { mutableStateOf(true) }
    var showImageUI by remember { mutableStateOf(false) }
    var contextText by remember { mutableStateOf(TextFieldValue("")) }
    var maxCharCount by remember { mutableStateOf("2000") }

    LaunchedEffect(selectedFileUri) {
        if (selectedFileUri != null) {
            showFileSelectionUI = false
            delay(500)
            showImageUI = true
            viewModel.imageToBase64(selectedFileUri!!)
            contextText = TextFieldValue("")
        } else {
            showImageUI = false
            showFileSelectionUI = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AnimatedVisibility(
            visible = showFileSelectionUI,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.alt_generator_generate_welcome_text),
                    modifier = Modifier.padding(bottom = 8.dp),
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.bodySmall
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(
                        OcrAction(
                            name = R.string.ocr_folder_button_label,
                            icon = R.drawable.rounded_folder_open_24,
                            lottie = R.raw.file_anim,
                            cd = R.string.ocr_folder_button_cd,
                            onClick = { filePickerLauncher.launch(arrayOf("*/*")) }
                        )
                    ).forEachIndexed { index: Int, ocrAction: OcrAction ->
                        SourceCard(
                            modifier = Modifier
                                .weight(1f)
                                .size(180.dp)
                                .padding(8.dp),
                            index = index,
                            containerColor = if (index == 0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary,
                            contentColor = if (index == 0) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onTertiary,
                            onClick = {
                                selectedFileUri = null
                                ocrAction.onClick.invoke()
                            }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                LottieWithCoilPlaceholder(
                                    image = ocrAction.icon,
                                    lottieJson = ocrAction.lottie,
                                    size = 100.dp
                                )
                                Text(
                                    text = stringResource(id = ocrAction.name),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showImageUI,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally()
        ) {
            selectedFileUri?.let {
                Column {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(8.dp),
                        colors = CardDefaults.cardColors()
                            .copy(containerColor = MaterialTheme.colorScheme.background),
                        border = BorderStroke(
                            width = 4.dp,
                            color = if (state.isLoading) {
                                val infiniteTransition =
                                    rememberInfiniteTransition(label = "borderPulse")
                                val borderColor by infiniteTransition.animateColor(
                                    initialValue = MaterialTheme.colorScheme.primary,
                                    targetValue = MaterialTheme.colorScheme.primaryContainer,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(800),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "borderColorAnimation"
                                )
                                borderColor
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    ) {
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
                                Text(text = selectedOption.displayName)
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
                                    text = { Text(text = option.displayName) },
                                    onClick = {
                                        selectedOption = option
                                        expanded = false
                                    }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .weight(1f),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LoadImage(url = it, modifier = Modifier.size(130.dp))
                            Spacer(modifier = Modifier.padding(8.dp))
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.End
                            ) {
                                Row {
                                    IconButton(onClick = { filePickerLauncher.launch(arrayOf("*/*")) }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.rounded_folder_open_24),
                                            contentDescription = stringResource(id = R.string.ocr_folder_button_cd)
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        viewModel.getAltText(
                                            source = state.base64String!!,
                                            language = selectedOption,
                                            contextText = contextText.text,
                                            maxChar = maxCharCount.toIntOrNull() ?: 2000
                                        )
                                    },
                                    enabled = !state.isLoading
                                ) {
                                    Text(text = stringResource(id = R.string.alt_generator_generate_label))
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = contextText,
                                onValueChange = { contextText = it },
                                modifier = Modifier
                                    .weight(0.6f)
                                    .padding(8.dp)
                                    .height(90.dp),
                                placeholder = { Text(text = stringResource(R.string.alt_generator_context_placeholder)) },
                                label = { Text(text = stringResource(id = R.string.alt_generator_context_label)) },
                                minLines = 3,
                                maxLines = 3
                            )
                            OutlinedTextField(
                                value = maxCharCount,
                                onValueChange = {
                                    // Only allow numeric input
                                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                        maxCharCount = it
                                    }
                                },
                                modifier = Modifier
                                    .weight(0.4f)
                                    .padding(horizontal = 8.dp)
                                    .height(90.dp),
                                label = { Text(text = stringResource(id = R.string.alt_generator_char_label)) },
                                placeholder = { Text(text = stringResource(R.string.alt_generator_char_placeholder)) },
                                singleLine = true
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = state.altText != null,
                        enter = fadeIn() + expandHorizontally(),
                        exit = fadeOut() + shrinkHorizontally()
                    ) {
                        state.altText?.let {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(textFieldValue.text))
                                            context.let { nonNullContext ->
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
                }
            }
        }
    }

    if (state.error) {
        state.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    if (showDialogPasteLink) {
        AlertDialog(
            onDismissRequest = { showDialogPasteLink = false },
            title = { Text(stringResource(id = R.string.alt_generator_dialog_url_title)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newUrl,
                        onValueChange = { newUrl = it },
                        label = { Text(stringResource(id = R.string.ocr_dialog_doc_name_label)) }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newUrl.isNotBlank()) {
                        selectedFileUri = newUrl
                        showDialogPasteLink = false
                    }
                }) {
                    Text(stringResource(id = R.string.ocr_dialog_doc_name_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialogPasteLink = false }) {
                    Text(stringResource(id = R.string.ocr_dialog_doc_name_cancel))
                }
            }
        )
    }
}
