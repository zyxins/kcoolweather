package com.zeronesky.kcoolweather.db

import org.litepal.crud.DataSupport

/**
 * Created by zyxins on 05/03/2018.
 */
class County: DataSupport() {
    var id: Int = 0
    var countyName: String? = ""
    var weatherId: String? = ""
    var cityId: Int = 0
}