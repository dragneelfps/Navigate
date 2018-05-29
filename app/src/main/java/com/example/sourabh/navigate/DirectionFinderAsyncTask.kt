package com.example.sourabh.navigate

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import com.beust.klaxon.Klaxon
import com.google.android.gms.maps.model.LatLng
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.ref.WeakReference

class DirectionFinderAsyncTask(activity: Activity): AsyncTask<LatLng, Unit, Unit>() {

    private val weakReferenceActivity: WeakReference<Activity> = WeakReference(activity)

    override fun doInBackground(vararg params: LatLng?) {
        if(params.size != 2){
            throw IllegalArgumentException("Invalid params: $params")
        }
        val source = params[0]
        source ?: throw IllegalArgumentException("No source address provided")
        val destination = params[1]
        destination ?: throw IllegalArgumentException("No destination address provided")
        generateResponse(source, destination)
    }

    override fun onPostExecute(result: Unit?) {
        mOnDirectionRetrievedListener?.onRetrieve()
    }

    private fun generateResponse(source: LatLng, destination: LatLng) {
        val sourceParam = generateQueryAddress(source)
        val destinationParam = generateQueryAddress(destination)
        val query = generateQuery(sourceParam, destinationParam)
        Log.d("xyz_direction_query", query)
        val response = getResponse(query)
        parseResponse(response)
    }


    private fun generateQueryAddress(location: LatLng): String {
        return "${location.latitude},${location.longitude}"
    }

    var steps: List<LatLng>? = null

    private fun parseResponse(response: String) {
        Log.d("xyz_directions_body", response)
        val directionApiResponse = Klaxon().parse<DirectionApiResponse>(response)
        steps = directionApiResponse?.getSteps()
        steps?.forEach {
            Log.d("xyz_steps", it.toString())
        }

    }

    private fun getResponse(query: String): String {
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(query).build()
        with(okHttpClient.newCall(request).execute()){
            if(this.code() == 200){
                return body()?.string() ?: throw IllegalStateException("No response body")
            }
            throw IllegalStateException("Request was not Ok: status: ${this.code()}")
        }
    }

    private fun generateQuery(source: String, destination: String): String {
        val prefix = "https://maps.googleapis.com/maps/api/directions/json?"
        val apiKey = weakReferenceActivity.get()?.getString(R.string.google_distance_api_key)
        apiKey ?: throw IllegalAccessException("No API key provided for ")
        return "${prefix}origin=$source&destination=$destination&key=$apiKey"
    }


    interface OnDirectionRetrievedListener {
        fun onStart()
        fun onRetrieve()
    }


    private var mOnDirectionRetrievedListener: OnDirectionRetrievedListener? = null

    fun setOnAddressRetrievedListener(listener: OnDirectionRetrievedListener?){
        mOnDirectionRetrievedListener = listener
    }
}