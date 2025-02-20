package com.alphaomardiallo.handydocs.feature.ocr.domain

import com.alphaomardiallo.handydocs.R

enum class TextRecognitionType(val label: Int) {
    LATIN(R.string.text_recognition_type_latin),
    CHINESE(R.string.text_recognition_type_chinese),
    DEVANAGARI(R.string.text_recognition_type_devanagari),
    JAPANESE(R.string.text_recognition_type_japanese),
    KOREAN(R.string.text_recognition_type_korean)
}
