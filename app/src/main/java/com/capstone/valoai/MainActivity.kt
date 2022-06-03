package com.capstone.valoai

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.valoai.databinding.ActivityMainBinding
import com.capstone.valoai.features.maps.presentation.VaksinLocationMapsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)


        binding.findVaccineLocation.setOnClickListener {
            startActivity(Intent(this@MainActivity, VaksinLocationMapsActivity::class.java))
        }

    }

}