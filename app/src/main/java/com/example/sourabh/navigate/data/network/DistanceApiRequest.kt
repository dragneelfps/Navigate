package com.example.sourabh.navigate.data.network

import android.content.Context
import com.example.sourabh.navigate.R
import com.example.sourabh.navigate.data.network.base.Request

class DistanceApiRequest(var origins: String, var destinations: String): Request {

    private var mKey = R.string.google_maps_key

    companion object {
        private val mRequestPrefix = "https://maps.googleapis.com/maps/api/distancematrix/json?"
    }

    override fun toQueryUrl(context: Context) = "${mRequestPrefix}origins=$origins&destinations=$destinations&key=${context.getString(mKey)}"
}