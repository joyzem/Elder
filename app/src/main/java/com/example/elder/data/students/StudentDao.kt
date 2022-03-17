package com.example.elder.data.students

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Query("SELECT * FROM students_table ORDER BY surname ASC")
    fun getAlphabetizedStudents(): Flow<MutableList<StudentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: StudentEntity)

    @Query("DELETE FROM students_table WHERE id == :studentId")
    suspend fun deleteStudentById(studentId: Int)
}