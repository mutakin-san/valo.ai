package com.capstone.valoai.features.auth.presentation.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.valoai.databinding.ActivityFormPersonalBinding

class FormPersonalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormPersonalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormPersonalBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}