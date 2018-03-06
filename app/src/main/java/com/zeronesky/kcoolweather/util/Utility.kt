package com.zeronesky.kcoolweather.util

import com.google.gson.Gson
import com.zeronesky.kcoolweather.db.City
import com.zeronesky.kcoolweather.db.County
import com.zeronesky.kcoolweather.db.Province
import com.zeronesky.kcoolweather.gson.Weather
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by zyxins on 05/03/2018.
 */
class Utility {
    companion object {
        fun handleProvinceResponse(response: String): Boolean = try {
                val allProvices = JSONArray(response)
                for (i in 0 until allProvices.length()) {
                    val provinceObject = allProvices.getJSONObject(i)
                    val province = Province()
                    province.provinceName = provinceObject.getString("name")
                    province.provinceCode = provinceObject.getInt("id")
                    province.save()
                }
                true
            } catch (e: JSONException) {
                e.printStackTrace()
                false
            }


        fun handleCityResponse(response: String, provinceId: Int): Boolean =  try {
                val allCities = JSONArray(response)
                for (i in 0 until allCities.length()) {
                    val cityObject = allCities.getJSONObject(i)
                    val city = City()
                    city.cityName = cityObject.getString("name")
                    city.cityCode = cityObject.getInt("id")
                    city.provinceId = provinceId
                    city.save()
                }
                true
            } catch (e: JSONException) {
                e.printStackTrace()
                false
            }


        fun handleCountyResponse(response: String, cityId: Int): Boolean =try {
                val allProvices = JSONArray(response)
                for (i in 0 until allProvices.length()) {
                    val countyObject = allProvices.getJSONObject(i)
                    val county = County()
                    county.countyName = countyObject.getString("name")
                    county.weatherId = countyObject.getString("weather_id")
                    county.cityId = cityId
                    county.save()
                }

                true
            } catch (e: JSONException) {
                e.printStackTrace()
                false
            }

        fun handleWeatherResponse(response: String): Weather? = try{
            var jsonObject = JSONObject(response)
            var jsonArray = jsonObject.getJSONArray("HeWeather")
            var weatherContent = jsonArray.getJSONObject(0).toString()

            Gson().fromJson(weatherContent, Weather::class.java)
        } catch (e: Exception){
            e.printStackTrace()
            null
        }

    }
}