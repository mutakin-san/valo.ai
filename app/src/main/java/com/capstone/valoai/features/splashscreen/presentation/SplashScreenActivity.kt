package com.capstone.valoai.features.splashscreen.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.valoai.features.auth.presentation.login.LoginActivity
import com.capstone.valoai.features.onboarding.data.local.OnBoardPref
import com.capstone.valoai.features.onboarding.data.local.datastore
import com.capstone.valoai.features.onboarding.presentation.OnBoardingActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private val onBoardPref: OnBoardPref by lazy {
        OnBoardPref(this.datastore)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            onBoardPref.launchStatus.first { status ->
                if (status) {
                    val intent = Intent(this@SplashScreenActivity, OnBoardingActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                    onBoardPref.updateIsFirstLaunchedToFalse()
                } else {
                    val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }

                status
            }
        }
    }
}