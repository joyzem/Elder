package com.example.elder.ui.screens.report

import com.example.elder.data.students.Student

data class ReportStudentUiState(
    val student: Student,
    var checked: Boolean = false
)