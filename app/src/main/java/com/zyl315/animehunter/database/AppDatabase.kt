package com.zyl315.animehunter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zyl315.animehunter.App
import com.zyl315.animehunter.database.dao.WatchHistoryDao
import com.zyl315.animehunter.database.enity.WatchHistory


@Database(
    entities = [WatchHistory::class], version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun watchHistoryDao(): WatchHistoryDao

    companion object {
        private var instance: AppDatabase? = null


        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "data.db"
                        ).build()
                    }
                }
            }
            return instance as AppDatabase
        }
    }
}

fun getAppDataBase() = AppDatabase.getInstance(App.context)