package com.example.debusa.data.remote.retrofit

import com.example.debusa.data.remote.response.PredictResponse
import com.example.debusa.data.remote.response.SubmitResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("predict")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): PredictResponse

    @Multipart
    @POST("submit")
    suspend fun sendQuestion(
        @Part("question") question:RequestBody,
        @Part("option") option: RequestBody
    ): SubmitResponse

}