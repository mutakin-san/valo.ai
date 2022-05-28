package com.capstone.valoai.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.capstone.valoai.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        findViewById<Button>(R.id.btn_login).setOnClickListener {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }
    }
}