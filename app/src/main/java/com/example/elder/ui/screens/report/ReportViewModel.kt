package com.example.elder.ui.screens.report

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.example.elder.data.students.StudentRepository
import com.example.elder.domain.GroupReport
import com.example.elder.domain.Lesson
import com.example.elder.domain.getCurrentLesson
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.text.DateFormat
import java.util.*

class ReportViewModel(private val repository: StudentRepository) : ViewModel() {

    var groupName = "ПИ2002"
    var students: MutableList<StudentUiState> by mutableStateOf(
        mutableStateListOf()
    )
        private set
    var selectMode: SelectMode by mutableStateOf(SelectMode.AttendingStudents)
        private set
    var date: Calendar by mutableStateOf(Calendar.getInstance())
        private set
    var lesson by mutableStateOf(getCurrentLesson())
        private set
    var reportHeader by mutableStateOf(
        if (selectMode == SelectMode.AttendingStudents) "Присутствующие" else "Отсутствующие"
    )
        private set

    init {
        viewModelScope.launch {
            repository.fetchAllStudents().collect {
                students = it.map { StudentUiState(it.surname) }.toMutableStateList()
            }
        }
    }

    fun onStudentChecked(studentUiState: StudentUiState) {
        val index = students.indexOf(studentUiState)
        students[index] = students[index].copy(checked = !studentUiState.checked)
    }

    fun onDateChanged(newDate: Calendar) {
        date = newDate

    }

    fun onLessonChanged(newLesson: Lesson) {
        lesson = newLesson
    }

    fun onSelectModeChanged(newMode: SelectMode) {
        selectMode = newMode
        updateReportHeader()
    }

    fun getReport(): GroupReport {
        val requiredStudents = students.filter { student -> student.checked }
        return createReport(requiredStudents, "$reportHeader:")
    }

    fun checkAllStudents(checked: Boolean) {
        viewModelScope.launch {
            for (i in 0..students.size - 1) {
                students[i] = students[i].copy(checked = checked)
            }
        }
    }

    private fun createReport(group: List<StudentUiState>, prefix: String): GroupReport {
        val groupReport = GroupReport(
            subject = "$groupName, ${DateFormat.getDateInstance().format(date.time)}, ${lesson.value}",
            content = group.joinToString(
                separator = "\n",
                prefix = "${prefix}\n",
                transform = { student ->
                    student.name
                })
        )
        return groupReport
    }

    private fun updateReportHeader() {
        reportHeader =
            if (selectMode == SelectMode.AttendingStudents) "Присутствующие" else "Отсутствующие"
    }
}

enum class SelectMode {
    AttendingStudents,
    MissingStudents
}

class ReportViewModelFactory(private val repository: StudentRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}