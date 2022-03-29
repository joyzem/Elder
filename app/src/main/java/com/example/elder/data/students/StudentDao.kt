package com.example.elder.data.students

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Query("SELECT * FROM students_table ORDER BY surname ASC")
    fun getAlphabetizedStudents(): Flow<MutableList<Student>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: Student)

    @Query("DELETE FROM students_table WHERE id == :studentId")
    suspend fun deleteStudentById(studentId: Int)
}