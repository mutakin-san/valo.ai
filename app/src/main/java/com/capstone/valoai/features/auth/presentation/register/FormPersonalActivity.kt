package com.capstone.valoai.features.auth.presentation.register

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.valoai.R
import com.capstone.valoai.databinding.ActivityFormPersonalBinding
import com.capstone.valoai.features.auth.presentation.login.LoginActivity
import com.capstone.valoai.features.dashboard.presentations.DashboardActivity
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.common.collect.ArrayListMultimap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log


class FormPersonalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormPersonalBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormPersonalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        setFormField()

        with(db) {
            collection("vaksins").get().addOnSuccessListener {
                val vaksins =
                    it.documents.map { item -> item.get("name") as String } as ArrayList<String>
                setVaksin(vaksins)
            }

            collection("riwayats").get().addOnSuccessListener {
               val riwayat =
                    it.documents.map { item -> item.get("name") as String } as ArrayList<String>
                setRiwayat(riwayat)

            }
        }
    }

    private fun setRiwayat(riwayat: ArrayList<String>) {
        with(binding) {
            val adapterRiwayat =
                ArrayAdapter(baseContext, R.layout.dropdown_menu_popup_item, riwayat)
            (fieldRiwayat1.editText as? AutoCompleteTextView)?.setAdapter(adapterRiwayat)
            (fieldRiwayat2.editText as? AutoCompleteTextView)?.setAdapter(adapterRiwayat)
        }
    }

    private fun setVaksin(vaksins: ArrayList<String>) {
        with(binding) {
            val adapterVaksin =
                ArrayAdapter(baseContext, R.layout.dropdown_menu_popup_item, vaksins)
            (fieldVakin1.editText as? AutoCompleteTextView)?.setAdapter(adapterVaksin)
            (fieldVakin2.editText as? AutoCompleteTextView)?.setAdapter(adapterVaksin)
        }
    }

    private fun setFormField() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        with(binding) {
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
                showProgressBar()
                Log.println(Log.INFO, "user", firebaseAuth.currentUser?.displayName ?: "empty")
                if (validateForm()) {
                    val users = firebaseAuth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(fieldName.editText?.text.toString())
                        .setPhotoUri(Uri.parse("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png"))
                        .build()

                    db.collection("users")
                        .document(users?.uid.toString()).set(
                            mapOf(
                                "name" to fieldName.editText?.text.toString(),
                                "birthDate" to fieldBirthDate.editText?.text.toString(),
                                "riwayat1" to fieldRiwayat1.editText?.text.toString(),
                                "riwayat2" to fieldRiwayat2.editText?.text.toString(),
                                "vaksin1" to fieldVakin1.editText?.text.toString(),
                                "vaksin2" to fieldVakin2.editText?.text.toString(),
                            )
                        ).addOnCompleteListener {
                            if (it.isSuccessful) {
                                firebaseAuth.currentUser?.updateProfile(profileUpdates)
                                    ?.addOnSuccessListener {
                                        startActivity(
                                            Intent(
                                                this@FormPersonalActivity,
                                                DashboardActivity::class.java
                                            )
                                        )
                                        finish()
                                        return@addOnSuccessListener
                                    }
                                Toast.makeText(
                                    this@FormPersonalActivity,
                                    "Register failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (it.isCanceled) {
                                Toast.makeText(
                                    this@FormPersonalActivity,
                                    "Register failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                }

                hideProgressBar()

            }


        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val name = binding.fieldName.editText?.text.toString()
        val birthDate = binding.fieldName.editText?.text.toString()
        val riwayat1 = binding.fieldName.editText?.text.toString()
//        val riwayat2 = binding.fieldName.editText?.text.toString()
        val vaksin1 = binding.fieldName.editText?.text.toString()
//        val vaksin2 = binding.fieldName.editText?.text.toString()

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
            binding.fieldRiwayat1.error = "Required."
            valid = false
        } else {
            binding.fieldRiwayat1.error = null
        }


        if (vaksin1.isEmpty()) {
            binding.fieldVakin1.error = "Required."
            valid = false
        } else {
            binding.fieldVakin1.error = null
        }



        return valid
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
}

