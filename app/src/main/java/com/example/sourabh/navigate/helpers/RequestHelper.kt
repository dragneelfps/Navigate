package com.example.sourabh.navigate.helpers

import com.example.sourabh.navigate.data.network.DistanceApiRequest
import com.google.android.gms.maps.model.LatLng


fun fromAddresses(vararg addresses: String) : String{
    var result = ""
    addresses.forEachIndexed{ i, address ->
        result += if(i != addresses.size - 1){
            "$address|"
        }else{
            address
        }
    }
    return result
}
fun fromLatLngs(vararg latLngs: LatLng): String{
    var result = ""
    latLngs.forEachIndexed{ i, latLng ->
        result += if(i != latLngs.size -1)
            "${latLng.latitude},${latLng.longitude}|"
        else
            "${latLng.latitude},${latLng.longitude}"
    }
    return result
}
fun fromPlaceId(placeId: String): String{
    return "place_id:$placeId"
}