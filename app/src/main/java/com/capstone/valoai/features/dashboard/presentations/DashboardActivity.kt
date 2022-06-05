package com.capstone.valoai.features.dashboard.presentations

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.capstone.valoai.databinding.ActivityDashboardBinding
import com.capstone.valoai.features.auth.presentation.login.LoginActivity
import com.capstone.valoai.features.dashboard.domain.adapter.FikesListAdapter
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

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName("NoerSy")
            .setPhotoUri(Uri.parse("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png"))
            .build()

        user?.updateProfile(profileUpdates)?.addOnCompleteListener {
            binding.txtName.text = user?.displayName
            Glide.with(this).load(user?.photoUrl).circleCrop().into(binding.profileDashboard)
        }



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

    fun onClickFeb() {
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