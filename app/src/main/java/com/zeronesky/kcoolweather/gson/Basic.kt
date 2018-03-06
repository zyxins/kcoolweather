package com.zeronesky.kcoolweather.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by zyxins on 06/03/2018.
 */

class Basic {
    @SerializedName("city")
    var cityName: String? = null

    @SerializedName("id")
    var weatherId: String? = null

    var update: Update? = null

    inner class Update {
        @SerializedName("loc")
        var updateTime: String? = null
    }
}
