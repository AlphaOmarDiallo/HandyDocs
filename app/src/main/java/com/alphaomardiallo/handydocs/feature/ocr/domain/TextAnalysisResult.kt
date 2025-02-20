package com.alphaomardiallo.handydocs.feature.ocr.domain

import java.io.IOException

sealed class TextAnalysisResult {
    data class Success(val text: String) : TextAnalysisResult()
    sealed class Error : TextAnalysisResult() {
        data class FileError(val exception: IOException) : Error()
        data class RecognitionError(val exception: Exception) : Error()
        data object InvalidImage : Error()
    }
}
