package com.example.sourabh.navigate

import android.app.Activity
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*

class AddressFinderAsyncTask(activity: Activity): AsyncTask<LatLng,Void, String>() {

    private val weakReferenceActivity: WeakReference<Activity> = WeakReference(activity)
    private var mOnAddressRetrievedListener: OnAddressRetrievedListener? = null

    override fun doInBackground(vararg params: LatLng?): String {
        var address = ""
        params[0]?.let { location ->
            weakReferenceActivity.get()?.let { context ->
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses: List<Address>
                try{
                    addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if(addresses.isNotEmpty()){
                        val addr = addresses[0]
                        val addressFragments = with(addr) {
                            (0..maxAddressLineIndex).map { getAddressLine(it) }
                        }
                        address = addressFragments.joinToString("\n")
                    }
                }catch (ioException : IOException){
                    address = "I/O Exception"
                }catch (illegalArgumentException: IllegalArgumentException){
                    address = "Illegal Arguments"
                }
            }

        }
        return address
    }

    override fun onPostExecute(address: String) {
        mOnAddressRetrievedListener?.onRetrieve(address)
    }

    interface OnAddressRetrievedListener {
        fun onRetrieve(address: String)
    }

    fun setOnAddressRetrievedListener(listener: OnAddressRetrievedListener?){
        mOnAddressRetrievedListener = listener
    }
}