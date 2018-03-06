package com.zeronesky.kcoolweather.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by zyxins on 06/03/2018.
 */
class Now {
    @SerializedName("tmp")
    var temperature: String? = null

    @SerializedName("cond")
    var more: More? = null

    inner class More{
        @SerializedName("txt")
        var info: String? = null
    }
}