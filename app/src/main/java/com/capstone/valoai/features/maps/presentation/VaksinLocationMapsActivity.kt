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
import com.capstone.valoai.features.maps.usecase.FaskesViewModel
import com.capstone.valoai.features.maps.usecase.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class VaksinLocationMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityVaksinLocationMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var viewModel: FaskesViewModel

    private val listFaskes = listOf(
        FaskesModel(
            1,
            "Puskesmas Puspahiang",
            "-7.408113733",
            "108.0493673",
            listOf("Sinovac", "Moderna")
        ),
        FaskesModel(
            2,
            "Klinik Polres Tasikmalaya",
            "-7.302484741",
            "108.1960569",
            listOf("Sinovac", "Moderna")
        ),
        FaskesModel(
            1,
            "Puskesmas Sukarame",
            "-7.356706189",
            "108.1334107",
            listOf("Sinovac", "Moderna")
        ),
        FaskesModel(
            1,
            "Klinik Zarra Medika",
            "-7.341704682",
            "108.1359896",
            listOf("Sinovac", "Moderna")
        ),
        FaskesModel(
            1,
            "Puskesmas Puspahiang",
            "-7.408113733",
            "108.0493673",
            listOf("Sinovac", "Moderna")
        ),
        FaskesModel(
            1,
            "Puskesmas Puspahiang",
            "-7.408113733",
            "108.0493673",
            listOf("Sinovac", "Moderna")
        ),
        FaskesModel(
            1,
            "Puskesmas Puspahiang",
            "-7.408113733",
            "108.0493673",
            listOf("Sinovac", "Moderna")
        ),
        FaskesModel(
            1,
            "Puskesmas Puspahiang",
            "-7.408113733",
            "108.0493673",
            listOf("Sinovac", "Moderna")
        ),
        FaskesModel(
            5,
            "Puskesmas Leuwisari",
            "-7.335137899",
            "108.1012665",
            listOf("Sinovac", "Moderna", "Pfizer", "dll")
        ),
        FaskesModel(
            6,
            "Puskesmas Mangunreja",
            "-7.36420436",
            "108.102777",
            listOf("Sinovac", "Moderna", "Pfizer", "dll")
        ),
        FaskesModel(
            7,
            "Puskesmas Singaparna",
            "-7.349938804",
            "108.111067",
            listOf("Sinovac", "Moderna", "Pfizer", "dll")
        ),
        FaskesModel(
            8,
            "Klinik Arafa",
            "-7.345872189",
            "108.1217619",
            listOf("Sinovac", "Moderna", "Pfizer", "dll")
        ),
        FaskesModel(
            9,
            "Puskesmas Tineuwati",
            "-7.353155725",
            "108.1049154",
            listOf("Sinovac", "Moderna", "Pfizer", "dll")
        ),
        FaskesModel(
            10,
            "RSUD SMC",
            "-7.357325058",
            "108.1061439",
            listOf("Sinovac", "Moderna", "Pfizer", "dll")
        ),
        FaskesModel(
            11,
            "Puskesmas Sariawangi",
            "-7.324530971",
            "108.0775018",
            listOf("Sinovac", "Moderna", "Pfizer", "dll")
        ),
        FaskesModel(
            12,
            "Puskesmas Cigalontang",
            "-7.34650908",
            "108.0332538",
            listOf("Sinovac", "Moderna", "Pfizer", "dll")
        ),
        FaskesModel(
            13,
            "Puskesmas Salawu",
            "-7.37338144",
            "108.0381755",
            listOf("Sinovac", "Moderna", "Pfizer", "dll")
        ),
        FaskesModel(
            14,
            "Puskesmas Sukahening",
            "-7.199670074",
            "108.1515353",
            listOf("Sinovac", "Moderna", "Pfizer", "dll")
        ),
        FaskesModel(
            15,
            "Klinik BMC Ciawi",
            "-7.153757751",
            "108.1452093",
            listOf("Sinovac", "Moderna", "Pfizer", "dll")
        ),
        FaskesModel(
            16,
            "Puskesmas Jamanis",
            "-7.182358033",
            "108.1816185",
            listOf("Sinovac", "Moderna", "Pfizer", "dll")
        ),
        FaskesModel(
            17,
            "Puskesmas Rajapolah",
            "-7.208842423",
            "108.1891716",
            listOf("Sinovac", "Moderna", "Pfizer", "dll")
        ),
        FaskesModel(
            18,
            "Al-Idrisiyyah Medical Center",
            "-7.263403696",
            "108.1866048",
            listOf("Sinovac", "Moderna", "Pfizer", "dll")
        ),
    )


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
                            Log.i("Fetch API", "Fetch Data Faskes : $list")
                            list.forEach { faskes ->
                                val location =
                                    LatLng(faskes.latitude.toDouble(), faskes.longitude.toDouble())
                                mMap.addMarker(
                                    MarkerOptions().position(location).title(faskes.name)
                                )
                                mMap.setOnInfoWindowClickListener {
                                    val detailFaskesIntent =
                                        Intent(
                                            this@VaksinLocationMapsActivity,
                                            DetailFaskesActivity::class.java
                                        )
                                    detailFaskesIntent.apply {
                                        putExtra(
                                            DetailFaskesActivity.FASKES_EXTRA_NAME,
                                            faskes
                                        )
                                    }
                                    startActivity(detailFaskesIntent)
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


}