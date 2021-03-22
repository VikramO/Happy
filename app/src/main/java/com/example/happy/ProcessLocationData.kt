package com.example.happy

import android.content.Context
import android.location.Location
import com.example.happy.GetUserLocation

class ProcessLocationData {
    fun getCoordinates(context: Context?): String {
        val locationResult: GetUserLocation.LocationResult =
            object : GetUserLocation.LocationResult() {
                override fun gotLocation(location: Location) {
                    //Got the location!
                }
            }
        val userLocation = GetUserLocation()
        val loc = userLocation.getLocation(context, locationResult)
        val gps_lat = loc["GPS"]!!.substring(0, loc["GPS"]!!.indexOf(","))
        val gps_long = loc["GPS"]!!
            .substring(loc["GPS"]!!.indexOf(",") + 1, loc["GPS"]!!.length)
        val gps_parsed_lat = gps_lat.toFloat()
        val gps_parsed_long = gps_long.toFloat()
        val net_lat = loc["GPS"]!!.substring(0, loc["GPS"]!!.indexOf(","))
        val net_long = loc["Network"]!!
            .substring(loc["Network"]!!.indexOf(",") + 1, loc["Network"]!!.length)
        val net_parsed_lat = net_lat.toFloat()
        val net_parsed_long = net_long.toFloat()
        val lat_avg = (gps_parsed_lat + net_parsed_lat) / 2
        val long_avg = (gps_parsed_long + net_parsed_long) / 2
        return "$lat_avg,$long_avg"
    }
}