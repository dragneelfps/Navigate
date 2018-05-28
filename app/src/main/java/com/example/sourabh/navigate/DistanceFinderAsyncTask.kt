package com.example.sourabh.navigate

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import com.beust.klaxon.Klaxon
import com.google.android.gms.maps.model.LatLng
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.ref.WeakReference

class DistanceFinderAsyncTask(activity: Activity): AsyncTask<LatLng, Void, String?>() {


    private val weakReferenceActivity: WeakReference<Activity> = WeakReference(activity)
    private var mOnDistanceRetrievedListener: OnDistanceRetrievedListener? = null

    override fun doInBackground(vararg params: LatLng?): String? {
        if(params.isEmpty()){
            return null
        }
        val source = params[0]
        source ?: return null
        val destination = params[1]
        destination ?: return null
        return calculateDistance(source, destination)
    }

    override fun onPostExecute(result: String?) {
        result?.let{ distance ->
            mOnDistanceRetrievedListener?.onRetrieve(distance)
        }
    }

    private fun calculateDistance(source: LatLng, destination: LatLng): String? {
        val origins = parseLatLng(source)
        val destinations = parseLatLng(destination)
        val queryUrl = generateQueryUrl(origins, destinations)
        Log.d("xyz_query_url", queryUrl)
        queryUrl ?: return null
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(queryUrl).build()
        val response = okHttpClient.newCall(request).execute()
        return parseDistance(response)
    }

    private fun parseDistance(response: Response?): String? {
        response?.body()?.string()?.let{ json->
            Log.d("xyz_response_body", json)
            val distanceApiResponse = Klaxon()
                    .parse<DistanceApiResponse>(json)
            Log.d("xyz_response_object", distanceApiResponse.toString())
            val distance = distanceApiResponse?.rows?.get(0)?.elements?.get(0)?.distance?.text
            Log.d("xyz_distance", distance)
            return distance
        }
        return null
    }

    private fun generateQueryUrl(origins: String, destinations: String): String? {
        val prefix = "https://maps.googleapis.com/maps/api/distancematrix/json?"
        val apiKey = weakReferenceActivity.get()?.getString(R.string.google_maps_key)
        apiKey ?: return null
        return "${prefix}origins=${origins}&destinations=${destinations}&key=${apiKey}"
    }

    private fun parseLatLng(location: LatLng): String {
        return "${location.latitude},${location.longitude}"
    }



    interface OnDistanceRetrievedListener {
        fun onRetrieve(distance: String)
    }


    fun setOnAddressRetrievedListener(listener: OnDistanceRetrievedListener?){
        mOnDistanceRetrievedListener = listener
    }
}