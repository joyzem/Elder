package com.example.elder.data.students

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.descriptors.PrimitiveKind

@Dao
interface StudentDao {

    @Query("SELECT * FROM students_table ORDER BY surname ASC")
    fun getAlphabetizedStudents(): Flow<MutableList<Student>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("DELETE FROM students_table")
    suspend fun cleanTable()
}