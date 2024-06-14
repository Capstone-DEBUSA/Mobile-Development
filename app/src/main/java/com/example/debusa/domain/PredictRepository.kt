package com.example.debusa.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.debusa.data.local.entity.HistoryAnalyzeEntity
import com.example.debusa.data.local.room.HistoryAnalyzeDatabase
import com.example.debusa.data.remote.response.PredictResponse
import com.example.debusa.data.remote.retrofit.ApiService
import com.example.debusa.utils.AppExecutors
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class PredictRepository(
    private val api: ApiService,
    private val db: HistoryAnalyzeDatabase,
    private val appExecutors: AppExecutors
) {
    private val _loadingPost = MutableLiveData<Boolean>()
    val loadingPost: LiveData<Boolean> = _loadingPost
    suspend fun uploadImage(img: File):PredictResponse{
        _loadingPost.value = true
        try {
            val requestImageFile = img.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "image",
                img.name,
                requestImageFile
            )
            val succesResponse = api.uploadImage(multipartBody)
            _loadingPost.value =false
            return succesResponse
        }catch (e: HttpException){
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, PredictResponse::class.java)
            if (errorBody != null && errorBody.startsWith("{")) {
                // Respons berupa objek JSON, lanjutkan deserialisasi
                return PredictResponse(status = "error", message = errorResponse.message ?: "Unknown error")
            } else {
                // Respons bukan objek JSON, tampilkan pesan kesalahan langsung
                return PredictResponse(status = "error", message = "Coba Lagi")
            }
            _loadingPost.value = false
            return PredictResponse(status = "error", message = errorResponse.message ?: "Coba Lagi")
        }catch (e: IOException){
            _loadingPost.value = false
            return  PredictResponse(status = "error", message = "Conection Error")
        }catch (e: JsonSyntaxException) {
            _loadingPost.value = false
            return PredictResponse(status = "error", message = "JSON Syntax Error")
        } catch (e: Exception) {
            _loadingPost.value = false
            return PredictResponse(status = "error", message = "Unknown Error")
        }
    }



    fun saveHistoryAnlyze(historyAnalyzeEntity: HistoryAnalyzeEntity){
        appExecutors.diskIO.execute{
            db.historyAnaylzeDao().insertHistoryAnalyze(historyAnalyzeEntity)
        }
    }

    fun getAllHistoryAnalyze(): LiveData<List<HistoryAnalyzeEntity>> {
        return db.historyAnaylzeDao().getHistoryAnalyze()
    }

    fun deleteHistoryAnalyze(){
        appExecutors.diskIO.execute{
            db.historyAnaylzeDao().deleteAllHistoryAnalyze()
        }
    }






    companion object {
        @Volatile
        private var instance: PredictRepository? = null
        fun getInstance(
            api: ApiService,
            db: HistoryAnalyzeDatabase,
            appExecutors: AppExecutors
        ): PredictRepository =
            instance ?: synchronized(this) {
                instance ?: PredictRepository(api,db,appExecutors)
            }.also { instance = it }
    }
}