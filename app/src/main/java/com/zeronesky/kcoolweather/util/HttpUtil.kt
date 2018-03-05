package com.zeronesky.kcoolweather.util

import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Created by zyxins on 05/03/2018.
 */
class HttpUtil {
    companion object {
        fun sendOkHttpRequest(address: String,callback: okhttp3.Callback): Unit {
            val client = OkHttpClient()
            val request = Request.Builder().url(address).build()
            client.newCall(request).enqueue(callback)
        }
    }
}