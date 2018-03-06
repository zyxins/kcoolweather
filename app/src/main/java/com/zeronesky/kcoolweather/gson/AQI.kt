package com.zeronesky.kcoolweather.gson

/**
 * Created by zyxins on 06/03/2018.
 */
class AQI {
    var city: AQICity? = null

    inner class AQICity{

        var aqi: String? = null
        var pm25:String? = null
    }
}