package com.example.sourabh.navigate.services

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import com.example.sourabh.navigate.helpers.getDistanceFromResponse
import com.example.sourabh.navigate.helpers.getDistanceResponse
import com.example.sourabh.navigate.helpers.getDurationFromResponse
import com.example.sourabh.navigate.data.network.DistanceApiRequest
import com.google.android.gms.maps.model.LatLng
import java.lang.ref.WeakReference
import com.example.sourabh.navigate.helpers.fromLatLngs

class DistanceFinderAsyncTask(activity: Activity): AsyncTask<LatLng, Unit, Unit>() {


    private val weakReferenceActivity: WeakReference<Activity> = WeakReference(activity)
    private var mOnDistanceRetrievedListener: OnDistanceRetrievedListener? = null

    var distance: Long = -1
        private set
    var time: Long = -1
        private set

    override fun onPreExecute() {
        mOnDistanceRetrievedListener?.onStart()
    }

    override fun doInBackground(vararg params: LatLng?){
        if(params.size != 2){
            throw IllegalArgumentException("Invalid params: $params")
        }
        val source = params[0]
        source ?: throw IllegalArgumentException("No source address provided")
        val destination = params[1]
        destination ?: throw IllegalArgumentException("No destination address provided")
        calculateDistance(source, destination)
    }

    override fun onPostExecute(result: Unit?) {
        mOnDistanceRetrievedListener?.onRetrieve()
    }

    private fun calculateDistance(source: LatLng, destination: LatLng){
        val query = DistanceApiRequest(origins = fromLatLngs(source), destinations = fromLatLngs(destination)).toQueryUrl(weakReferenceActivity.get()!!.baseContext)
        Log.d("xyz_query", query)
        val response = getDistanceResponse(query)
        parseResponse(response)
    }

    private fun parseResponse(response: String) {
        Log.d("xyz_response_body", response)
        distance = getDistanceFromResponse(response)
        time = getDurationFromResponse(response)
        Log.d("xyz_distance", "$distance")
        Log.d("xyz_time","$time")

    }

    interface OnDistanceRetrievedListener {
        fun onStart()
        fun onRetrieve()
    }

    fun setOnAddressRetrievedListener(listener: OnDistanceRetrievedListener?){
        mOnDistanceRetrievedListener = listener
    }
}