package com.capstone.valoai.features.splashscreen.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.valoai.features.auth.presentation.login.LoginActivity
import com.capstone.valoai.features.dashboard.presentations.DashboardActivity
import com.capstone.valoai.features.onboarding.data.local.OnBoardPref
import com.capstone.valoai.features.onboarding.data.local.datastore
import com.capstone.valoai.features.onboarding.presentation.OnBoardingActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private val onBoardPref: OnBoardPref by lazy {
        OnBoardPref(this.datastore)
    }
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Firebase.initialize(this)
        firebaseAuth = FirebaseAuth.getInstance()

        lifecycleScope.launch {
            onBoardPref.launchStatus.first { status ->
                if (status) {
                    navigateTo(OnBoardingActivity::class.java)
                    onBoardPref.updateIsFirstLaunchedToFalse()
                } else {
                    if (firebaseAuth.currentUser != null) {
                        navigateTo(DashboardActivity::class.java)
                    }else {
                        navigateTo(LoginActivity::class.java)
                    }
                }

                status
            }
        }
    }


    private fun navigateTo(destination: Class<*>){
        val intent = Intent(this@SplashScreenActivity, destination)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}