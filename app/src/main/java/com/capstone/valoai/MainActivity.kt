package com.capstone.valoai

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.valoai.features.maps.presentation.VaksinLocationMapsActivity
import com.capstone.valoai.features.onboarding.data.local.OnBoardPref
import com.capstone.valoai.features.onboarding.data.local.datastore
import com.capstone.valoai.features.onboarding.presentation.OnBoardingActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val onBoardPref: OnBoardPref by lazy {
        OnBoardPref(this.datastore)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            onBoardPref.launchStatus.collectLatest { status ->
                if(status) {
                    val intent = Intent(this@MainActivity, OnBoardingActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    onBoardPref.updateIsFirstLaunchedToFalse()
                    finish()
                }
            }
        }
        setContentView(R.layout.activity_detail_vaksin)

        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            finish()
        }


        findViewById<TextView>(R.id.faskes_title).setOnClickListener {
            startActivity(Intent(this@MainActivity, VaksinLocationMapsActivity::class.java))
        }
    }
}