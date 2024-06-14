package com.example.debusa.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.debusa.data.local.entity.HistoryAnalyzeEntity

@Dao
interface HistoryAnalyzeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistoryAnalyze(historyAnalyze: HistoryAnalyzeEntity)

    @Query("SELECT * FROM historyanalyze")
    fun getHistoryAnalyze(): LiveData<List<HistoryAnalyzeEntity>>

    @Query("DELETE FROM historyanalyze")
    fun deleteAllHistoryAnalyze()
}