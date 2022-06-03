package com.capstone.valoai.features.detail_faskes.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.valoai.databinding.ActivityDetailFaskesBinding
import com.capstone.valoai.features.detail_faskes.data.models.FaskesModel
import com.capstone.valoai.features.maps.presentation.VaksinLocationMapsActivity

class DetailFaskesActivity : AppCompatActivity() {
    private var faskesData: FaskesModel? = null

    private lateinit var binding: ActivityDetailFaskesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailFaskesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        faskesData = intent.getParcelableExtra(FASKES_EXTRA_NAME)


        binding.faskesTitle.text = faskesData?.name
        binding.faskesName.text = faskesData?.name
        binding.btnShowRute.setOnClickListener {
            val mapsRuteUri =
                Uri.parse("google.navigation:q=${faskesData?.latitude},${faskesData?.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, mapsRuteUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.faskesTitle.setOnClickListener {
            startActivity(Intent(this@DetailFaskesActivity, VaksinLocationMapsActivity::class.java))
        }
    }


    companion object{
        const val FASKES_EXTRA_NAME = "faskes data"
    }
}