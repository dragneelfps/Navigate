package com.example.sourabh.navigate

import com.beust.klaxon.Json

data class DistanceApiResponse(
    @Json(name = "destination_addresses")
    val destinations: Array<String>,
    @Json(name = "origin_addresses")
    val origins: Array<String>,
    val rows: Array<Row>,
    val status: String,
    val error_meesage: String = ""
){
    fun isSuccessful(): Boolean{
        return status == "OK"
    }
}

data class Row(
    val elements: Array<Element>
)

data class Element(
    val distance: Distance? = null,
    val duration: Duration? = null,
    val status: String
){
    fun isSuccessful(): Boolean{
        return status == "OK"
    }
}

data class Distance(
    val text: String,
    val value: Long
)

data class Duration(
    val text: String,
    val value: Long
)