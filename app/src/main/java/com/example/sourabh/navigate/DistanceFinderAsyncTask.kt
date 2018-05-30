package com.example.sourabh.navigate

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import com.beust.klaxon.Klaxon
import com.example.sourabh.navigate.models.DistanceApiRequest
import com.example.sourabh.navigate.util.fromLatLngs
import com.google.android.gms.maps.model.LatLng
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.ref.WeakReference

class DistanceFinderAsyncTask(activity: Activity): AsyncTask<LatLng, Unit, Unit>() {


    private val weakReferenceActivity: WeakReference<Activity> = WeakReference(activity)
    private var mOnDistanceRetrievedListener: OnDistanceRetrievedListener? = null

    var distance: Long = -1
    var time: Long = -1

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
//        val originParam = generateQueryAddress(source)
//        val destinationParam = generateQueryAddress(destination)
//        val query = generateQuery(originParam, destinationParam)
        val query = DistanceApiRequest(origins = fromLatLngs(source), destinations = fromLatLngs(destination)).toQueryUrl()
        Log.d("xyz_query", query)
        val response = getResponse(query)
        parseResponse(response)
    }

    private fun getResponse(query: String): String{
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(query).build()
        with(okHttpClient.newCall(request).execute()){
            if(this.code() == 200){
                return body()?.string() ?: throw IllegalStateException("No response body")
            }
            throw IllegalStateException("Request was not Ok: status: ${this.code()}")
        }
    }

    private fun parseResponse(response: String) {
        Log.d("xyz_response_body", response)
        val distanceApiResponse = Klaxon().parse<DistanceApiResponse>(response)
        distanceApiResponse?.let { responseObj->
            if(!responseObj.isSuccessful()){
                throw IllegalArgumentException(responseObj.error_meesage)
            }
            val element = responseObj.rows[0].elements[0]
            if(!element.isSuccessful()){
                throw IllegalArgumentException("Request not successful: Response: ${element.status} ")
            }
            distance = responseObj.rows[0].elements[0].distance!!.value
            time = responseObj.rows[0].elements[0].duration!!.value
            Log.d("xyz_distance", "$distance")
            Log.d("xyz_time","$time")
        }
    }

    private fun generateQuery(origins: String, destinations: String): String {
        val prefix = "https://maps.googleapis.com/maps/api/distancematrix/json?"
        val apiKey = weakReferenceActivity.get()?.getString(R.string.google_distance_api_key)
        apiKey ?: throw IllegalAccessException("No API key provided for ")
        return "${prefix}origins=$origins&destinations=$destinations&key=$apiKey"
    }

    private fun generateQueryAddress(location: LatLng): String {
        return "${location.latitude},${location.longitude}"
    }

    interface OnDistanceRetrievedListener {
        fun onStart()
        fun onRetrieve()
    }


    fun setOnAddressRetrievedListener(listener: OnDistanceRetrievedListener?){
        mOnDistanceRetrievedListener = listener
    }
}