package com.example.elder.ui.screens.parsing

sealed class StudentsParsingStatus {

    data class Success(
        val result: List<String>
    ) : StudentsParsingStatus()

    data class Error(
        val error: Map<String, String>
    ) : StudentsParsingStatus()

    object Loading : StudentsParsingStatus()

    object Waiting : StudentsParsingStatus()
}
