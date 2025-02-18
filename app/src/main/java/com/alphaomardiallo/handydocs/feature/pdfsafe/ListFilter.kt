package com.alphaomardiallo.handydocs.feature.pdfsafe

import androidx.annotation.StringRes
import com.alphaomardiallo.handydocs.R

sealed class ListFilter(
    @StringRes val label: Int = R.string.pdf_safe_filter_option_name,
) {
    data object NameAsc : ListFilter(label = R.string.pdf_safe_filter_option_name)

    data object NameDesc : ListFilter(label = R.string.pdf_safe_filter_option_name_desc)

    data object TimeAsc : ListFilter(label = R.string.pdf_safe_filter_option_date)

    data object TimeDesc : ListFilter(label = R.string.pdf_safe_filter_option_date_desc)

    data object Favorite : ListFilter(label = R.string.pdf_safe_filter_option_fav)

    data object None : ListFilter()
}
