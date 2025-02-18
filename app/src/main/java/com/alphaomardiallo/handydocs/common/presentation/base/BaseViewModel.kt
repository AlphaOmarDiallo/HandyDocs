package com.alphaomardiallo.handydocs.common.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alphaomardiallo.handydocs.common.domain.navigator.AppNavigator
import kotlinx.coroutines.launch

abstract class BaseViewModel(private val appNavigator: AppNavigator) : ViewModel() {

    ///////////////////////////////////////////////////////////////////////////
    // Navigation
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Initiates a navigation back action in the application.
     * This function is typically called to navigate the user back to the previous screen or destination.
     * It launches a coroutine within the ViewModel's scope to execute the navigation action asynchronously.
     */
    fun navigateBack() {
        viewModelScope.launch {
            appNavigator.navigateBack()
        }
    }

    /**
     * Initiates a navigation action to a specified route in the application.
     * This function is typically called to navigate the user to a specific screen or destination.
     * It launches a coroutine within the ViewModel's scope to execute the navigation action asynchronously.
     * @param route The route to which the navigation should be performed.
     * @param popUpToRoute The route to which the navigation back stack should be popped up to before navigating to the new route.
     *                      If null, no route will be popped from the back stack before navigating to the new route.
     * @param inclusive Determines whether the specified popUpToRoute should also be popped from the back stack.
     *                  If true, the specified popUpToRoute and all routes above it in the back stack will be popped.
     *                  If false, only the specified popUpToRoute will be popped.
     *                  Defaults to false.
     * @param isSingleTop Specifies whether the destination should be treated as a single top-level destination.
     *                    If true, and the destination already exists in the back stack, it will not be added again.
     *                    If false, the destination will always be added to the back stack.
     */
    fun navigateTo(
        route: String,
        popUpToRoute: String? = null,
        inclusive: Boolean = false,
        isSingleTop: Boolean = false,
    ) {
        viewModelScope.launch {
            appNavigator.navigateTo(
                route,
                popUpToRoute,
                inclusive,
                isSingleTop
            )
        }
    }
}
