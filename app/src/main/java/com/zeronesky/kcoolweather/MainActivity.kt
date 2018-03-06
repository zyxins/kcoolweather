package com.zeronesky.kcoolweather

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getString("weather",null) != null) {
            var intent = Intent(this,WeatherActivity::class.java)
            startActivity(intent)
            finish()

        }
    }
}
