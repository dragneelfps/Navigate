package com.example.sourabh.navigate

import com.beust.klaxon.Json
import com.google.android.gms.maps.model.LatLng

data class DirectionApiResponse(
        @Json(name = "geocoded_waypoints")
        var geocodedWaypoints: Array<WayPoint>,
        var routes: Array<Route>,
        var status: String
){
        fun isSuccessful() = status == "OK"
        fun getSteps(routeIndex: Int = 0): List<LatLng>{
                return routes[routeIndex].getSteps()
        }
}

data class WayPoint(
        @Json(name = "geocoder_status")
        var geocoderStatus: String,
        @Json(name = "place_id")
        var placeId: String = "",
        @Json(name = "types")
        var waypointTypes: Array<String>
)

data class Route(
        var bounds: Bounds,
        var copyrights: String,
        var legs: Array<Leg>,
        @Json(name = "overview_polyline")
        var overviewPolyline: PolyLine,
        var summary: String
        //Todo add warnings and waypoint_order. Refer to API
){
        fun getSteps(): List<LatLng>{
                val calculatedSteps = ArrayList<LatLng>(0)
                with(legs[0]){
                        steps.forEach { step ->
                                calculatedSteps.add(step.getStartLocation())
                        }
                        calculatedSteps.add(steps.last().getEndLocation())
                        return calculatedSteps
                }
        }
}

data class Bounds(
        var northeast: Location,
        var southwest: Location
){
}

data class Location(
        var lat: Double,
        var lng: Double
)

data class Leg(
        var distance: Distance,
        var duration: Duration,
        @Json(name = "end_address")
        var endAddress: String,
        @Json(name = "end_location")
        var endLocation: Location,
        @Json(name = "start_address")
        var startAddress: String,
        @Json(name = "start_location")
        var startLocation: Location,
        var steps: Array<Step>
        //Todo add traffic_speed_entry and via_waypoint. Refer to API
)

data class Step(
        var distance: Distance,
        var duration: Duration,
        @Json(name = "end_location")
        var endLocation: Location,
        @Json(name = "html_instructions")
        var htmlInstructions: String,
        @Json(name = "start_location")
        var startLocation: Location,
        @Json(name = "travel_mode")
        var travelMode: String
){
        fun getStartLocation(): LatLng{
                return LatLng(startLocation.lat, startLocation.lng)
        }
        fun getEndLocation(): LatLng{
                return LatLng(endLocation.lat, endLocation.lng)
        }
}

data class PolyLine(
        var points: String
)