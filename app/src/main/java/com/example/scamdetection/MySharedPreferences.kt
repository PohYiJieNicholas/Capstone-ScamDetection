package com.example.scamdetection

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MySharedPreferences(context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveArrayList(key: String, list: ArrayList<String>) {
        val json = gson.toJson(list)
        sharedPreferences.edit().putString(key, json).apply()
    }

    fun getArrayList(key: String): ArrayList<String> {
        val json = sharedPreferences.getString(key, null)
        val type = object : TypeToken<ArrayList<String>>() {}.type
        return gson.fromJson(json, type) ?: ArrayList()
    }
}
