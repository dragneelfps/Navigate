package com.example.sourabh.navigate.data.network

import com.example.sourabh.navigate.data.network.base.Request

class DistanceApiRequest(var origins: String, var destinations: String): Request {

    private var mKey: String = "AIzaSyDXvNwCFJWjeKJF2Tm1CerJqSq2fxytLkY"

    companion object {
        private val mRequestPrefix = "https://maps.googleapis.com/maps/api/distancematrix/json?"
    }

    override fun toQueryUrl() = "${mRequestPrefix}origins=$origins&destinations=$destinations&key=$mKey"
}