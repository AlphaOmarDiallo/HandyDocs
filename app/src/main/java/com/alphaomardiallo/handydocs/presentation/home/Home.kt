package com.alphaomardiallo.handydocs.presentation.home

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.alphaomardiallo.handydocs.R
import com.alphaomardiallo.handydocs.domain.model.ImageDoc
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.getAllImages()

    HomeContent(list = uiState.value.allImageDoc)
}

@Composable
private fun HomeContent(list: List<ImageDoc> = emptyList()) {
    if (list.isEmpty()) {
        ListEmptyScreen()
    } else {
        ListNotEmptyScreen(list)
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
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
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
