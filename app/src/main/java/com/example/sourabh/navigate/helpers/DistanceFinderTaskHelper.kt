package com.example.sourabh.navigate.helpers

import com.example.sourabh.navigate.services.DistanceFinderAsyncTask
import com.example.sourabh.navigate.data.network.DistanceApiResponse
import okhttp3.OkHttpClient
import okhttp3.Request

fun DistanceFinderAsyncTask.getDistanceResponse(query: String): String{
    val okHttpClient = OkHttpClient()
    val request = Request.Builder().url(query).build()
    with(okHttpClient.newCall(request).execute()){
        if(this.code() == 200){
            return body()?.string() ?: throw IllegalStateException("No response body")
        }
        throw IllegalStateException("Request was not Ok: status: ${this.code()}")
    }
}

fun DistanceFinderAsyncTask.getDistanceFromResponse(response: String): Long{
    val distanceApiResponse = DistanceApiResponse.fromString(response)
    distanceApiResponse.let { responseObj ->
        if (!responseObj.isSuccessful()) {
            throw IllegalArgumentException(responseObj.errorMessage)
        }
        val element = responseObj.getElement(0, 0)
        if (!element.isSuccessful()) {
            throw IllegalArgumentException("Request not successful: Response: ${element.status} ")
        }
        return responseObj.getDistance()
    }
}

fun DistanceFinderAsyncTask.getDurationFromResponse(response: String): Long{
    val distanceApiResponse = DistanceApiResponse.fromString(response)
    distanceApiResponse.let { responseObj ->
        if (!responseObj.isSuccessful()) {
            throw IllegalArgumentException(responseObj.errorMessage)
        }
        val element = responseObj.getElement(0, 0)
        if (!element.isSuccessful()) {
            throw IllegalArgumentException("Request not successful: Response: ${element.status} ")
        }
        return responseObj.getDuration()
    }
}