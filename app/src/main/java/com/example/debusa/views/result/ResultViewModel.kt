package com.example.debusa.views.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.debusa.data.local.entity.HistoryAnalyzeEntity
import com.example.debusa.domain.PredictRepository

class ResultViewModel (
   val repository: PredictRepository
):ViewModel(){
    fun saveAnalyze(result: String, deskripsi: String,manfaat:String, imageUri: String){
        val historyAnalyze = HistoryAnalyzeEntity(
            result = result,
            deskripsi = deskripsi,
            manfaat = manfaat,
            image = imageUri
        )
        repository.saveHistoryAnlyze(historyAnalyze)
    }

    fun getAllHistoryAnalyze(): LiveData<List<HistoryAnalyzeEntity>> = repository.getAllHistoryAnalyze()

    fun deleteHistory(){
        repository.deleteHistoryAnalyze()
    }
}