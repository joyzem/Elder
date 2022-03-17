package com.example.elder

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elder.ui.data.Repository
import com.example.elder.ui.model.GroupReport
import com.example.elder.ui.model.Lesson
import com.example.elder.ui.model.StudentUiState
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

class StudentsViewModel : ViewModel() {

    var group = Repository.getGroup("GROUP2").toMutableStateList()
        private set

    var date: Calendar by mutableStateOf(Calendar.getInstance())
        private set

    var lesson by mutableStateOf(Lesson.FIRST)
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