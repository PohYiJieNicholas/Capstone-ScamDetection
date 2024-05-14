package com.example.scamdetection.apiCall

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("api/data")
    fun getUserData():  Call<PredictionData>
}