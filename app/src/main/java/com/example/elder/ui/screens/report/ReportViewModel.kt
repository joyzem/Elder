package com.example.elder.ui.screens.report

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.example.elder.ElderApplication
import com.example.elder.GROUP_NAME
import com.example.elder.data.students.StudentRepository
import com.example.elder.domain.GroupReport
import com.example.elder.domain.Lesson
import com.example.elder.domain.getCurrentLesson
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.text.DateFormat
import java.util.*

class ReportViewModel(application: Application, private val repository: StudentRepository) :
    AndroidViewModel(application) {

    var students: MutableList<ReportStudentUiState> by mutableStateOf(
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
            repository.fetchAllStudents().collect { incomingStudents ->
                students = incomingStudents.map { studentEntity ->
                    ReportStudentUiState(studentEntity)
                }.toMutableStateList()
            }
        }
    }

    fun onStudentChecked(reportStudentUiState: ReportStudentUiState) {
        val index = students.indexOf(reportStudentUiState)
        students[index] = students[index].copy(checked = !reportStudentUiState.checked)
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
            for (i in 0 until students.size) {
                students[i] = students[i].copy(checked = checked)
            }
        }
    }

    private fun getGroupName(): String? {
        return try {
            getApplication<ElderApplication>().applicationContext.openFileInput(GROUP_NAME)
                .bufferedReader().readLine()
        } catch (e: Exception) {
            null
        }
    }

    private fun createReport(group: List<ReportStudentUiState>, prefix: String): GroupReport {
        val groupReport = GroupReport(
            subject = "${getGroupName() ?: "Группа: "}, ${
                DateFormat.getDateInstance().format(date.time)
            }, ${lesson.value}",
            content = group.joinToString(
                separator = "\n",
                prefix = "${prefix}\n",
                transform = { studentUiState ->
                    studentUiState.student.surname
                }
            )
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

class ReportViewModelFactory(
    private val application: ElderApplication,
    private val studentRepository: StudentRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportViewModel(application = application, studentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}