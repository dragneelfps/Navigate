package com.example.sourabh.navigate.models

import com.google.android.gms.maps.model.LatLng

class DistanceApiRequest constructor(var origins: String, var destinations: String){

    private var mKey: String = "AIzaSyDXvNwCFJWjeKJF2Tm1CerJqSq2fxytLkY"

    companion object {
        private val mRequestPrefix = "https://maps.googleapis.com/maps/api/distancematrix/json?"
    }

    fun toQueryUrl() = "${mRequestPrefix}origins=$origins&destinations=$destinations&key=$mKey"
}