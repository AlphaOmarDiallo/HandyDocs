package com.alphaomardiallo.handydocs.feature.docviewer

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.alphaomardiallo.handydocs.R
import com.alphaomardiallo.handydocs.common.domain.model.ImageDoc
import com.alphaomardiallo.handydocs.common.presentation.composable.AdmobGenericBanner
import com.github.panpf.zoomimage.CoilZoomAsyncImage
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber
import java.io.File

@Composable
fun DocViewerScreen(viewModel: DocViewerViewModel = koinViewModel(), onClose: () -> Unit) {
    val uiState = viewModel.state
    DocViewerScreenContent(
        doc = uiState.selectedImage,
        onClose = onClose,
        updateDoc = viewModel::updateDocumentName,
        deleteDoc = viewModel::deleteDocument,
        selectedToNull = viewModel::selectedImageToNull
    )

    BackHandler {
        onClose.invoke()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DocViewerScreenContent(
    doc: ImageDoc? = null,
    onClose: () -> Unit = {},
    updateDoc: (ImageDoc, String) -> Unit = { _, _ -> },
    deleteDoc: (ImageDoc) -> Unit = {},
    selectedToNull: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { doc?.uriJpeg?.size ?: 1 })
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var showDialogShare by remember { mutableStateOf(false) }
    var showDialogEdit by remember { mutableStateOf(false) }
    var showDialogDelete by remember { mutableStateOf(false) }
    val docViewerTitle =
        if (doc?.displayName != null) doc.displayName else stringResource(id = R.string.doc_viewer_no_name)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = docViewerTitle) },
                navigationIcon = {
                    IconButton(onClick = {
                        selectedToNull.invoke()
                        onClose.invoke()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = String.format(
                                stringResource(id = R.string.icon_content_description),
                                Icons.Default.Close.name
                            ),
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showDialogEdit = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = String.format(
                                stringResource(id = R.string.icon_content_description),
                                Icons.Default.Edit.name
                            ),
                        )
                    }
                    IconButton(onClick = {
                        // Path to the PDF file in internal storage
                        val pdfFile = File(doc?.uriPdf?.path ?: "")

                        // Check if the file exists
                        if (pdfFile.exists()) {
                            // Generate the URI using FileProvider
                            val pdfUri: Uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                pdfFile
                            )

                            // Create the share intent
                            val shareIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "application/pdf"
                                putExtra(Intent.EXTRA_STREAM, pdfUri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }

                            // Start the share intent
                            context.startActivity(
                                Intent.createChooser(
                                    shareIntent,
                                    context.getString(R.string.pdf_safe_share_pdf)
                                )
                            )
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.pdf_safe_pdf_not_found),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = String.format(
                                stringResource(id = R.string.icon_content_description),
                                Icons.Default.Share.name
                            )
                        )
                    }
                    IconButton(onClick = {
                        showDialogDelete = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = String.format(
                                stringResource(id = R.string.icon_content_description),
                                Icons.Default.Delete.name
                            ),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    actionIconContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    doc?.let {
                        it.uriJpeg.forEachIndexed { index, _ ->
                            Column(modifier = Modifier.width(20.dp)) {
                                Text(
                                    text = (index + 1).toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .clickable {
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(index)
                                            }
                                        }
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                                if (pagerState.currentPage == index) {
                                    HorizontalDivider(
                                        thickness = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (doc != null) {
                Column {
                    CoilZoomAsyncImage(
                        model = doc.uriJpeg[pagerState.currentPage],
                        contentDescription = "nothing",
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    )
                    AdmobGenericBanner(modifier = Modifier.padding(4.dp))
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(200.dp))
                }
            }
        }
    }

    if (showDialogShare) {
        BasicAlertDialog(
            onDismissRequest = { showDialogShare = false },
            modifier = Modifier
                .height(200.dp)
                .width(500.dp),
        ) {
            var text by remember { mutableStateOf(doc?.displayName ?: "") }

            Card(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.pdf_safe_dialog_edit_doc_name),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        maxLines = 1,
                        placeholder = { Text(text = stringResource(id = R.string.pdf_safe_dialog_edit_doc_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(
                            onClick = { showDialogShare = false },
                            colors = IconButtonDefaults.iconButtonColors()
                                .copy(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = String.format(
                                    stringResource(id = R.string.icon_content_description),
                                    Icons.Default.Close.name
                                )
                            )
                        }
                        IconButton(
                            onClick = {
                                updateDoc.invoke(doc!!, text)
                                showDialogShare = false
                            },
                            colors = IconButtonDefaults.iconButtonColors()
                                .copy(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = String.format(
                                    stringResource(id = R.string.icon_content_description),
                                    Icons.Default.Done.name
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialogEdit && doc != null) {
        BasicAlertDialog(
            onDismissRequest = { showDialogEdit = false },
            modifier = Modifier
                .height(200.dp)
                .width(600.dp),
        ) {
            var text by remember { mutableStateOf(doc.displayName ?: "") }

            Card(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.pdf_safe_dialog_edit_doc_name),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        placeholder = { Text(text = stringResource(id = R.string.pdf_safe_dialog_edit_doc_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(
                            onClick = { showDialogEdit = false },
                            colors = IconButtonDefaults.iconButtonColors()
                                .copy(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = String.format(
                                    stringResource(id = R.string.icon_content_description),
                                    Icons.Default.Close.name
                                )
                            )
                        }
                        IconButton(
                            onClick = {
                                updateDoc.invoke(doc, text)
                                showDialogEdit = false
                            },
                            colors = IconButtonDefaults.iconButtonColors()
                                .copy(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = String.format(
                                    stringResource(id = R.string.icon_content_description),
                                    Icons.Default.Done.name
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialogDelete && doc != null) {
        BasicAlertDialog(
            onDismissRequest = { showDialogEdit = false },
            modifier = Modifier
                .wrapContentHeight()
                .width(600.dp),
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.doc_viewer_delete_title),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = stringResource(id = R.string.doc_viewer_delete_text),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                showDialogDelete = false
                            },
                            modifier = Modifier.wrapContentWidth(),
                            colors = ButtonDefaults.buttonColors()
                                .copy(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_clear_24),
                                    contentDescription = Icons.Default.Close.name
                                )
                                Text(text = stringResource(id = R.string.doc_viewer_keep))
                            }
                        }
                        Button(
                            onClick = {
                                if (doc.uriPdf != null) {
                                    if (deleteFiles(doc.uriPdf)) {
                                        doc.uriJpeg.map { deleteFiles(it) }
                                        deleteDoc.invoke(doc)
                                        showDialogDelete = false
                                        onClose.invoke()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.doc_viewer_error_deleting_file),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        showDialogDelete = false
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.doc_viewer_error_deleting_file),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    showDialogDelete = false
                                }
                            },
                            modifier = Modifier.wrapContentWidth(),
                            colors = ButtonDefaults.buttonColors()
                                .copy(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = String.format(
                                        stringResource(id = R.string.icon_content_description),
                                        Icons.Default.Delete.name
                                    )
                                )
                                Text(text = stringResource(id = R.string.doc_viewer_delete))
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun deleteFiles(uri: Uri): Boolean {
    return try {
        val file = File(Uri.parse(uri.toString()).path!!)
        if (file.exists()) file.delete() else false
    } catch (e: Exception) {
        Timber.e(e.localizedMessage)
        false
    }
}
