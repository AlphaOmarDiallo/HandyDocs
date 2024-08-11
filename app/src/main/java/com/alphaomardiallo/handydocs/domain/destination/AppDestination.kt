package com.alphaomardiallo.handydocs.domain.destination

import androidx.annotation.StringRes
import com.alphaomardiallo.handydocs.R

sealed class AppDestination(
    @StringRes val resId: Int = 0,
) : Destination(resId.toString()) {

    data object Home : AppDestination(resId = R.string.home_destination)

    data object ScannerLauncher: AppDestination(resId = R.string.scanner_destination)

    data object PdfViewer: AppDestination(resId = R.string.pdf_viewer_destination)
}
