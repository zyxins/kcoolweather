package com.zeronesky.kcoolweather.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.preference.PreferenceManager
import com.zeronesky.kcoolweather.util.HttpUtil
import com.zeronesky.kcoolweather.util.Utility
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

/**
 * Created by zyxins on 06/03/2018.
 */
class AutoUpdateService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        updateWeather()
        updateBingPic()

        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val anHour = 8 * 60 * 60 * 1000
        val triggerAtTime = SystemClock.elapsedRealtime() + anHour
        val i = Intent(this, AutoUpdateService::class.java)
        val pi = PendingIntent.getService(this, 0, i, 0)
        manager.cancel(pi!!)
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi)

        return super.onStartCommand(intent, flags, startId)
    }


    fun updateWeather() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val weatherString = prefs.getString("weather", null)

        if (weatherString != null) {
            val weather = Utility.handleWeatherResponse(weatherString)
            val weatherId = weather!!.basic!!.weatherId
            val weatherUrl = "http://guolin.tech/api/weather?cityid=$weatherId&key=bc0418b57b2d4918819d3974ac1285d9"
            HttpUtil.sendOkHttpRequest(weatherUrl, object : okhttp3.Callback {
                override fun onResponse(call: Call?, response: Response?) {
                    val responseText = response!!.body()!!.string()
                    val weather = Utility.handleWeatherResponse(responseText)

                    if (weather != null && "ok".equals(weather.status)) {
                        var editor = PreferenceManager.getDefaultSharedPreferences(this@AutoUpdateService).edit()
                        editor.putString("weather", responseText)
                        editor.apply()
                    }
                }

                override fun onFailure(call: Call?, e: IOException?) {
                    e?.printStackTrace()
                }
            })
        }
    }

    fun updateBingPic() {
        var requestBingPic = "http://guolin.tech/api/bing_pic"
        HttpUtil.sendOkHttpRequest(requestBingPic,object: okhttp3.Callback{
            override fun onResponse(call: Call?, response: Response?) {
                var bingPic = response!!.body()!!.string()
                var editor = PreferenceManager.getDefaultSharedPreferences(this@AutoUpdateService).edit()
                editor.putString("bing_pic",bingPic)
                editor.apply()
            }

            override fun onFailure(call: Call?, e: IOException?) {
                e?.printStackTrace()
            }
        })
    }
}