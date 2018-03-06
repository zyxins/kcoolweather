package com.zeronesky.kcoolweather

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.zeronesky.kcoolweather.gson.Weather
import com.zeronesky.kcoolweather.service.AutoUpdateService
import com.zeronesky.kcoolweather.util.HttpUtil
import com.zeronesky.kcoolweather.util.Utility
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.aqi.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.now.*
import kotlinx.android.synthetic.main.suggestion.*
import kotlinx.android.synthetic.main.title.*
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

/**
 * Created by zyxins on 06/03/2018.
 */
class WeatherActivity :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(Build.VERSION.SDK_INT >= 21) {
            var decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = Color.TRANSPARENT
        }

        setContentView(R.layout.activity_weather)

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)

        var prefs = PreferenceManager.getDefaultSharedPreferences(this)
        var weatherString = prefs.getString("weather",null)
       // weatherLayout = findViewById(R.id.weatherLayout) as ScrollView

        val weatherId: String?

        if (weatherString != null) {
            val weather = Utility.handleWeatherResponse(weatherString)
            weatherId = weather?.basic?.weatherId
            showWeatherInfo(weather!!)
        }else
        {
            weatherId = getIntent().getStringExtra("weather_id")
            weatherLayout.visibility = View.INVISIBLE
            requestWeather(weatherId)
        }

        swipeRefresh.setOnRefreshListener {
            requestWeather(weatherId!!)
        }

        navButton.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        var bingPic = prefs.getString("bing_pic",null)
        if (bingPicImg != null) {
            Glide.with(this).load(bingPic).into(bingPicImg)
        }else
        {
            loadBingPic()
        }

    }

    fun requestWeather(weatherId: String){
        var weatherUrl =  "http://guolin.tech/api/weather?cityid=$weatherId&key=bc0418b57b2d4918819d3974ac1285d9"
        HttpUtil.sendOkHttpRequest(weatherUrl,object: okhttp3.Callback{

            override fun onResponse(call: Call?, response: Response?) {
                val responseText = response!!.body()!!.string()
                val weather = Utility.handleWeatherResponse(responseText)

                runOnUiThread{
                    if (weather != null && "ok".equals(weather.status)) {
                        var editor = PreferenceManager.getDefaultSharedPreferences(this@WeatherActivity).edit()
                        editor.putString("weather",responseText);
                        editor.apply()
                        showWeatherInfo(weather)

                    }else{
                        Toast.makeText(this@WeatherActivity,"获取天气信息失败",Toast.LENGTH_SHORT).show()
                    }

                    swipeRefresh.isRefreshing = false
                }

            }

            override fun onFailure(call: Call?, e: IOException?) {
                e?.printStackTrace()
                runOnUiThread{
                    Toast.makeText(this@WeatherActivity,"获取天气信息失败",Toast.LENGTH_SHORT).show()
                    swipeRefresh.isRefreshing = false
                }
            }
        })

        loadBingPic()
    }

    fun showWeatherInfo(weather: Weather){
        titleCity.text = weather.basic!!.cityName
        titleUpdateTime.text = weather.basic!!.update!!.updateTime!!.split(" ")[1]
        degreeText.text = weather.now!!.temperature + "℃"
        weatherInfoText.text = weather.now!!.more!!.info
        forcastLayout.removeAllViews()

        for(forcast in weather.forecastList!!){
            var view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forcastLayout,false)
            val dateText1 = view.findViewById<View>(R.id.dateText) as TextView
            val infoText1 = view.findViewById<View>(R.id.infoText) as TextView
            val maxText1 = view.findViewById<View>(R.id.maxText) as TextView
            val minText1 = view.findViewById<View>(R.id.minText) as TextView
            dateText1.text = forcast.date
            infoText1.text = forcast.more!!.info
            maxText1.text = forcast.temperature!!.max
            minText1.text = forcast.temperature!!.min

            forcastLayout.addView(view)
        }

        if (weather.aqi != null){
            aqiText.text = weather.aqi!!.city!!.aqi
            pm25Text.text = weather.aqi!!.city!!.pm25
        }

        comfortText.text = "舒适度：" + weather.suggestion!!.comfort!!.info
        carWashText.text = "洗车指数：" + weather.suggestion!!.carWash!!.info
        sportText.text = "运动建议：" + weather.suggestion!!.sport!!.info
        weatherLayout.visibility = View.VISIBLE

        val intent = Intent(this, AutoUpdateService::class.java)
        startActivity(intent)

    }

    fun loadBingPic(){
        var requestBingPic = "http://guolin.tech/api/bing_pic"
        HttpUtil.sendOkHttpRequest(requestBingPic,object: okhttp3.Callback{
            override fun onResponse(call: Call?, response: Response?) {
                var bingPic = response!!.body()!!.string()
                var editor = PreferenceManager.getDefaultSharedPreferences(this@WeatherActivity).edit()
                editor.putString("bing_pic",bingPic)
                editor.apply()
                runOnUiThread{
                    Glide.with(this@WeatherActivity).load(bingPic).into(bingPicImg)
                }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                e?.printStackTrace()
            }
        })
    }
}