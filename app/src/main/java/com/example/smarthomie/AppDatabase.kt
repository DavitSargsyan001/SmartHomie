package com.example.smarthomie

import android.content.Context
import androidx.room.Room
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DeviceDetails::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDetailsDao(): DeviceDetailsDao
}

// Inside the AppDatabase.kt or another suitable file
object DatabaseBuilder {
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        if (INSTANCE == null) {
            synchronized(AppDatabase::class) {
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                    AppDatabase::class.java, "your_database_name.db")
                    .fallbackToDestructiveMigration() // Handle migrations
                    .build()
            }
        }
        return INSTANCE!!
    }
}
