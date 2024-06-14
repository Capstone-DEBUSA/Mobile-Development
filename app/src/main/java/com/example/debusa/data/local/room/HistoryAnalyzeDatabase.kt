package com.example.debusa.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.debusa.data.local.entity.HistoryAnalyzeEntity

@Database(entities = [HistoryAnalyzeEntity::class], version = 1, exportSchema = false)
abstract class HistoryAnalyzeDatabase  : RoomDatabase() {
    abstract fun historyAnaylzeDao():HistoryAnalyzeDao

    companion object {
        @Volatile
        private var instance: HistoryAnalyzeDatabase? = null

        fun getInstance(context: Context):HistoryAnalyzeDatabase =
            instance ?: synchronized(this){
                instance ?: Room.databaseBuilder(
                    context.applicationContext  ,
                    HistoryAnalyzeDatabase::class.java,"HistoryAnalyze.db"
                ).build()
            }
    }
}