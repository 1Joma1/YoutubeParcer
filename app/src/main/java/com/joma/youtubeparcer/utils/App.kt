package com.joma.youtubeparcer.utils

import android.app.Application
import androidx.room.Room
import com.joma.youtubeparcer.db.AppDatabase

class App : Application() {

    companion object {
        lateinit var database: AppDatabase
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(this, AppDatabase::class.java, "database").fallbackToDestructiveMigration()
            .build()
    }

    fun getInstance() : App {
        return instance
    }

    fun getDatabase() : AppDatabase {
        return database
    }
}