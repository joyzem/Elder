package com.example.elder.ui.screens.manage

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.elder.GROUP_NAME
import com.example.elder.data.students.Student
import com.example.elder.data.students.StudentRepository
import com.example.elder.domain.convertFullNameListToSurnameList
import com.example.elder.ui.screens.parsing.StudentsParsingStatus
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.request.*
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

    var parsingStatus: StudentsParsingStatus by mutableStateOf(StudentsParsingStatus.Waiting)
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

    fun parseGroupByInternet(groupName: String) {
        viewModelScope.launch {
            parsingStatus = StudentsParsingStatus.Loading
            try {
                val client = HttpClient()
                val data =
                    client.get<String>("http://students-kubsau.herokuapp.com/students?group=$groupName")
                val jsonResult = Gson().fromJson(data, GetStudentsResult::class.java)
                if (jsonResult.success) {
                    parsingStatus =
                        StudentsParsingStatus.Success(convertFullNameListToSurnameList(jsonResult.result))
                } else {
                    parsingStatus = StudentsParsingStatus.Error(jsonResult.err)
                }
            } catch (e: Exception) {
                parsingStatus = StudentsParsingStatus.Error(mapOf("error" to e.toString()))
            }
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            studentRepository.delete(student)
        }
    }

    fun setNewParsingStatus(status: StudentsParsingStatus) {
        parsingStatus = status
    }

    fun insertGroup(students: List<String>) {
        viewModelScope.launch {
            studentRepository.deleteAllStudents()
            students.forEach { student ->
                studentRepository.insert(student)
            }
        }
    }

    private data class GetStudentsResult(
        val success: Boolean, val result: List<String>, val err: Map<String, String>
    )
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