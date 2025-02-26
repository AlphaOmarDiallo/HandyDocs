package com.alphaomardiallo.handydocs.feature.altgenerator.presentation

import com.alphaomardiallo.handydocs.common.domain.navigator.AppNavigator
import com.alphaomardiallo.handydocs.common.presentation.base.BaseViewModel
import com.alphaomardiallo.handydocs.feature.altgenerator.domain.AltGeneratorRepository

class AltGeneratorViewModel(
    appNavigator: AppNavigator,
    private val altGeneratorRepository: AltGeneratorRepository
) : BaseViewModel(appNavigator) {

    suspend fun getString() =
        altGeneratorRepository.imageUrlToBase64("https://img.20mn.fr/e60tGsSWQ-m1jCKm-SU_6Sk/1444x920_psg-s-bradley-barcola-celebrates-after-scoring-his-side-s-second-goal-during-the-champions-league-opening-phase-soccer-match-between-paris-saint-germain-and-manchester-city-at-the-parc-des-princes-in-paris-wednesday-jan-22-2025-ap-photo-michel-euler-th116-25022771653491-2501222230")
}
