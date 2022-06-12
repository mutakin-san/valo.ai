package com.capstone.valoai.features.splashscreen.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.valoai.commons.navigateTo
import com.capstone.valoai.features.auth.presentation.login.LoginActivity
import com.capstone.valoai.features.dashboard.presentations.DashboardActivity
import com.capstone.valoai.features.onboarding.data.local.OnBoardPref
import com.capstone.valoai.features.onboarding.data.local.datastore
import com.capstone.valoai.features.onboarding.presentation.OnBoardingActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private val onBoardPref: OnBoardPref by lazy {
        OnBoardPref(this.datastore)
    }
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onStart() {
        super.onStart()
        FirebaseApp.initializeApp(this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = Firebase.auth

        lifecycleScope.launch {
            onBoardPref.launchStatus.first { status ->
                if (status) {
                    navigateTo(this@SplashScreenActivity, OnBoardingActivity::class.java)
                    onBoardPref.updateIsFirstLaunchedToFalse()
                } else {
                    if (firebaseAuth.currentUser != null) {
                        navigateTo(this@SplashScreenActivity, DashboardActivity::class.java)
                    }else {
                        navigateTo(this@SplashScreenActivity, LoginActivity::class.java)
                    }
                }

                status
            }
        }
    }

}