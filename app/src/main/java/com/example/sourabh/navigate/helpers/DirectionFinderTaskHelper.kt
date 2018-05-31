package com.example.sourabh.navigate.helpers

import com.example.sourabh.navigate.services.DirectionFinderAsyncTask
import okhttp3.OkHttpClient
import okhttp3.Request

fun DirectionFinderAsyncTask.getResponse(query: String): String {
    val okHttpClient = OkHttpClient()
    val request = Request.Builder().url(query).build()
    with(okHttpClient.newCall(request).execute()){
        if(this.code() == 200){
            return body()?.string() ?: throw IllegalStateException("No response body")
        }
        throw IllegalStateException("Request was not Ok: status: ${this.code()}")
    }
}