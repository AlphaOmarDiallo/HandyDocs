package com.alphaomardiallo.handydocs.feature.altgenerator.presentation.model

enum class Language(val displayName: String, val code: String) {
    ENGLISH("English", "en"),
    SPANISH("Spanish", "es"),
    FRENCH("French", "fr"),
    GERMAN("German", "de"),
    CHINESE_SIMPLIFIED("Chinese (Simplified)", "zh-CN"),
    CHINESE_TRADITIONAL("Chinese (Traditional)", "zh-TW"),
    JAPANESE("Japanese", "ja"),
    KOREAN("Korean", "ko"),
    PORTUGUESE("Portuguese", "pt"),
    ITALIAN("Italian", "it"),
    RUSSIAN("Russian", "ru"),
    DUTCH("Dutch", "nl");

    companion object {
        fun fromCode(code: String): Language? {
            return entries.find { it.code == code }
        }

        fun listOfLanguages(): List<Language> {
            return entries.toList()
        }
    }
}
