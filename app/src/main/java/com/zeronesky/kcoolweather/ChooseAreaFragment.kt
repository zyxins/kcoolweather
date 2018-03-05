package com.zeronesky.kcoolweather


import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.zeronesky.kcoolweather.R.layout.choose_area
import com.zeronesky.kcoolweather.db.City
import com.zeronesky.kcoolweather.db.County
import com.zeronesky.kcoolweather.db.Province
import com.zeronesky.kcoolweather.util.HttpUtil
import com.zeronesky.kcoolweather.util.Utility
import kotlinx.android.synthetic.main.choose_area.*
import okhttp3.Call
import okhttp3.Response
import org.litepal.crud.DataSupport
import java.io.IOException
import java.text.FieldPosition
import javax.security.auth.callback.Callback


/**
 * A simple [Fragment] subclass.
 */
class ChooseAreaFragment : Fragment() {

    companion object {
        val LEVEL_PROVINCE = 0
        val LEVEL_CITY = 1
        val LEVEL_COUNTY = 2

    }

    var progressDialog: ProgressDialog? = null

    var adapter:ArrayAdapter<String>? = null
    var dataList = ArrayList<String?>()
    var provinceList: List<Province>? = null
    var cityList: List<City>? = null
    var countyList: List<County>? = null

    var selectProvince: Province? = null
    var selectedCity: City? = null
    var currentLevel: Int? = -1

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        adapter = ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,dataList)
        listView.adapter = adapter
        return chooseArea
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView.onItemClickListener = AdapterView.OnItemClickListener{
             _, _, position, _ ->
                 when(currentLevel){
                     LEVEL_PROVINCE -> {
                         selectProvince = provinceList!!.get(position)
                     }
                     LEVEL_CITY -> {
                         selectedCity = cityList!!.get(position)
                     }
                 }
        }

        backButton.setOnClickListener{
            when(currentLevel){
                LEVEL_COUNTY -> {}
                LEVEL_CITY -> {}
            }
        }
    }

        fun queryProvinces(){
            titleText.text = "中国"
            backButton.visibility = View.GONE
            provinceList = DataSupport.findAll(Province::class.java)

            if (provinceList!!.size > 0){
                dataList.clear()
                for (province in provinceList!!){
                    dataList.add(province.provinceName)

                }

                adapter!!.notifyDataSetChanged()
                listView.setSelection(0)
                currentLevel = LEVEL_PROVINCE

            }else{
                val address = "http://guolin.tech/api/china"
                //todo
            }

        }


        fun queryFromServer( address: String, type: String){
            showProgressDialog()

           /* HttpUtil.sendOkHttpRequest(address,callback = class Obj:Callback(){

            })*/



        }



        fun showProgressDialog(){
            if ( progressDialog== null) {
                progressDialog = ProgressDialog(activity)
                progressDialog!!.setMessage("正在加载。。。")
                progressDialog!!.setCanceledOnTouchOutside(false);
            }
        }

        fun closeProgressDialog(){
            if (progressDialog != null){
                progressDialog!!.dismiss()
            }
        }


    inner class OCallback(var type: String): okhttp3.Callback{

        override fun onFailure(call: Call?, e: IOException?) {
            activity.runOnUiThread {
                closeProgressDialog()
                Toast.makeText(context,"加载失败",Toast.LENGTH_SHORT).show()
            }
        }

        override fun onResponse(call: Call?, response: Response?) {
            var responseText = response?.body().toString()
            var result:Boolean =  when(type)
            {
                "province" -> Utility.handleProvinceResponse(responseText)
                "city" -> Utility.handleCityResponse(responseText,selectProvince!!.id)
                "county" -> Utility.handleCountyResponse(responseText,selectedCity!!.id)
                else -> false

            }

            if(result){
                activity.runOnUiThread{
                    closeProgressDialog()
                    when (type){
                        "province" -> queryProvinces()
                        "city" -> queryProvinces()
                        "couty" ->queryProvinces()
                    }
                }
            }
        }
    }

}// Required empty public constructor
