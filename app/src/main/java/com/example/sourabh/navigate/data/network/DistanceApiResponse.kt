package com.example.sourabh.navigate.data.network

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon

/**
 * Describes response from Google Distance Matrix API
 * All the currently not needed fields are commented out for now
 */
class DistanceApiResponse private constructor(
        val status: String,
//        @Json(name = "origin_addresses")
//        val originAddresses: Array<String>,
//        @Json(name = "destination_addresses")
//        val destinationAddresses: Array<String>,
        val rows: Array<Row>,
        @Json(name = "error_message")
        val errorMessage: String = ""
){
    class Row(
            val elements: Array<Element>
    )

    class Element(
            val status: String,
            val duration: Duration? = null,
            val distance: Distance? = null
    ){
        fun isSuccessful() = status == "OK"
    }

    companion object {
        fun fromString(response: String) =
            Klaxon().parse<DistanceApiResponse>(response) ?: throw IllegalArgumentException("The response cannot be converted to an object")
    }

    fun isSuccessful() = status == "OK"

    fun getElement(originIndex: Int = 0, destnationIndex: Int = 0): Element {
        return rows[originIndex].elements[destnationIndex]
    }

    fun getDistance(originIndex: Int = 0, destnationIndex: Int = 0): Long{
        return rows[originIndex].elements[destnationIndex].distance?.value ?: -1
    }

    fun getDuration(originIndex: Int = 0, destnationIndex: Int = 0): Long{
        return rows[originIndex].elements[destnationIndex].duration?.value ?: -1
    }
}