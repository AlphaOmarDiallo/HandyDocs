package com.alphaomardiallo.handydocs.common.domain.destination

import androidx.annotation.StringRes
import com.alphaomardiallo.handydocs.R

sealed class AppDestination(
    @StringRes val resId: Int = 0,
) : Destination(resId.toString()) {

    data object PDFSAFE : AppDestination(resId = R.string.pdf_safe_destination)
    data object OCR : AppDestination(resId = R.string.ocr_destination)
    data object ALTGEN : AppDestination(resId = R.string.alt_gen_destination)
}
