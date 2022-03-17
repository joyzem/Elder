package com.example.elder

import android.app.Application
import com.example.elder.data.students.StudentRepository
import com.example.elder.data.students.StudentRoomDatabase

class ElderApplication : Application() {
    private val database by lazy { StudentRoomDatabase.getDatabase(this) }
    val repository by lazy { StudentRepository(database.studentDao()) }
}

const val GROUP_NAME = "groupName"