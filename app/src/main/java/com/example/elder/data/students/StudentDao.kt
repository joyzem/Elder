package com.example.elder.data.students

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Query("SELECT * FROM students_table ORDER BY surname ASC")
    fun getAlphabetizedStudents(): Flow<MutableList<Student>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)
}