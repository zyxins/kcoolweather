package com.zeronesky.kcoolweather.util

import com.zeronesky.kcoolweather.db.City
import com.zeronesky.kcoolweather.db.County
import com.zeronesky.kcoolweather.db.Province
import org.json.JSONArray
import org.json.JSONException

/**
 * Created by zyxins on 05/03/2018.
 */
class Utility {
    companion object {
        fun handleProvinceResponse(response: String): Boolean {
            return try {
                val allProvices = JSONArray(response)
                for (i in 0..allProvices.length()) {
                    val provinceObject = allProvices.getJSONObject(i)
                    val province = Province()
                    province.provinceName = provinceObject.getString("name")
                    province.provinceCode = provinceObject.getInt("id")
                    province.save()
                }
                true
            } catch (e: JSONException) {
                false
            }
        }

        fun handleCityResponse(response: String, provinceId: Int): Boolean {
            return try {
                val allCities = JSONArray(response)
                for (i in 0..allCities.length()) {
                    val cityObject = allCities.getJSONObject(i)
                    val city = City()
                    city.cityName = cityObject.getString("name")
                    city.cityCode = cityObject.getInt("id")
                    city.provinceId = provinceId
                    city.save()
                }
                true
            } catch (e: JSONException) {
                false
            }
        }

        fun handleCountyResponse(response: String, cityId: Int): Boolean {
            return try {
                val allProvices = JSONArray(response)
                for (i in 0..allProvices.length()) {
                    val countyObject = allProvices.getJSONObject(i)
                    val county = County()
                    county.countyName = countyObject.getString("name")
                    county.weatherId = countyObject.getString("weather_id")
                    county.cityId = cityId
                    county.save()
                }
                true
            } catch (e: JSONException) {
                false
            }
        }
    }
}