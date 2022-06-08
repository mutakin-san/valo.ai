package com.capstone.valoai.features.auth.presentation.register

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.capstone.valoai.R
import com.capstone.valoai.databinding.ActivityFormPersonalBinding
import com.capstone.valoai.features.dashboard.presentations.DashboardActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log


class FormPersonalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormPersonalBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormPersonalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val items = listOf("Option 1", "Option 2", "Option 3", "Option 4")
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        with(binding) {
            val adapter = ArrayAdapter(baseContext, R.layout.dropdown_menu_popup_item, items)
            (fieldRiwayat1.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            (fieldRiwayat2.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            (fieldVakin1.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            (fieldVakin2.editText as? AutoCompleteTextView)?.setAdapter(adapter)


            fieldBirthDate.editText?.setOnFocusChangeListener { view, b ->
                if (b) {
                    Log.println(Log.INFO, "test", "test")
                    datePicker.show(supportFragmentManager, datePicker.toString())
                }
            }

            datePicker.addOnPositiveButtonClickListener {
                fieldBirthDate.editText?.setText(outputDateFormat.format(it))
            }

            btnSubmit.setOnClickListener {
                Log.println(Log.INFO, "user", firebaseAuth.currentUser?.displayName ?: "empty")
                if (validateForm()) {
//                    startActivity(Intent(this@FormPersonalActivity, DashboardActivity::class.java))
//                    db.collection()
//                    finish()
                }
            }

            firebaseAuth = Firebase.auth
            db = FirebaseFirestore.getInstance()
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val name = binding.fieldName.editText?.text.toString()
        val birthDate = binding.fieldName.editText?.text.toString()
        val riwayat1 = binding.fieldName.editText?.text.toString()
        val riwayat2 = binding.fieldName.editText?.text.toString()
        val vaksin1 = binding.fieldName.editText?.text.toString()
        val vaksin2 = binding.fieldName.editText?.text.toString()

        if (name.isEmpty()) {
            binding.fieldName.error = "Required."
            valid = false
        } else {
            binding.fieldName.error = null
        }


        if (birthDate.isEmpty()) {
            binding.fieldBirthDate.error = "Required."
            valid = false
        } else {
            binding.fieldBirthDate.error = null
        }

        if (riwayat1.isEmpty()) {
            binding.fieldBirthDate.error = "Required."
            valid = false
        } else {
            binding.fieldBirthDate.error = null
        }


        if (riwayat2.isEmpty() && riwayat1.isNotEmpty()) {
            binding.fieldBirthDate.error = "Required."
            valid = false
        } else {
            binding.fieldBirthDate.error = null
        }

        if (TextUtils.isEmpty(vaksin1)) {
            binding.fieldBirthDate.error = "Required."
            valid = false
        } else {
            binding.fieldBirthDate.error = null
        }

        if (vaksin2.isEmpty() && vaksin1.isNotEmpty()) {
            binding.fieldBirthDate.error = "Required."
            valid = false
        } else {
            binding.fieldBirthDate.error = null
        }


        return valid
    }

    private val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
}

