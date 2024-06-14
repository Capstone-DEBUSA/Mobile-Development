package com.example.debusa.di

import android.content.Context
import com.example.debusa.data.local.room.HistoryAnalyzeDatabase
import com.example.debusa.data.remote.retrofit.ApiConfig
import com.example.debusa.domain.PredictRepository
import com.example.debusa.utils.AppExecutors
import kotlinx.coroutines.runBlocking

object Injection {
    fun providePredictRepository(context: Context): PredictRepository {
//        val db = StoryDatabase.getDatabase(context)
        val api = ApiConfig.getApiService()
        val db = HistoryAnalyzeDatabase.getInstance(context)
        val appExecutors = AppExecutors()
        return PredictRepository.getInstance(api, db, appExecutors)
    }
}