package com.zeronesky.kcoolweather.db

import org.litepal.crud.DataSupport

/**
 * Created by zyxins on 05/03/2018.
 */
class City: DataSupport() {
    var id: Int = 0
    var cityName: String? = ""
    var cityCode: Int = 0
    var provinceId: Int = 0
}