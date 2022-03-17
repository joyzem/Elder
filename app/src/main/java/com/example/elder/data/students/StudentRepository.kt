package com.example.elder.data.students

import kotlinx.coroutines.flow.Flow

class StudentRepository(private val studentDao: StudentDao) {

    fun fetchAllStudents(): Flow<MutableList<StudentEntity>> = studentDao.getAlphabetizedStudents()

    suspend fun insert(studentSurname: String) {
        val sEntity = StudentEntity(surname = studentSurname)
        studentDao.insert(sEntity)
    }

    suspend fun delete(studentId: Int) {
        studentDao.deleteStudentById(studentId)
    }
}