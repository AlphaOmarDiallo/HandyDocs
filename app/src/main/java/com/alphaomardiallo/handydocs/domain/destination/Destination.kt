package com.alphaomardiallo.handydocs.domain.destination

import androidx.navigation.NamedNavArgument

open class Destination(
    val route: String,
    val args: List<NamedNavArgument> = emptyList(),
)
