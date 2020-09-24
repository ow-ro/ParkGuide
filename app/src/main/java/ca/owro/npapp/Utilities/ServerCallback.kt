package ca.owro.npapp.Utilities

import org.json.JSONObject

interface ServerCallback {
    fun onParkDataResponse(response:Boolean)
    fun onWeatherApiResponse(response: Boolean)
    fun onNPSAlertApiResponse(response: Boolean)
    fun onTripDetailDataResponse(response: Boolean)
    fun onTripDetailUILoadResponse(response: Boolean)
}