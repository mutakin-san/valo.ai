package com.capstone.valoai.features.dashboard.presentations

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.capstone.valoai.commons.ApiConfig
import com.capstone.valoai.commons.Status
import com.capstone.valoai.databinding.ActivityDashboardBinding
import com.capstone.valoai.features.auth.domain.usecases.UserServices
import com.capstone.valoai.features.auth.presentation.login.LoginActivity
import com.capstone.valoai.features.dashboard.domain.adapter.FakesListAdapter
import com.capstone.valoai.features.dashboard.domain.adapter.FakesListAdapter.OnItemClickCallback
import com.capstone.valoai.features.dashboard.domain.adapter.RiwayatListAdapter
import com.capstone.valoai.features.detail_faskes.data.models.FaskesModel
import com.capstone.valoai.features.detail_faskes.presentation.DetailFaskesActivity
import com.capstone.valoai.features.maps.data.FaskesRepository
import com.capstone.valoai.features.maps.domain.usecase.FaskesViewModel
import com.capstone.valoai.features.maps.domain.usecase.ViewModelFactory
import com.capstone.valoai.features.maps.presentation.VaksinLocationMapsActivity
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RelativeCornerSize
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val db by lazy { FirebaseFirestore.getInstance() }
    private var user: FirebaseUser? = null

    private lateinit var viewModel: FaskesViewModel


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
                firebaseAuth.signOut()
            }

            txtName.text = user?.displayName
            Glide.with(baseContext).load(user?.photoUrl).circleCrop().into(profileDashboard)
        }


        user?.let {
            UserServices.getDataUser(it.uid, db){ user ->
                Log.i(DashboardActivity::class.simpleName, "Data User = ${user.name}")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener {
            if (it.currentUser == null) {
                startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                finish()
                return@addAuthStateListener
            }
        }
    }

    private fun attachFakesList() {
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(FaskesRepository(ApiConfig.faskesService))
            )[FaskesViewModel::class.java]

        viewModel.getAllFaskes().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
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
        val intentToDetail =
            Intent(this@DashboardActivity, DetailFaskesActivity::class.java)
        intentToDetail.apply {
            putExtra(
                DetailFaskesActivity.FASKES_EXTRA_NAME,
                data
            )
        }
        startActivity(intentToDetail)
    }

    private fun attachHistoryList() {
        val dataDummy = arrayListOf("Test3", "Test5")
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = RiwayatListAdapter(dataDummy)
        with(binding) {
            dashboardList.layoutManager = layoutManager
            adapter.setOnItemClickCallback(object : RiwayatListAdapter.OnItemClickCallback {
                override fun onItemClicked(data: String) {
                }
            })
            dashboardList.adapter = adapter
        }
    }

    private fun onClickFloatingBtn() {
        startActivity(Intent(this@DashboardActivity, VaksinLocationMapsActivity::class.java))
    }
}