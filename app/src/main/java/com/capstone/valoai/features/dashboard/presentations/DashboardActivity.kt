package com.capstone.valoai.features.dashboard.presentations

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.capstone.valoai.databinding.ActivityDashboardBinding
import com.capstone.valoai.features.auth.presentation.login.LoginActivity
import com.capstone.valoai.features.dashboard.domain.adapter.FakesListAdapter
import com.capstone.valoai.features.dashboard.domain.adapter.RiwayatListAdapter
import com.capstone.valoai.features.maps.presentation.VaksinLocationMapsActivity
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RelativeCornerSize
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest


class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        this.title = "Dashboard"
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        attachFikesList()
        setContentView(binding.root)

        val bottomAppBar = binding.bottomAppBar
        val bottomBarBackground = bottomAppBar.background as MaterialShapeDrawable
        bottomBarBackground.shapeAppearanceModel = bottomBarBackground.shapeAppearanceModel
            .toBuilder()
            .setAllCorners(RoundedCornerTreatment()).setAllCornerSizes(RelativeCornerSize(0.1f))
            .build()

        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser

        binding.txtName.text = user?.displayName
        Glide.with(this).load(user?.photoUrl).circleCrop().into(binding.profileDashboard)


        binding.bottomNavigationView.setOnItemSelectedListener{ item ->
//            Log.println(Log.INFO, "Test", "${item.title}")
            when (item.title) {
                "Home" -> attachFikesList()
                "Riwayat" -> attachHistoryList()
            }
            true
        }

        binding.fab.setOnClickListener {
            onClickFeb()
        }

        firebaseAuth.addAuthStateListener {
            if (it.currentUser == null) {
                startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                finish()
            }
        }

        binding.profileDashboard.setOnClickListener {
            firebaseAuth.signOut()
        }

    }

    private fun attachFikesList() {
        val dataDummy = arrayListOf("Test", "Test1")
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.dashboardList.layoutManager = layoutManager
        binding.dashboardList.adapter = FakesListAdapter(dataDummy)
    }

    private fun attachHistoryList() {
        val dataDummy = arrayListOf("Test3", "Test5")
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.dashboardList.layoutManager = layoutManager
        binding.dashboardList.adapter = RiwayatListAdapter(dataDummy)
    }

    fun onClickFeb() {
        startActivity(Intent(this@DashboardActivity, VaksinLocationMapsActivity::class.java))
    }
}