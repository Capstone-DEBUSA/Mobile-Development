package com.example.debusa.views.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.debusa.data.remote.response.PredictResponse
import com.example.debusa.domain.PredictRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(
    private val predictRepository: PredictRepository
): ViewModel() {
    private val viewModelJob = Job()
    private val _predictResult = MutableLiveData<PredictResponse>()
    val predictResuult : LiveData<PredictResponse> = _predictResult

    fun uploadImage(img: File){
        viewModelScope.launch {
            val result = predictRepository.uploadImage(img)
            _predictResult.value = result
        }
    }

    fun loading(): LiveData<Boolean> = predictRepository.loadingPost

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}