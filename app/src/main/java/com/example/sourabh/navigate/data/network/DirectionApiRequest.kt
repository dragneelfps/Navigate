package com.example.sourabh.navigate.data.network

import com.example.sourabh.navigate.data.network.base.Request

class DirectionApiRequest(var origin: String, var destination: String): Request {

    private val mKey = "AIzaSyDXvNwCFJWjeKJF2Tm1CerJqSq2fxytLkY"

    companion object {
        private val mRequestPrefix = "https://maps.googleapis.com/maps/api/directions/json?"
    }

    override fun toQueryUrl() = "${mRequestPrefix}origin=$origin&destination=$destination&key=$mKey"
}