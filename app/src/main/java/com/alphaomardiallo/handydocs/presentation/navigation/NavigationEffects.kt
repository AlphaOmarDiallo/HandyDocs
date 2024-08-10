package com.alphaomardiallo.handydocs.presentation.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.alphaomardiallo.handydocs.domain.navigator.NavigationIntent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun NavigationEffects(
    navigationChannel: Channel<NavigationIntent>,
    navHostController: NavHostController,
) {

    val activity = (LocalContext.current as? Activity)

    LaunchedEffect(key1 = activity, key2 = navHostController, key3 = navigationChannel ) {
        navigationChannel.receiveAsFlow().collect { intent ->

            if (activity?.isFinishing == true){
                return@collect
            }

            when (intent) {
                is NavigationIntent.NavigateBack -> {
                    val route = intent.route
                    if (route != null) {
                        navHostController.popBackStack(route = route, inclusive = intent.inclusive)
                    } else {
                        navHostController.popBackStack()
                    }
                }

                is NavigationIntent.NavigateTo -> {
                    navHostController.navigate(route = intent.route) {
                        intent.popUpToRoute?.let { popUpToRoute ->
                            popUpTo(popUpToRoute) { inclusive = intent.inclusive}
                        }
                    }
                }

            }
        }
    }
}
