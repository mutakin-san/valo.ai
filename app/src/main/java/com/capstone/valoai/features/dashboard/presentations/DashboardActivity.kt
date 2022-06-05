package com.capstone.valoai.features.dashboard.presentations

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.valoai.R
import com.capstone.valoai.features.auth.presentation.login.LoginActivity
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RelativeCornerSize
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.firebase.auth.FirebaseAuth


class DashboardActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onStart() {
        super.onStart()
        firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.addAuthStateListener {
            if(it.currentUser == null){
                startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        val bottomBarBackground = bottomAppBar.background as MaterialShapeDrawable
        bottomBarBackground.shapeAppearanceModel = bottomBarBackground.shapeAppearanceModel
            .toBuilder()
            .setAllCorners(RoundedCornerTreatment()).setAllCornerSizes(RelativeCornerSize(0.1f))
            .build()
    }
}