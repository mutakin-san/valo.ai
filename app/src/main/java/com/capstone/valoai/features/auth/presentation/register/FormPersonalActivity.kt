package com.capstone.valoai.features.auth.presentation.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.capstone.valoai.R

class FormPersonalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_personal)
    }

    fun onBirthDateClick(view: View) {}
}