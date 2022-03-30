package com.example.elder.screens.report

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

    var group: MutableList<StudentUiState> by mutableStateOf(
        mutableStateListOf()
    )
        private set

    init {
        viewModelScope.launch {
            repository.fetchAllStudents().collect {
                group = it.map { StudentUiState(it.surname) }.toMutableStateList()
            }
        }
    }

    var date: Calendar by mutableStateOf(Calendar.getInstance())
        private set
    var lesson by mutableStateOf(getCurrentLesson())
        private set

    fun onStudentChecked(studentUiState: StudentUiState) {
        val index = group.indexOf(studentUiState)
        group[index] = group[index].copy(checked = !studentUiState.checked)
    }

    fun onDateChanged(newDate: Calendar) {
        date = newDate
    }

    fun onLessonChanged(newLesson: Lesson) {
        lesson = newLesson
    }

    fun onAttendingStudentsRequest(): GroupReport {
        val attendingStudents = group.filter { student -> student.checked }
        return createReport(attendingStudents, "Присутствующие:")
    }

    fun onMissingStudentsRequest(): GroupReport {
        val missingStudents = group.filter { student -> !student.checked }
        return createReport(missingStudents, "Отсутствующие:")
    }

    fun checkAllStudents(checked: Boolean) {
        viewModelScope.launch {
            for (i in 0..group.size - 1) {
                group[i] = group[i].copy(checked = checked)
            }
        }
    }

    private fun createReport(group: List<StudentUiState>, prefix: String): GroupReport {
        val groupReport = GroupReport(
            subject = "ПИ2002, ${DateFormat.getDateInstance().format(date.time)}, ${lesson.value}",
            content = group.joinToString(
                separator = "\n",
                prefix = "${prefix}\n",
                transform = { student ->
                    student.name
                })
        )
        return groupReport
    }
}

class CreateReportViewModelFactory(private val repository: StudentRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}