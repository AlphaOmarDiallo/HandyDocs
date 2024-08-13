package com.alphaomardiallo.handydocs.presentation.main

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alphaomardiallo.handydocs.R
import com.alphaomardiallo.handydocs.domain.destination.AppDestination
import com.alphaomardiallo.handydocs.domain.model.ImageDoc
import com.alphaomardiallo.handydocs.presentation.home.HomeScreen
import com.alphaomardiallo.handydocs.presentation.navigation.NavigationEffects
import com.alphaomardiallo.handydocs.presentation.theme.HandyDocsTheme
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

        setContent {
            val navController = rememberNavController()

            NavigationEffects(
                navigationChannel = viewModel.navigationChannel,
                navHostController = navController
            )

            Timber.e("SIZE filesDir ${this.fileList().size} - Size cacheDir ${this.cacheDir.listFiles()?.size}")

            HandyDocsTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { AppBar() }
                ) { innerPadding ->

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceBright,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = AppDestination.Home.route
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
    private fun AppBar() {
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
                                    val jpegFileName = "HandyDocsImage_${System.currentTimeMillis()}.jpeg"
                                    val jpegFile = File(nonNullActivity.filesDir, jpegFileName)

                                    // Save the JPEG file
                                    nonNullActivity.contentResolver.openInputStream(uri)?.use { inputStream ->
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
                                    uriPdf = savedFileUri
                                )
                            )
                            Timber.d("Copied to fos")

                            viewModel.navigateBack()
                        }
                    }
                } else {
                    Timber.e(rawResult.resultCode.toString())
                    viewModel.navigateBack()
                }
            }

        TopAppBar(
            title = { Text(text = stringResource(id = R.string.app_name_formated)) },
            actions = {
                IconButton(onClick = {
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
                                    "Error: ${it.message}",
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
            },
            colors = TopAppBarDefaults.topAppBarColors().copy(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }
}

private fun NavGraphBuilder.appDestination(): NavGraphBuilder = this.apply {
    composable(route = AppDestination.Home.route) {
        HomeScreen()
    }
}
