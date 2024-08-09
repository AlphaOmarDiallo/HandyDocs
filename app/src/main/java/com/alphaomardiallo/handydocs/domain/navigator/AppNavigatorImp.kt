package com.alphaomardiallo.handydocs.domain.navigator

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel

class AppNavigatorImp : AppNavigator {

    /**
     * Channel for sending and receiving navigation intents.
     * This channel is used to communicate navigation events between different parts of the application.
     * It has a large capacity to ensure that navigation intents are not lost due to buffer overflow,
     * and it drops the latest intent when the buffer is full to prioritize newer intents.
     */
    override val navigationChannel: Channel<NavigationIntent> = Channel(
        capacity = Int.MAX_VALUE,
        onBufferOverflow = BufferOverflow.DROP_LATEST
    )

    /**
     * Navigates back in the application's navigation flow.
     * @param route The route to which the navigation should be performed.
     *              If null, the default behavior of navigating back to the previous screen is applied.
     * @param inclusive Determines whether the specified route should also be popped from the back stack.
     *                  If true, the specified route and all routes above it in the back stack will be popped.
     *                  If false, only the specified route will be popped.
     *                  Defaults to false.
     */
    override suspend fun navigateBack(route: String?, inclusive: Boolean) {
        navigationChannel.send(
            NavigationIntent.NavigateBack(
                route = route,
                inclusive = inclusive
            )
        )
    }

    /**
     * Navigates to a specified route in the application's navigation flow.
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
    override suspend fun navigateTo(
        route: String,
        popUpToRoute: String?,
        inclusive: Boolean,
        isSingleTop: Boolean,
    ) {
        navigationChannel.send(
            NavigationIntent.NavigateTo(
                route = route,
                popUpToRoute = popUpToRoute,
                inclusive = inclusive,
                isSingleTop = isSingleTop
            )
        )
    }
}
