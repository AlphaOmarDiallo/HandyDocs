package com.alphaomardiallo.handydocs.feature.pdfsafe

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.alphaomardiallo.handydocs.R
import com.alphaomardiallo.handydocs.common.domain.model.ImageDoc
import com.alphaomardiallo.handydocs.common.presentation.ad.AdmobGenericBanner
import com.alphaomardiallo.handydocs.feature.docviewer.DocViewerScreen
import org.koin.androidx.compose.koinViewModel
import java.io.File

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val uiState = viewModel.state
    HomeContent(
        list = uiState.allImageDoc,
        updateDoc = viewModel::updateDocumentName,
        updateSelectedDoc = viewModel::updateDocumentSelected,
        filterList = viewModel::getAllImageTest,
        updateFavorite = viewModel::updateDocumentFavorite,
        currentFilterType = viewModel.state.filterType
    )
}

@Composable
private fun HomeContent(
    list: List<ImageDoc> = emptyList(),
    updateDoc: (ImageDoc, String) -> Unit,
    updateSelectedDoc: (ImageDoc) -> Unit,
    filterList: (ListFilter) -> Unit,
    updateFavorite: (ImageDoc) -> Unit,
    currentFilterType: ListFilter
) {
    if (list.isEmpty() && currentFilterType == ListFilter.None) {
        ListEmptyScreen()
    } else {
        ListNotEmptyScreen(
            list,
            updateDoc,
            updateSelectedDoc,
            filterList,
            updateFavorite,
            currentFilterType
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ListEmptyScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.pdf_safe_empty_list),
            style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListNotEmptyScreen(
    list: List<ImageDoc> = emptyList(),
    updateDoc: (ImageDoc, String) -> Unit,
    updateSelectedDoc: (ImageDoc) -> Unit,
    filterList: (ListFilter) -> Unit,
    updateFavorite: (ImageDoc) -> Unit,
    currentFilterType: ListFilter
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var showDialogViewer by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<ImageDoc?>(null) }
    var selectedFilters by remember { mutableStateOf<ListFilter>(ListFilter.None) }

    LaunchedEffect(selectedFilters) {
        filterList.invoke(selectedFilters)
    }

    Column {
        if (list.size > 1 || currentFilterType == ListFilter.Favorite) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                val paddingDefault = 16.dp

                val filterOptions = listOf(
                    ListFilter.Favorite,
                    ListFilter.NameAsc,
                    ListFilter.NameDesc,
                    ListFilter.TimeAsc,
                    ListFilter.TimeDesc
                )

                val numberString = context.resources.getQuantityString(
                    R.plurals.pdf_safe_number_items,
                    list.size,
                    list.size
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.pdf_safe_filter_title),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(
                            top = paddingDefault,
                            start = paddingDefault,
                            end = paddingDefault
                        )
                    )
                    Text(
                        text = numberString,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(
                            top = paddingDefault,
                            start = paddingDefault,
                            end = paddingDefault
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = paddingDefault)
                ) {
                    filterOptions.forEach { option ->
                        FilterChip(
                            selected = selectedFilters == option,
                            onClick = {
                                selectedFilters = if (selectedFilters == option) {
                                    ListFilter.None
                                } else {
                                    option
                                }
                            },
                            label = { Text(text = stringResource(id = option.label)) },
                            leadingIcon = {
                                if (selectedFilters == option) {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = String.format(
                                            stringResource(id = R.string.icon_content_description),
                                            Icons.Default.Done.name
                                        )
                                    )
                                }
                            }
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                    }
                }

                HorizontalDivider(Modifier.fillMaxWidth(), thickness = 2.dp)
            }
        }

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = 4.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            items(list) { doc ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = RectangleShape,
                    colors = CardDefaults.cardColors()
                        .copy(
                            containerColor = Color.White,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AsyncImage(
                            model = doc.uriJpeg.first(),
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(16.dp)
                                .clickable {
                                    updateSelectedDoc.invoke(doc)
                                    showDialogViewer = true
                                }
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedTextField(
                                value = doc.displayName
                                    ?: stringResource(id = R.string.pdf_safe_no_name_picture),
                                onValueChange = {},
                                modifier = Modifier.padding(8.dp),
                                enabled = false,
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            selected = doc
                                            showDialog = true
                                        },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = String.format(
                                                stringResource(id = R.string.icon_content_description),
                                                Icons.Default.Edit.name
                                            ),
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.height(20.dp)
                                        )
                                    }
                                },
                                colors = TextFieldDefaults.colors().copy(
                                    disabledTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            )
                        }

                        Text(
                            text = doc.getReadableTime(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            IconButton(onClick = {
                                // Path to the PDF file in internal storage
                                val pdfFile = File(doc.uriPdf?.path ?: "")

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
                                    ),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = { updateFavorite.invoke(doc) }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = String.format(
                                        stringResource(id = R.string.icon_content_description),
                                        Icons.Default.Star.name
                                    ),
                                    tint = if (doc.isFavorite) Color.Yellow else Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        AdmobGenericBanner(modifier = Modifier.fillMaxWidth().padding(4.dp))
    }

    if (showDialog && selected != null) {
        BasicAlertDialog(
            onDismissRequest = { showDialog = false },
            modifier = Modifier
                .height(200.dp)
                .width(600.dp),
        ) {
            var text by remember { mutableStateOf(selected?.displayName ?: "") }

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
                            onClick = { showDialog = false },
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
                                updateDoc.invoke(selected!!, text)
                                showDialog = false
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

    if (showDialogViewer) {
        BasicAlertDialog(
            onDismissRequest = { showDialog = false },
            modifier = Modifier.fillMaxSize(),
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                DocViewerScreen {
                    showDialogViewer = false
                }
            }
        }
    }
}
