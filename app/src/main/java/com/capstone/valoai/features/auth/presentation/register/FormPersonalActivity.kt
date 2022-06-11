package com.capstone.valoai.features.auth.presentation.register

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.capstone.valoai.R
import com.capstone.valoai.databinding.ActivityFormPersonalBinding
import com.capstone.valoai.features.dashboard.presentations.DashboardActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FormPersonalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFormPersonalBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var vaksins: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormPersonalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        setFormField()

        with(db) {
            collection("vaksins").get().addOnSuccessListener {
                vaksins =
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
        val datePicker2 =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        val datePicker3 =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        with(binding) {
            fieldBirthDate.editText?.setOnFocusChangeListener { _, b ->
                if (b) {
                    datePicker.show(supportFragmentManager, datePicker.toString())
                }
            }

            datePicker.addOnPositiveButtonClickListener {
                fieldBirthDate.editText?.setText(outputDateFormat.format(it))
            }

            dateFieldVakin1.editText?.setOnFocusChangeListener { _, b ->
                if (b) {
                    datePicker2.show(supportFragmentManager, datePicker2.toString())
                }
            }

            datePicker2.addOnPositiveButtonClickListener {
                dateFieldVakin1.editText?.setText(outputDateFormat.format(it))
            }

            dateFieldVakin2.editText?.setOnFocusChangeListener { view, b ->
                if (b) {
                    datePicker3.show(supportFragmentManager, datePicker3.toString())
                }
            }

            datePicker3.addOnPositiveButtonClickListener {
                dateFieldVakin2.editText?.setText(outputDateFormat.format(it))
            }


            fieldVakin1.editText?.doOnTextChanged { text, _, _, _ ->
                if (text.isNullOrEmpty()) {
                    labelVakin2.visibility = View.GONE
                    fieldVakin2.visibility = View.GONE
                    dateFieldVakin2.visibility = View.GONE
                    dateLabelVakin2.visibility = View.GONE
                } else {
                    fieldVakin2.visibility = View.VISIBLE
                    labelVakin2.visibility = View.VISIBLE
                    dateFieldVakin2.visibility = View.VISIBLE
                    dateLabelVakin2.visibility = View.VISIBLE
                }
            }

            fieldVakin1.editText?.setOnFocusChangeListener { view, b ->
                if (!b) return@setOnFocusChangeListener
                vaksin1Auto.showDropDown()
            }

            fieldVakin2.editText?.setOnFocusChangeListener { view, b ->
                if (!b) return@setOnFocusChangeListener
                vaksin2Auto.showDropDown()
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
                                "tanggal_vaksin1" to dateFieldVakin1.editText?.text.toString(),
                                "tanggal_vaksin2" to dateFieldVakin2.editText?.text.toString(),
                                "vaksin1" to fieldVakin1.editText?.text.toString(),
                                "vaksin2" to fieldVakin2.editText?.text.toString(),
                            )

                        ).addOnCompleteListener {
                            if (it.isSuccessful) {
                                firebaseAuth.currentUser?.updateProfile(profileUpdates)
                                    ?.addOnSuccessListener {
                                        val test = SuccessDialogView(this@FormPersonalActivity)
                                        test.showDialog("Success", "Kamu berhasil membuat akun!") {
                                            startActivity(
                                                Intent(
                                                    this@FormPersonalActivity,
                                                    DashboardActivity::class.java
                                                )
                                            )
                                            finish()
                                        }
                                        return@addOnSuccessListener
                                    }?.addOnCanceledListener {
                                        Toast.makeText(
                                            this@FormPersonalActivity,
                                            "Register failed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
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
        val vaksin1 = binding.fieldVakin1.editText?.text.toString()
        val vaksin2 = binding.fieldVakin2.editText?.text.toString()

        if (name.isEmpty()) {
            valid = false
            binding.fieldName.error = "Required."
        } else {
            binding.fieldName.error = null
        }


        if (birthDate.isEmpty()) {
            valid = false
            binding.fieldBirthDate.error = "Required."
        } else {
            binding.fieldBirthDate.error = null
        }

        if (vaksin1.isEmpty()) {
            binding.fieldVakin1.error = "Required."
            valid = false
        } else {
            binding.fieldVakin1.error = null
        }

        if (!vaksins.contains(vaksin1)) {
            valid = false
            binding.fieldVakin1.error = "Vaksin Tidak Valid"
        } else {
            binding.fieldVakin1.error = null
        }


        if (vaksins.isNotEmpty() && !vaksins.contains(vaksin2)) {
            binding.fieldVakin2.error = "Vaksin Tidak Valid"
            valid = false
        } else {
            binding.fieldVakin2.error = null
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



