package com.capstone.valoai.features.dashboard.presentations

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.capstone.valoai.R
import com.capstone.valoai.commons.ApiConfig
import com.capstone.valoai.commons.Status
import com.capstone.valoai.commons.navigateTo
import com.capstone.valoai.databinding.ActivityDashboardBinding
import com.capstone.valoai.features.auth.presentation.login.LoginActivity
import com.capstone.valoai.features.dashboard.data.models.RiwayatVaksin
import com.capstone.valoai.features.dashboard.domain.adapter.FakesListAdapter
import com.capstone.valoai.features.dashboard.domain.adapter.FakesListAdapter.OnItemClickCallback
import com.capstone.valoai.features.dashboard.domain.adapter.RiwayatListAdapter
import com.capstone.valoai.features.detail_faskes.data.models.FaskesModel
import com.capstone.valoai.features.detail_faskes.presentation.DetailFaskesActivity
import com.capstone.valoai.features.maps.data.FaskesRepository
import com.capstone.valoai.features.maps.domain.usecase.FaskesViewModel
import com.capstone.valoai.features.maps.domain.usecase.ViewModelFactory
import com.capstone.valoai.features.maps.presentation.VaccineLocationMapsActivity
import com.capstone.valoai.features.profile.data.remote.UserDataSourceRemote
import com.capstone.valoai.features.profile.domain.vmodel.ProfileViewModel
import com.capstone.valoai.features.profile.presentations.ProfileActivity
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RelativeCornerSize
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var user: FirebaseUser? = null
    private var vaksinType: String = "AZ"

    private lateinit var viewModel: FaskesViewModel


    override fun onResume() {
        with(binding) {
            txtName.text = user?.displayName
            Glide.with(baseContext).load(user?.photoUrl).circleCrop().into(profileDashboard)
        }

        super.onResume()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        this.title = "Dashboard"
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        attachFakesList()
        setContentView(binding.root)

        val bottomAppBar = binding.bottomAppBar
        val bottomBarBackground = bottomAppBar.background as MaterialShapeDrawable
        bottomBarBackground.shapeAppearanceModel = bottomBarBackground.shapeAppearanceModel
            .toBuilder()
            .setAllCorners(RoundedCornerTreatment()).setAllCornerSizes(RelativeCornerSize(0.1f))
            .build()

        firebaseAuth = Firebase.auth
        user = firebaseAuth.currentUser

        with(binding) {
            bottomNavigationView.setOnItemSelectedListener { item ->
                when (item.title) {
                    "Home" -> attachFakesList()
                    "Riwayat" -> attachHistoryList()
                }
                true
            }

            fab.setOnClickListener { onClickFloatingBtn() }

            profileDashboard.setOnClickListener {
                startActivity(Intent(this@DashboardActivity, ProfileActivity::class.java))
            }

            txtName.text = user?.displayName
            Glide.with(baseContext).load(user?.photoUrl).circleCrop().into(profileDashboard)
        }


    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener {
            if (it.currentUser == null) {
                navigateTo(this@DashboardActivity, LoginActivity::class.java)
            }
        }
    }

    private fun attachFakesList() {
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(
                    FaskesRepository(
                        ApiConfig.faskesService,
                        ApiConfig.recommendationService
                    )
                )
            )[FaskesViewModel::class.java]

        showProgressBar()
        viewModel.getAllFaskes(vaksinType).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.titleList.setText(R.string.rekomendasi_list)
                        val adapter = FakesListAdapter(resource.data ?: ArrayList(), baseContext)
                        with(binding) {
                            dashboardList.layoutManager = layoutManager
                            adapter.setOnItemClickCallback(object : OnItemClickCallback {
                                override fun onItemClicked(data: FaskesModel) {
                                    goToDetailFakes(data)
                                }
                            })
                            dashboardList.adapter = adapter
                        }
                        hideProgressBar()
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

    private fun goToDetailFakes(data: FaskesModel) {
        val intentToDetail = Intent(this@DashboardActivity, DetailFaskesActivity::class.java)
        intentToDetail.apply {
            putExtra(
                DetailFaskesActivity.FASKES_EXTRA_NAME,
                data
            )
        }
        startActivity(intentToDetail)
    }

    private val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    private fun attachHistoryList() {
        showProgressBar()
        binding.titleList.setText(R.string.riwayat_list)
        user?.let { it ->
            val dataSource = UserDataSourceRemote(it)
            val viewModel = ProfileViewModel(dataSource)

            viewModel.getProfile().observe(this) { data ->
                if (data.status == Status.SUCCESS) {
                    Log.println(Log.INFO, "test", data.data?.vaksin1 ?: "none")

                    vaksinType = data.data?.vaksin1 ?: "AZ"

                    val vaksin1 = RiwayatVaksin(
                        data.data?.vaksin1 ?: "",
                        data.data?.tanggal_vaksin1 ?: "",
                        "1"
                    )
                    val vaksin2 = RiwayatVaksin(
                        data.data?.vaksin2 ?: "",
                        data.data?.tanggal_vaksin2 ?: "",
                        "2"
                    )
                    val vaksin3 = RiwayatVaksin(
                        data.data?.vaksin3 ?: "",
                        data.data?.tanggal_vaksin3 ?: "",
                        "3"
                    )
                    val adapter = RiwayatListAdapter(arrayListOf(vaksin1, vaksin2, vaksin3))

                    with(binding) {
                        dashboardList.layoutManager = layoutManager
                        adapter.setOnItemClickCallback(object :
                            RiwayatListAdapter.OnItemClickCallback {
                            override fun onItemClicked(data: RiwayatVaksin) {

                            }
                        })
                        dashboardList.adapter = adapter
                        hideProgressBar()
                    }
                }
            }
        }
    }

    private fun onClickFloatingBtn() {
        startActivity(
            Intent(
                this@DashboardActivity,
                VaccineLocationMapsActivity::class.java
            ).apply { putExtra("vaccineType", vaksinType) })
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }
}