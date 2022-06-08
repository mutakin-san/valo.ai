package com.capstone.valoai.features.maps.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.capstone.valoai.R
import com.capstone.valoai.commons.ApiConfig
import com.capstone.valoai.commons.Status
import com.capstone.valoai.databinding.ActivityVaksinLocationMapsBinding
import com.capstone.valoai.features.detail_faskes.data.models.FaskesModel
import com.capstone.valoai.features.detail_faskes.presentation.DetailFaskesActivity
import com.capstone.valoai.features.maps.data.FaskesRepository
import com.capstone.valoai.features.maps.domain.usecase.FaskesViewModel
import com.capstone.valoai.features.maps.domain.usecase.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class VaksinLocationMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var tflite: Interpreter
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityVaksinLocationMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var viewModel: FaskesViewModel

    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLocation()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadModel()
        launchRecommendation("Sinovac","Sinovac")
        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(FaskesRepository(ApiConfig.faskesService))
            )[FaskesViewModel::class.java]


        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this@VaksinLocationMapsActivity)


        binding = ActivityVaksinLocationMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.back.setOnClickListener {
            finish()
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getMyLocation()

        viewModel.getAllFaskes().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { list ->
                            list.forEach { faskes ->
                                val location =
                                    LatLng(faskes.latitude.toDouble(), faskes.longitude.toDouble())
                                mMap.addMarker(
                                    MarkerOptions().position(location).title(faskes.name)
                                )?.apply {
                                    tag = faskes
                                }
                            }

                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
//                        progressBar.visibility = View.VISIBLE
//                        recyclerView.visibility = View.GONE
                    }
                }
            }
        }



        mMap.setOnInfoWindowClickListener {
            val detailFaskesIntent =
                Intent(
                    this@VaksinLocationMapsActivity,
                    DetailFaskesActivity::class.java
                )

            detailFaskesIntent.apply {
                putExtra(
                    DetailFaskesActivity.FASKES_EXTRA_NAME,
                    it.tag as FaskesModel
                )
            }
            startActivity(detailFaskesIntent)
        }
    }


    @SuppressLint("MissingPermission")
    private fun getMyLocation() {
        try {

            if (checkSelfPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mMap.isMyLocationEnabled = true
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val myLocation = LatLng(location.latitude, location.longitude)
                        mMap.addCircle(
                            CircleOptions()
                                .center(myLocation)
                                .radius(10000.0)
                                .strokeColor(getColor(R.color.blue_primary_secondary))
                        )
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 10F))
                        // TODO: Rekomendasi tempat terdekat dengan lokasi saat ini
                    }
                }

            } else {
                requestLocationPermission.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        } catch (e: Exception) {

        }
    }


    private fun launchRecommendation(vac1: String, vac2: String) {
        try {
            tflite.run(vac1, 1f)
            tflite.getOutputTensor(0)
        }catch (e: java.lang.Exception){
            Log.d("Error" , e.localizedMessage)
        }
    }

    private fun loadModel() {
        try {
            loadModelFile()?.let {
                tflite =  Interpreter(it)
            }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun loadModelFile(): MappedByteBuffer? {
        val fileDescriptor = this.assets.openFd("vac.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declareLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declareLength)
    }

}