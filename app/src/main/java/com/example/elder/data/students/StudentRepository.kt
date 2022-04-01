package com.example.elder.data.students

import kotlinx.coroutines.flow.Flow

class StudentRepository(private val studentDao: StudentDao) {

    fun fetchAllStudents(): Flow<MutableList<Student>> = studentDao.getAlphabetizedStudents()

    suspend fun insert(studentSurname: String) {
        val sEntity = Student(surname = studentSurname)
        studentDao.insert(sEntity)
    }

    suspend fun delete(student: Student) {
        studentDao.deleteStudent(student = student)
    }
}