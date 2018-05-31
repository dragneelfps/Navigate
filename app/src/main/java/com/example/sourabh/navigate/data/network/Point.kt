package com.example.sourabh.navigate.data.network

import com.beust.klaxon.Json
import com.google.android.gms.maps.model.LatLng

class Point(
        @Json(name = "lat")
        var latitude: Double,
        @Json(name = "lng")
        var longitude: Double
){
        fun toLatLng() = LatLng(latitude, longitude)
}