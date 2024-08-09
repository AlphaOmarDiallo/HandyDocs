package com.alphaomardiallo.handydocs.domain.destination

import androidx.annotation.StringRes
import com.alphaomardiallo.handydocs.R

sealed class AppDestination(
    @StringRes val resId: Int = 0,
) : Destination(resId.toString()) {

    data object Home : AppDestination(resId = R.string.camera_destination)

    data object Camera : AppDestination(resId = R.string.camera_destination)
}
