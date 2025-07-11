package com.alphaomardiallo.handydocs.common.presentation.main

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alphaomardiallo.handydocs.R
import com.alphaomardiallo.handydocs.common.domain.destination.AppDestination
import com.alphaomardiallo.handydocs.common.domain.model.ImageDoc
import com.alphaomardiallo.handydocs.common.presentation.model.BottomNav
import com.alphaomardiallo.handydocs.common.presentation.navigation.NavigationEffects
import com.alphaomardiallo.handydocs.common.presentation.theme.HandyDocsTheme
import com.alphaomardiallo.handydocs.feature.altgenerator.presentation.AltGenerator
import com.alphaomardiallo.handydocs.feature.docviewer.DocViewerScreen
import com.alphaomardiallo.handydocs.feature.ocr.presentation.OcrScreen
import com.alphaomardiallo.handydocs.feature.pdfsafe.PdfSafeScreen
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            val navController = rememberNavController()
            val currentRoute: MutableState<AppDestination> =
                remember { mutableStateOf(AppDestination.ALTGEN) }
            val state = viewModel.state

            NavigationEffects(
                navigationChannel = viewModel.navigationChannel,
                navHostController = navController
            )

            HandyDocsTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        AppBar(
                            searchFunction = viewModel::searchDoc,
                            searResult = state.searchList,
                            updateSelectedDoc = viewModel::updateDocumentSelected,
                            currentRoute = currentRoute
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ) {
                            listOf(
                                BottomNav(
                                    route = AppDestination.ALTGEN.route,
                                    cd = R.string.alt_gen_destination,
                                    label = R.string.alt_gen_label_bb,
                                    icon = R.drawable.ic_robot,
                                    selectedIcon = R.drawable.ic_robot_fill
                                ),
                                BottomNav(
                                    route = AppDestination.OCR.route,
                                    cd = R.string.ocr_destination,
                                    label = R.string.ocr_label_bb,
                                    icon = R.drawable.ic_scanner,
                                    selectedIcon = R.drawable.ic_scanner_fill
                                ),
                                BottomNav(
                                    route = AppDestination.PDFSAFE.route,
                                    cd = R.string.pdf_safe_destination,
                                    label = R.string.pdf_safe_label_bb,
                                    icon = R.drawable.ic_safe,
                                    selectedIcon = R.drawable.ic_safe_fill
                                )
                            ).forEach { navItem ->
                                val select = currentRoute.value.route == navItem.route
                                NavigationBarItem(
                                    selected = select,
                                    label = {
                                        Text(
                                            text = stringResource(id = navItem.label),
                                            fontWeight = if (select) FontWeight.Bold else FontWeight.Normal
                                        )
                                            },
                                    alwaysShowLabel = true,
                                    icon = {
                                        Icon(
                                            painter = painterResource(id = if (select) navItem.selectedIcon else navItem.icon),
                                            contentDescription = stringResource(id = navItem.cd),
                                            tint = MaterialTheme.colorScheme.onBackground
                                        )
                                    },
                                    onClick = {
                                        if (currentRoute.value.route != navItem.route) {
                                            currentRoute.value = when (navItem.route) {
                                                AppDestination.PDFSAFE.route -> AppDestination.PDFSAFE
                                                AppDestination.OCR.route -> AppDestination.OCR
                                                AppDestination.ALTGEN.route -> AppDestination.ALTGEN
                                                else -> {
                                                    AppDestination.PDFSAFE
                                                }
                                            }
                                            navController.navigate(navItem.route)
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.Black,
                                        indicatorColor = Color.Transparent,
                                        unselectedIconColor = Color.Black
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->

                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = AppDestination.ALTGEN.route
                        ) {
                            appDestination()
                        }
                    }
                }
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun AppBar(
        searchFunction: (String) -> Unit = {},
        searResult: List<ImageDoc> = emptyList(),
        updateSelectedDoc: (ImageDoc) -> Unit = {},
        currentRoute: MutableState<AppDestination>
    ) {
        val context = LocalContext.current
        val activity = context as? Activity

        val options = GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(true)
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
                            val fileName = "HandyDocs${System.currentTimeMillis()}.pdf"
                            val file = File(nonNullActivity.filesDir, fileName)
                            val fos = FileOutputStream(file)

                            nonNullActivity.contentResolver.openInputStream(pdf.uri)?.use {
                                it.copyTo(fos)
                            }

                            val savedFileUri = Uri.fromFile(file)

                            val savedJpegUris = result.pages?.mapNotNull { page ->
                                page.imageUri.let { uri ->
                                    // Generate a unique file name for each JPEG image
                                    val jpegFileName =
                                        "HandyDocsImage_${System.currentTimeMillis()}.jpeg"
                                    val jpegFile = File(nonNullActivity.filesDir, jpegFileName)

                                    // Save the JPEG file
                                    nonNullActivity.contentResolver.openInputStream(uri)
                                        ?.use { inputStream ->
                                            FileOutputStream(jpegFile).use { outputStream ->
                                                inputStream.copyTo(outputStream)
                                            }
                                        }

                                    // Return the URI of the saved JPEG file
                                    Uri.fromFile(jpegFile)
                                }
                            } ?: emptyList()

                            viewModel.savePdfInDatabase(
                                ImageDoc(
                                    name = "HandyDocs${System.currentTimeMillis()}.pdf",
                                    uriJpeg = savedJpegUris,
                                    displayName = null,
                                    uriPdf = savedFileUri,
                                    time = System.currentTimeMillis(),
                                )
                            )

                            viewModel.navigateTo(AppDestination.PDFSAFE.route, isSingleTop = true)
                        }
                    }
                } else {
                    Timber.e(rawResult.resultCode.toString())
                    viewModel.navigateTo(AppDestination.PDFSAFE.route, isSingleTop = true)
                }
            }

        var showSearchDialog by remember { mutableStateOf(false) }

        var showDialogViewer by remember { mutableStateOf(false) }

        Column(Modifier.fillMaxWidth()) {
            TopAppBar(
                title = {
                    val title = when (currentRoute.value) {
                        AppDestination.PDFSAFE -> R.string.pdf_safe_label
                        AppDestination.OCR -> R.string.ocr_label
                        AppDestination.ALTGEN -> R.string.alt_gen_label
                    }
                    Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                        Text(
                            text = stringResource(id = title),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight(
                                    1000
                                )
                            )
                        )
                    }
                },
                actions = {
                    if (currentRoute.value == AppDestination.PDFSAFE) {
                        IconButton(onClick = {
                            showSearchDialog = true
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.rounded_search_24),
                                contentDescription = Icons.Default.Search.name
                            )
                        }

                        IconButton(onClick = {
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
                                                getString(R.string.toast_error_message),
                                                it.localizedMessage
                                            ),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_document_scanner_24),
                                contentDescription = Icons.Default.Add.name
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        if (showSearchDialog) {
            BasicAlertDialog(
                onDismissRequest = { showSearchDialog = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                var text by remember { mutableStateOf("") }

                LaunchedEffect(text) {
                    searchFunction.invoke(text)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Column {
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            maxLines = 1,
                            placeholder = { Text(text = stringResource(id = R.string.main_search_hint)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            trailingIcon = {
                                IconButton(onClick = { text = "" }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_clear_24),
                                        contentDescription = stringResource(id = R.string.main_clear)
                                    )
                                }
                            }
                        )

                        if (searResult.isNotEmpty()) {
                            searResult.forEach {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                        .clickable {
                                            Timber.d("Item with id: ${it.id} was clicked")
                                            updateSelectedDoc.invoke(it)
                                            showDialogViewer = true
                                        },
                                    colors = CardDefaults.cardColors().copy(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    ),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                ) {
                                    Text(
                                        text = it.displayName ?: it.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = stringResource(id = R.string.main_search_unnamed_warning),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = { showSearchDialog = false },
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Text(text = stringResource(id = R.string.main_search_close))
                            }
                        }
                    }
                }
            }
        }

        if (showDialogViewer) {
            BasicAlertDialog(
                onDismissRequest = { showDialogViewer = false },
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
}

private fun NavGraphBuilder.appDestination(): NavGraphBuilder = this.apply {
    composable(route = AppDestination.PDFSAFE.route) {
        PdfSafeScreen()
    }
    composable(route = AppDestination.OCR.route) {
        OcrScreen()
    }
    composable(route = AppDestination.ALTGEN.route) {
        AltGenerator()
    }
}
