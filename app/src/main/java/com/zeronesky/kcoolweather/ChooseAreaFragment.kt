package com.zeronesky.kcoolweather


import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.zeronesky.kcoolweather.db.City
import com.zeronesky.kcoolweather.db.County
import com.zeronesky.kcoolweather.db.Province
import com.zeronesky.kcoolweather.util.HttpUtil
import com.zeronesky.kcoolweather.util.Utility
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.choose_area.*
import okhttp3.Call
import okhttp3.Response
import org.litepal.crud.DataSupport
import java.io.IOException

class ChooseAreaFragment : Fragment() {

    companion object {
        const val LEVEL_PROVINCE = 0
        const val LEVEL_CITY = 1
        const val LEVEL_COUNTY = 2
    }

    private var progressDialog: ProgressDialog? = null

    private var adapter: ArrayAdapter<String>? = null
    private var dataList = ArrayList<String?>()
    private var provinceList: List<Province>? = null
    private var cityList: List<City>? = null
    private var countyList: List<County>? = null

    private var selectProvince: Province? = null
    private var selectedCity: City? = null
    private var currentLevel: Int? = -1

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.choose_area, container, false)
    }

    //fragment 经常要重新创建，所以 列表在此赋值
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, dataList)
        listView!!.adapter = adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            when (currentLevel) {
                LEVEL_PROVINCE -> {
                    selectProvince = provinceList!![position]
                    queryCities()
                }
                LEVEL_CITY -> {
                    selectedCity = cityList!![position]
                    queryCounties()
                }
                LEVEL_COUNTY -> {
                    var weatherId = countyList!![position].weatherId
                    if(activity is MainActivity) {
                        var intent = Intent(activity, WeatherActivity::class.java)
                        intent.putExtra("weather_id", weatherId)
                        startActivity(intent)
                        activity.finish()
                    }else if(activity is WeatherActivity){
                        val activity = activity as WeatherActivity
                        activity.drawerLayout.closeDrawers()
                        activity.swipeRefresh.isRefreshing = true
                        activity.requestWeather(weatherId!!)
                    }
                }
            }
        }

        backButton.setOnClickListener {
            when (currentLevel) {
                LEVEL_COUNTY -> queryCities()
                LEVEL_CITY -> queryProvinces()
            }
        }

        queryProvinces()
    }

    fun queryProvinces() {
        titleText.text = "中国"
        backButton.visibility = View.GONE

        provinceList = DataSupport.findAll(Province::class.java)
        if (provinceList!!.size > 0) {
            dataList.clear()
            for (province in provinceList!!) {
                dataList.add(province.provinceName)
            }

            adapter!!.notifyDataSetChanged()
            listView.setSelection(0)
            currentLevel = LEVEL_PROVINCE

        } else {
            val address = "http://guolin.tech/api/china"
            queryFromServer(address, "province")
        }

    }

    fun queryCities() {
        titleText.text = selectProvince!!.provinceName
        backButton.visibility = View.VISIBLE
        cityList = DataSupport.where("provinceid = ?", selectProvince!!.id.toString()).find(City::class.java)

        if (cityList!!.size > 0) {
            dataList.clear()
            for (city in cityList!!) {
                dataList.add(city.cityName)
            }
            adapter!!.notifyDataSetChanged()
            listView.setSelection(0)
            currentLevel = LEVEL_CITY
        } else {
            val provinceCode = selectProvince!!.provinceCode
            val address = "http://guolin.tech/api/china/$provinceCode"
            queryFromServer(address, "city")
        }
    }

    fun queryCounties() {
        titleText.text = selectedCity!!.cityName
        backButton.visibility = View.VISIBLE
        countyList = DataSupport.where("cityid = ?", selectedCity!!.id.toString()).find(County::class.java)

        if (countyList!!.size > 0) {
            dataList.clear()
            for (county in countyList!!) {
                dataList.add(county.countyName)
            }

            adapter!!.notifyDataSetChanged()
            listView.setSelection(0)
            currentLevel = LEVEL_COUNTY

        } else {
            val provinceCode = selectProvince!!.provinceCode
            val cityCode = selectedCity!!.cityCode
            val address = "http://guolin.tech/api/china/$provinceCode/$cityCode"
            queryFromServer(address, "county")
        }
    }

    fun queryFromServer(address: String, type: String) {
        showProgressDialog()
        HttpUtil.sendOkHttpRequest(address, object : okhttp3.Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                activity.runOnUiThread {
                    closeProgressDialog()
                    Toast.makeText(context, "加载失败", Toast.LENGTH_SHORT).show()
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call?, response: Response?) {
                var responseText = response!!.body()!!.string()

                var result = when(type){
                    "province" -> Utility.handleProvinceResponse(responseText!!)
                    "city" -> Utility.handleCityResponse(responseText!!, selectProvince!!.id)
                    "county" -> Utility.handleCountyResponse(responseText!!, selectedCity!!.id)
                    else -> false
                }

                if (result) {
                    activity.runOnUiThread {
                        closeProgressDialog()
                           when (type){
                               "province" -> queryProvinces()
                               "city" ->  queryCities()
                               "county" -> queryCounties()
                           }
                    }
                }
            }
        })
    }


    fun showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(activity)
            progressDialog!!.setMessage("正在加载。。。")
            progressDialog!!.setCanceledOnTouchOutside(false);
        }
        progressDialog!!.show()
    }

    fun closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }
    }


}
