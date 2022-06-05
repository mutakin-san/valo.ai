package com.capstone.valoai.features.dashboard.presentations

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.valoai.databinding.ActivityDashboardBinding
import com.capstone.valoai.features.dashboard.domain.adapter.FikesListAdapter
import com.capstone.valoai.features.maps.presentation.VaksinLocationMapsActivity
import com.bumptech.glide.Glide
import com.capstone.valoai.features.auth.presentation.login.LoginActivity
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RelativeCornerSize
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


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
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val bottomAppBar = binding.bottomAppBar
        val bottomBarBackground = bottomAppBar.background as MaterialShapeDrawable
        bottomBarBackground.shapeAppearanceModel = bottomBarBackground.shapeAppearanceModel
            .toBuilder()
            .setAllCorners(RoundedCornerTreatment()).setAllCornerSizes(RelativeCornerSize(0.1f))
            .build()

        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser

        firebaseAuth.addAuthStateListener {
            if (it.currentUser == null) {
                startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                finish()
            }
        }

    }

    private fun attachFikesList() {
        val dataDummy = arrayListOf("Test", "Test1")
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.dashboardList.layoutManager = layoutManager
        binding.dashboardList.adapter = FikesListAdapter(dataDummy)
    }

    fun onClickFeb(view: View) {
        print("test")
        startActivity(Intent(this@DashboardActivity, VaksinLocationMapsActivity::class.java))

        binding.txtName.text = user?.displayName
        Glide.with(this@DashboardActivity).load(user?.photoUrl).into(binding.profileDashboard)
        binding.profileDashboard.setOnClickListener {
            firebaseAuth.signOut()
        }
        binding.fab.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, VaksinLocationMapsActivity::class.java))
        }
    }
}