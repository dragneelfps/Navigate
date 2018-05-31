package com.example.sourabh.navigate.data.network

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import com.google.android.gms.maps.model.LatLng


/**
 * Describes response from Google Directions API
 * All the currently not needed fields are commented out for now
 */

class DirectionApiResponse(
        val status: String,
//        @Json(name = "geocoded_waypoints")
//        val geocodedWaypoints: Array<GeocodedWaypoint>,
        val routes: Array<Route>,
        @Json(name = "error_message")
        val errorMessage: String = ""
) {

    fun isSuccesful() = status == "OK"

    fun getPolylines(): List<LatLng> {
        val polylines = ArrayList<LatLng>(0)
        if(isSuccesful()){
            with(routes[0]){
                with(this.legs[0]){
                    this.steps.forEachIndexed{ i, step ->
                        polylines.add( if(i != this.steps.size - 1){
                            step.startLocation.toLatLng()
                        } else {
                            step.endLocation.toLatLng()
                        })
                    }
                }
            }
        }
        return polylines
    }

    companion object {
        fun fromString(response: String) =
                Klaxon().parse<DirectionApiResponse>(response) ?: throw IllegalArgumentException("The response cannot be converted to an object")
    }

    class GeocodedWaypoint(
            @Json(name = "geocoder_status")
            val geocoderStatus: String? = null,
            @Json(name = "place_id")
            val placeId: String? = null,
            val types: Array<String>? = null,
            @Json(name = "partial_match")
            val partialMatch: Boolean? = null
    ){
        fun isEmpty() = geocoderStatus == null
    }

    class Route(
//            val bounds: Bound,
//            val copyrights: String,
            val legs: Array<Leg>
//            @Json(name = "overview_polyline")
//            val overviewPolyline: Polyline,
//            val summary: String,
//            val warnings: Array<String>
//            @Json(name = "waypoint_order")
//            val waypointOrder: Array<Int>
    )

    class Bound(
            val northeast: Point,
            val southwest: Point
    )

    class Leg(
//            val distance: Distance,
//            val duration: Duration,
//            @Json(name = "end_address")
//            val endAddress: String,
//            @Json(name = "end_location")
//            val endLocation: Point,
//            @Json(name = "start_address")
//            val startAddress: String,
//            @Json(name = "start_location")
//            val startLocation: Point,
            val steps: Array<Step>
//            @Json(name = "traffic_speed_entry")
//            val trafficSpeedEntry: Array<String>? = null, //No idea about the type returned. Resolve when we get an error
//            @Json(name = "via_waypoint")
//            val viaWaypoint: Array<String>? = null //This too
    )

    class Step(
//            val distance: Distance,
//            val duration: Duration,
            @Json(name = "end_location")
            val endLocation: Point,
//            @Json(name = "html_instruction")
//            val htmlInstruction: String = "",
//            val polyline: Polyline,
            @Json(name = "start_location")
            val startLocation: Point
//            @Json(name = "travel_mode")
//            val travelMode: String
    )
}