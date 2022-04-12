package com.example.elder.ui.screens.report

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.elder.data.students.Student

data class ReportStudentUiState(
    val student: Student,
    var checked: Boolean = false,
    var reasonOfMissing: MutableState<String> = mutableStateOf("")
)