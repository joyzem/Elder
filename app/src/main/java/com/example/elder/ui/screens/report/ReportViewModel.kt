package com.example.elder.ui.screens.report

import android.app.Application
import androidx.compose.runtime.*
import androidx.compose.ui.state.ToggleableState
import androidx.lifecycle.*
import com.example.elder.ElderApplication
import com.example.elder.GROUP_NAME
import com.example.elder.data.students.StudentRepository
import com.example.elder.domain.FixedSumTwoValuesCounter
import com.example.elder.domain.GroupReport
import com.example.elder.domain.Lesson
import com.example.elder.domain.getCurrentLesson
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.text.DateFormat
import java.util.*

class ReportViewModel(application: Application, private val repository: StudentRepository) :
    AndroidViewModel(application) {

    var students: MutableList<ReportStudentUiState> by mutableStateOf(mutableStateListOf())
        private set
    private var selectMode: SelectMode by mutableStateOf(SelectMode.AttendingStudents)
    var date: Calendar by mutableStateOf(Calendar.getInstance())
        private set
    var lesson: Lesson by mutableStateOf(getCurrentLesson())
        private set
    private var studentsCounter: FixedSumTwoValuesCounter? = null
    var toggleableState: ToggleableState by mutableStateOf(ToggleableState.Off)
        private set

    init {
        viewModelScope.launch {
            repository.fetchAllStudents().collect { incomingStudents ->
                students = incomingStudents.map { studentEntity ->
                    ReportStudentUiState(studentEntity)
                }.toMutableStateList()
                studentsCounter = FixedSumTwoValuesCounter(incomingStudents.size)
                studentsCounter!!.setValuesByFirstValue(getAttendingStudentsAmount())
                toggleableState = studentsCounter!!.getToggleableState()
            }
        }
    }

    fun onStudentChecked(reportStudentUiState: ReportStudentUiState) {
        val index = students.indexOf(reportStudentUiState)
        students[index] = students[index].copy(
            checked = !reportStudentUiState.checked,
            hasReason = mutableStateOf(false),
            reasonOfMissing = mutableStateOf("")
        )
        studentsCounter?.increaseFirstOrSecondValue(isFirst = students[index].checked)
        toggleableState = studentsCounter?.getToggleableState() ?: ToggleableState.On
    }

    fun onDateChanged(newDate: Calendar) {
        date = newDate
    }

    fun onLessonChanged(newLesson: Lesson) {
        lesson = newLesson
    }

    fun onSelectModeChanged(newMode: SelectMode) {
        selectMode = newMode
    }

    fun getReport(): GroupReport {
        val attemptingPart = students.filter { reportStudentUiState ->
            reportStudentUiState.checked
        }.joinToString(
            separator = "\n",
            prefix = "????????????????????????????:\n",
            transform = { reportStudentUiState ->
                reportStudentUiState.student.surname
            }
        )
        val missingPart = students.filter { reportStudentUiState ->
            !reportStudentUiState.checked
        }.joinToString(
            separator = "\n",
            prefix = "??????????????????????????:\n",
            transform = { reportStudentUiState ->
                var reason = ""
                reportStudentUiState.run {
                    if (hasReason.value && reasonOfMissing.value.isNotEmpty()) {
                        reason = " (${reportStudentUiState.reasonOfMissing.value})"
                    }
                }
                return@joinToString "${reportStudentUiState.student.surname}$reason"
            }
        )

        val content = when (selectMode) {
            SelectMode.AttendingStudents -> attemptingPart
            SelectMode.MissingStudents -> missingPart
            SelectMode.Both -> attemptingPart + "\n\n" + missingPart
        }
        return GroupReport(
            subject = "${getGroupName() ?: "????????????: "}, ${
                DateFormat.getDateInstance().format(date.time)
            }, ${lesson.value}",
            content = content
        )
    }

    fun onToggleableStateClicked() {
        when (toggleableState) {
            ToggleableState.On -> checkAllStudents(false)
            ToggleableState.Indeterminate -> checkAllStudents(true)
            ToggleableState.Off -> checkAllStudents(true)
        }
    }

    private fun checkAllStudents(checked: Boolean) {
        viewModelScope.launch {
            for (i in 0 until students.size) {
                students[i] = students[i].copy(
                    checked = checked,
                    hasReason = mutableStateOf(false),
                    reasonOfMissing = mutableStateOf("")
                )
            }
        }
        studentsCounter!!.setValuesByFirstValue(if (checked) students.size else 0)
        toggleableState = studentsCounter!!.getToggleableState()
    }

    private fun getAttendingStudentsAmount(): Int {
        var counter = 0
        students.forEach { student -> if (student.checked) counter++ }
        return counter
    }

    private fun getGroupName(): String? {
        return try {
            getApplication<ElderApplication>().applicationContext.openFileInput(GROUP_NAME)
                .bufferedReader().readLine()
        } catch (e: Exception) {
            null
        }
    }
}

enum class SelectMode {
    AttendingStudents,
    MissingStudents,
    Both
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