package com.example.elder

import android.app.Application
import com.example.elder.data.students.StudentRepository
import com.example.elder.data.students.StudentRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ElderApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { StudentRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { StudentRepository(database.studentDao()) }
}

const val GROUP_NAME = "groupName"