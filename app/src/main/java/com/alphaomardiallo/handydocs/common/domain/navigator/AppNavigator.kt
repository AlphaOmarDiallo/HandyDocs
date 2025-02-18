package com.alphaomardiallo.handydocs.common.domain.navigator

import kotlinx.coroutines.channels.Channel

interface AppNavigator {

    val navigationChannel: Channel<NavigationIntent>

    suspend fun navigateBack(
        route: String? = null,
        inclusive: Boolean = false
    )

    suspend fun navigateTo(
        route: String,
        popUpToRoute: String? = null,
        inclusive: Boolean = false,
        isSingleTop: Boolean = false
    )
}
