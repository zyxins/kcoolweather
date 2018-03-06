package com.zeronesky.kcoolweather.gson

/**
 * Created by zyxins on 06/03/2018.
 */


import com.google.gson.annotations.SerializedName

class Forecast {

    var date: String? = null

    @SerializedName("tmp")
    var temperature: Temperature? = null

    @SerializedName("cond")
    var more: More? = null

    inner class Temperature {

        var max: String? = null

        var min: String? = null

    }

    inner class More {

        @SerializedName("txt_d")
        var info: String? = null

    }

}