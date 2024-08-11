package com.alphaomardiallo.handydocs.presentation.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AddCircle
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alphaomardiallo.handydocs.R
import com.alphaomardiallo.handydocs.domain.destination.AppDestination
import com.alphaomardiallo.handydocs.presentation.camera.CameraScreen
import com.alphaomardiallo.handydocs.presentation.home.HomeScreen
import com.alphaomardiallo.handydocs.presentation.navigation.NavigationEffects
import com.alphaomardiallo.handydocs.presentation.theme.HandyDocsTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModel()

    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.onCameraPermissionGranted()
            } else {
                // Camera permission denied
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            NavigationEffects(
                navigationChannel = viewModel.navigationChannel,
                navHostController = navController
            )

            HandyDocsTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        AppBar(currentRoute = currentRoute)
                    }
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
    private fun AppBar(currentRoute: String? = null) {

        val title = when (currentRoute) {
            AppDestination.CameraPreview.route -> {
                R.string.camera_app_bar_title
            }

            else -> {
                R.string.app_name_formated
            }
        }

        TopAppBar(
            title = { Text(text = stringResource(id = title)) },
            navigationIcon = {
                if (currentRoute == AppDestination.CameraPreview.route) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = Icons.Default.Home.name
                        )
                    }
                }
            },
            actions = {
                if (currentRoute == AppDestination.Home.route) {
                    IconButton(onClick = {
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.CAMERA
                            ) -> {
                                viewModel.onCameraPermissionGranted()
                            }

                            else -> {
                                cameraPermissionRequest.launch(Manifest.permission.CAMERA)
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.AddCircle,
                            contentDescription = Icons.Default.Add.name
                        )
                    }
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

    composable(route = AppDestination.CameraPreview.route) {
        CameraScreen()
    }
}
