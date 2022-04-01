package com.example.elder.data.students

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Student::class], version = 1, exportSchema = false)
abstract class StudentRoomDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    companion object {
        // Singleton
        @Volatile
        private var INSTANCE: StudentRoomDatabase? = null

        fun getDatabase(context: Context): StudentRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudentRoomDatabase::class.java,
                    "student_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}