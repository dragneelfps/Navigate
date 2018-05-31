package com.example.sourabh.navigate.services

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import com.example.sourabh.navigate.data.network.DirectionApiRequest
import com.example.sourabh.navigate.data.network.DirectionApiResponse
import com.example.sourabh.navigate.helpers.fromLatLngs
import com.example.sourabh.navigate.helpers.getResponse
import com.google.android.gms.maps.model.LatLng
import java.lang.ref.WeakReference

class DirectionFinderAsyncTask(activity: Activity) : AsyncTask<LatLng, Unit, Unit>() {

    private val weakReferenceActivity: WeakReference<Activity> = WeakReference(activity)
    var steps: List<LatLng>? = null
    private var successful: Boolean = false

    override fun doInBackground(vararg params: LatLng?) {
        if (params.size != 2) {
            throw IllegalArgumentException("Invalid params: $params")
        }
        val source = params[0]
        source ?: throw IllegalArgumentException("No source address provided")
        val destination = params[1]
        destination ?: throw IllegalArgumentException("No destination address provided")
        generateResponse(source, destination)
    }

    override fun onPostExecute(result: Unit?) {
        mOnDirectionRetrievedListener?.onRetrieve(successful)
    }

    private fun generateResponse(source: LatLng, destination: LatLng) {
        val query = DirectionApiRequest(origin = fromLatLngs(source), destination = fromLatLngs(destination)).toQueryUrl()
        Log.d("xyz_direction_query", query)
        val response = getResponse(query)
        parseResponse(response)
    }

    private fun parseResponse(response: String) {
        Log.d("xyz_directions_body", response)
        val directionApiResponse = DirectionApiResponse.fromString(response)
        steps = directionApiResponse.getPolylines()
        successful = directionApiResponse.isSuccesful()
    }

    interface OnDirectionRetrievedListener {
        fun onStart()
        fun onRetrieve(successful: Boolean)
    }


    private var mOnDirectionRetrievedListener: OnDirectionRetrievedListener? = null

    fun setOnAddressRetrievedListener(listener: OnDirectionRetrievedListener?) {
        mOnDirectionRetrievedListener = listener
    }
}