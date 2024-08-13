package com.alphaomardiallo.handydocs.presentation.home

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.alphaomardiallo.handydocs.R
import com.alphaomardiallo.handydocs.domain.model.ImageDoc
import com.alphaomardiallo.handydocs.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.domain.repository.ImageDocRepository
import com.alphaomardiallo.handydocs.presentation.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(
    appNavigator: AppNavigator,
    private val imageDocRepository: ImageDocRepository
) : BaseViewModel(appNavigator) {

    var state by mutableStateOf(HomeUiState())
        private set

    private var job: Job? = null

    init {
        getAllImageTest()
    }

    fun updateDocumentName(imageDoc: ImageDoc, newName: String) {
        viewModelScope.launch {
            imageDocRepository.upsertImage(
                imageDoc.copy(displayName = newName)
            )
        }
    }

    fun updateDocumentSelected(imageDoc: ImageDoc) {
        viewModelScope.launch {
            imageDocRepository.getAllImages().first().map {
                if (it.id == imageDoc.id) {
                    imageDocRepository.upsertImage(imageDoc.copy(isSelected = true))
                } else {
                    imageDocRepository.upsertImage(it.copy(isSelected = false))
                }
            }
        }
    }

    private fun getAllImages() {
        job = viewModelScope.launch {
            imageDocRepository.getAllImages().collect { imageList ->
                state = state.copy(allImageDoc = imageList)
            }
        }
    }

    private fun getAllImagesNameAsc() {
        job = viewModelScope.launch {
            imageDocRepository.getAllImageNameAsc().collect { imageList ->
                state = state.copy(allImageDoc = imageList)
            }
        }
    }

    fun getAllImageTest(filterType: ListFilter = ListFilter.None) {
        job?.cancel()

        viewModelScope.launch {
            when (filterType){
                is ListFilter.NameAsc -> getAllImagesNameAsc()
                is ListFilter.NameDesc -> getAllImagesNameAsc()
                is ListFilter.TimeAsc -> getAllImages()
                is ListFilter.TimeDesc -> getAllImages()
                is ListFilter.None -> getAllImages()
            }
        }
    }

    data class HomeUiState(
        val allImageDoc: List<ImageDoc> = emptyList()
    )

    sealed class ListFilter(
        @StringRes val label: Int = R.string.home_filter_option_name,
        val type: ListFilterType = ListFilterType.NONE
    ) {
        data object NameAsc : ListFilter(
            label = R.string.home_filter_option_name,
            type = ListFilterType.NAME_ASC
        )

        data object NameDesc : ListFilter(
            label = R.string.home_filter_option_name_desc,
            type = ListFilterType.NAME_DESC
        )

        data object TimeAsc : ListFilter(
            label = R.string.home_filter_option_date,
            type = ListFilterType.TIME_ASC
        )

        data object TimeDesc : ListFilter(
            label = R.string.home_filter_option_date_desc,
            type = ListFilterType.TIME_DESC
        )

        data object None : ListFilter()
    }

    enum class ListFilterType() {
        NAME_ASC,
        NAME_DESC,
        TIME_ASC,
        TIME_DESC,
        NONE
    }
}
