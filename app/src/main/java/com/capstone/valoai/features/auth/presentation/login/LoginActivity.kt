package com.capstone.valoai.features.auth.presentation.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.capstone.valoai.R
import com.capstone.valoai.features.dashboard.presentations.DashboardActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.btn_login).setOnClickListener {
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        }
    }
}