package com.capstone.valoai.features.maps.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.capstone.valoai.R
import com.capstone.valoai.commons.ApiConfig
import com.capstone.valoai.commons.Status
import com.capstone.valoai.commons.hideProgressBar
import com.capstone.valoai.commons.showProgressBar
import com.capstone.valoai.databinding.ActivityVaksinLocationMapsBinding
import com.capstone.valoai.features.detail_faskes.data.models.FaskesModel
import com.capstone.valoai.features.detail_faskes.presentation.DetailFaskesActivity
import com.capstone.valoai.features.maps.data.FaskesRepository
import com.capstone.valoai.features.maps.domain.usecase.FaskesViewModel
import com.capstone.valoai.features.maps.domain.usecase.ViewModelFactory
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.concurrent.TimeUnit


class VaccineLocationMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityVaksinLocationMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var viewModel: FaskesViewModel
    private lateinit var locationRequest: LocationRequest

    private var vaccineType: String = "AZ"


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

        if(intent != null){
            vaccineType = intent.getStringExtra("vaccineType") as String
        }

        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(FaskesRepository(ApiConfig.faskesService, ApiConfig.recommendationService))
            )[FaskesViewModel::class.java]


        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this@VaccineLocationMapsActivity)


        binding = ActivityVaksinLocationMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.back.setOnClickListener {
            finish()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getMyLocation()
        createLocationRequest()
        mMap.uiSettings.isZoomControlsEnabled = true

        viewModel.getAllFaskes(vaccineType).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        hideProgressBar(binding.mapsLoading)
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
                        hideProgressBar(binding.mapsLoading)
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        showProgressBar(binding.mapsLoading)
                    }
                }
            }
        }



        mMap.setOnInfoWindowClickListener {
            val detailFaskesIntent =
                Intent(
                    this@VaccineLocationMapsActivity,
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
            Toast.makeText(
                this@VaccineLocationMapsActivity,
                e.localizedMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val resolutionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            when (result.resultCode) {
                RESULT_OK ->
                    getMyLocation()
                RESULT_CANCELED ->
                    Toast.makeText(
                        this@VaccineLocationMapsActivity,
                        getString(R.string.gps_message),
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getMyLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Toast.makeText(
                            this@VaccineLocationMapsActivity,
                            sendEx.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

}