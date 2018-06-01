package com.example.sourabh.navigate.data.network

import android.content.Context
import com.example.sourabh.navigate.R
import com.example.sourabh.navigate.data.network.base.Request

class DirectionApiRequest(var origin: String, var destination: String): Request {

    private val mKey = R.string.google_maps_key

    companion object {
        private val mRequestPrefix = "https://maps.googleapis.com/maps/api/directions/json?"
    }

    override fun toQueryUrl(context: Context) = "${mRequestPrefix}origin=$origin&destination=$destination&key=${context.getString(mKey)}"
}