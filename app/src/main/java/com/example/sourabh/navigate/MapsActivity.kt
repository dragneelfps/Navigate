package com.example.sourabh.navigate

import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.sourabh.navigate.services.AddressFinderAsyncTask
import com.example.sourabh.navigate.services.DirectionFinderAsyncTask
import com.example.sourabh.navigate.services.DistanceFinderAsyncTask
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMapClickListener{

    private var mDestinationMarker: Marker? = null
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationProvider: FusedLocationProviderClient

    private var mMyLocation: Location? = null
    private var mLocationPermissionDenied: Boolean = false

    var myLocationMarker: Marker? = null
    private var routePolyline: Polyline? = null

    companion object {
        private val LOCATION_PERMISSION_REQ: Int = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mFusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMyLocationClickListener(this)
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMapClickListener(this)
        enableMyLocation()
    }

    private fun enableMyLocation() {
        if (PermissionUtil.isPermissionAvailable(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            mMap.isMyLocationEnabled = true
            showMyLocation()
        } else {
            PermissionUtil.requestPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_REQ)
        }
    }




    fun showMyLocation() {
        mFusedLocationProvider.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let { loc ->
                mMyLocation = loc
                val latLng = LatLng(loc.latitude, loc.longitude)
                val markerOptions  = MarkerOptions().position(latLng).title("My Location")
                myLocationMarker = mMap.addMarker(markerOptions)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                val myLocationAddressTask = AddressFinderAsyncTask(this@MapsActivity)
                myLocationAddressTask.setOnAddressRetrievedListener(object: AddressFinderAsyncTask.OnAddressRetrievedListener{
                    override fun onStart() {
                        source_address.text = "Loading........"
                    }

                    override fun onRetrieve(address: String) {
                        myLocationMarker?.title = address
                        myLocationMarker?.showInfoWindow()
                        source_address.text = address
                    }
                })
                myLocationAddressTask.execute(latLng)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQ) {
            if (PermissionUtil.isPermissionGranted(permissions, grantResults, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                mMap.isMyLocationEnabled = true
                showMyLocation()
            } else {
                mLocationPermissionDenied = true
                val deniedDialog = PermissionUtil.PermissionDeniedDialog.newInstance(false)
                deniedDialog.message = "Where art thou?"
                deniedDialog.show(supportFragmentManager, "denied_dialog")
            }
        }
    }


    override fun onMyLocationButtonClick(): Boolean {
        //Show my location
        return false
    }

    override fun onMyLocationClick(location: Location) {

    }

    override fun onMapClick(latLng: LatLng?) {
        mDestinationMarker?.remove()
        latLng?.let {
            val marker = MarkerOptions().position(it).title("Destination")
            mDestinationMarker = mMap.addMarker(marker)
            mDestinationMarker?.isDraggable = true
            val destinationAddressTask = AddressFinderAsyncTask(this@MapsActivity)
            destinationAddressTask.setOnAddressRetrievedListener(object: AddressFinderAsyncTask.OnAddressRetrievedListener{
                override fun onStart() {
                    destination_address.text = "Loading........"
                }

                override fun onRetrieve(address: String) {
                    mDestinationMarker?.title = address
                    mDestinationMarker?.showInfoWindow()
                    destination_address.text = address
                }
            })
            destinationAddressTask.execute(latLng)
            val distanceFinderAsyncTask = DistanceFinderAsyncTask(this@MapsActivity)
            distanceFinderAsyncTask.setOnAddressRetrievedListener(object : DistanceFinderAsyncTask.OnDistanceRetrievedListener{
                override fun onStart() {
                    distance.text = "Loading........"
                    time.text = "Loading........"
                }

                override fun onRetrieve() {
                    val d = distanceFinderAsyncTask.distance
                    distance.text  = if(d == -1L){
                        "Not found"
                    }else{
                        formatDistance(d.toString())
                    }
                    val t = distanceFinderAsyncTask.time
                    time.text  = if(t == -1L){
                        "Not found"
                    }else{
                        formatTime(t.toString())
                    }
                }
            })
            distanceFinderAsyncTask.execute(myLocationMarker?.position, mDestinationMarker?.position)
            val directionFinderAsyncTask = DirectionFinderAsyncTask(this)
            directionFinderAsyncTask.setOnAddressRetrievedListener(object : DirectionFinderAsyncTask.OnDirectionRetrievedListener{
                override fun onStart() {

                }

                override fun onRetrieve(successful: Boolean) {
                    routePolyline?.remove()

                    if(!successful) return

                    //The request was successful
                    directionFinderAsyncTask.steps?.let {
                        routePolyline = mMap.addPolyline(PolylineOptions()
                                .clickable(true)
                                .addAll(it))
                    }
                }
            })
            directionFinderAsyncTask.execute(myLocationMarker?.position, mDestinationMarker?.position)

        }
    }

    fun formatTime(time: String) : String {
        val t = time.toLong()
        val hours = t/3600
        val minutes = t/60
        val secs = t%60
        return "$hours hours $minutes minutes $secs seconds"
    }

    fun formatDistance(distance: String) = "$distance meters"

}
