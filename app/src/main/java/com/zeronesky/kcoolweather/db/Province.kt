package com.zeronesky.kcoolweather.db

import org.litepal.crud.DataSupport

/**
 * Created by zyxins on 05/03/2018.
 */
class Province: DataSupport() {
    var id: Int =0
    var provinceName: String? = ""
    var provinceCode: Int = 0

}

