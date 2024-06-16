package com.example.scamdetection.apiCall

import android.gesture.Prediction
import android.renderscript.ScriptGroup.Input
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
//    @GET("api/data")
//    fun getUserData():  Call<PredictionData>

    @POST("api/data")
    fun getPrediction(@Body inputData:InputData): Call<ResponseData>
}

data class ResponseData(val message: String, val output: String, val timeTaken:String)
