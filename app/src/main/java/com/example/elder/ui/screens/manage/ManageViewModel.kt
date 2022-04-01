package com.example.elder.ui.screens.manage

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.elder.GROUP_NAME
import com.example.elder.data.students.Student
import com.example.elder.data.students.StudentRepository
import com.example.elder.ui.screens.report.ReportViewModel
import com.example.elder.ui.screens.report.ReportStudentUiState
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class ManageViewModel(
    private val studentRepository: StudentRepository,
    groupName: String?
) : ViewModel() {

    var students: MutableList<Student> by mutableStateOf(
        mutableStateListOf()
    )
        private set

    var groupName by mutableStateOf(groupName)
        private set

    init {
        viewModelScope.launch {
            studentRepository.fetchAllStudents().collect { incomingStudents ->
                students = incomingStudents.toMutableStateList()
            }
        }
    }

    fun saveGroupName(groupName: String, context: Context) {
        context.openFileOutput(GROUP_NAME, Context.MODE_PRIVATE).use {
            it.write(groupName.toByteArray())
        }
        this.groupName = groupName
    }

    fun insertStudent(surname: String) {
        viewModelScope.launch {
            studentRepository.insert(surname)
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            studentRepository.delete(student)
        }
    }

}

class ManageViewModelFactory(
    private val studentRepository: StudentRepository,
    private val groupName: String?
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ManageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ManageViewModel(studentRepository, groupName = groupName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}