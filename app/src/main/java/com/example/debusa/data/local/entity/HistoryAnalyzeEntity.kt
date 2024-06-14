package com.example.debusa.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historyanalyze")
data class HistoryAnalyzeEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long? = null,

    @ColumnInfo(name = "result")
    var result: String = "",

    @ColumnInfo(name = "deskripsi")
    var deskripsi: String = "",

    @ColumnInfo(name = "manfaat")
    var manfaat: String = "",

    @ColumnInfo(name = "image")
    var image: String = ""
)