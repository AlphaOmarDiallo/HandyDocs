package com.alphaomardiallo.handydocs.presentation.home

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.alphaomardiallo.handydocs.R
import com.alphaomardiallo.handydocs.domain.model.ImageDoc
import org.koin.androidx.compose.koinViewModel
import java.io.File

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    // FIXME: BUGGY
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(list = uiState.value.allImageDoc)
}

@Composable
private fun HomeContent(list: List<ImageDoc> = emptyList()) {
    if (list.isEmpty()) {
        ListEmptyScreen()
    } else {
        ListNotEmptyScreenV2(list)
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
            text = stringResource(id = R.string.home_empty_list),
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ListNotEmptyScreen(list: List<ImageDoc> = emptyList()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        list.forEach { imageDoc ->
            ImageDocCard(imageDoc = imageDoc)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ListNotEmptyScreenV2(list: List<ImageDoc> = emptyList()) {
    val context = LocalContext.current

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        items(list) { photo ->
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
                        model = photo.uriJpeg.first(),
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(16.dp)
                    )
                    Text(
                        text = photo.displayName ?: "No name",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(onClick = {
                            // Path to the PDF file in internal storage
                            val pdfFile = File(photo.uriPdf?.path ?: "")

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
                                    Intent.createChooser(shareIntent, "Share PDF")
                                )
                            } else {
                                Toast.makeText(context,
                                    "PDF file not found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = Icons.Default.Share.name,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = Icons.Default.Star.name
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ImageDocCard(
    imageDoc: ImageDoc? = ImageDoc(
        name = "test",
        uriJpeg = listOf(Uri.EMPTY),
        displayName = "Display name",
    )
) {
    Card(
        onClick = { /*TODO*/ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = imageDoc?.displayName ?: "noName")
            }

            AsyncImage(
                model = imageDoc?.uriJpeg?.first(),
                contentDescription = "preview",
                modifier = Modifier.size(100.dp)
            )
        }
    }
}
