package com.capstone.valoai.features.profile.presentations

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.capstone.valoai.commons.Status
import com.capstone.valoai.commons.navigateTo
import com.capstone.valoai.databinding.ActivityEditProfileBinding
import com.capstone.valoai.features.auth.presentation.login.LoginActivity
import com.capstone.valoai.features.profile.data.models.Profile
import com.capstone.valoai.features.profile.data.remote.UserDataSourceRemote
import com.capstone.valoai.features.profile.domain.vmodel.ProfileViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val user: FirebaseUser? by lazy { auth.currentUser }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()


        with(binding) {
            back.setOnClickListener {
                finish()
            }

            user?.let {
                showProgressBar()
                val dataSource = UserDataSourceRemote(it)
                val viewModel = ProfileViewModel(dataSource)
                Glide.with(baseContext).load(user?.photoUrl).circleCrop().into(binding.profileImage)
                viewModel.getProfile().observe(this@EditProfileActivity) { result ->
                    result.let { st ->
                        when (st.status) {
                            Status.SUCCESS -> {
                                fieldName.editText?.setText(st.data?.name)
                                fieldBirthDate.editText?.setText(st.data?.birthDate)
                                btnSubmit.setOnClickListener {
                                    if(!validateForm()) return@setOnClickListener
                                    showProgressBar()

                                    val name = fieldName.editText?.text
                                    val dateBirth = fieldBirthDate.editText?.text

                                    val profile = st.data?.copyWith(
                                        name = name.toString(),
                                        birthDate = dateBirth.toString(),
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                    )
                                    if (profile != null) {
                                        viewModel.putProfile(profile)
                                            .observe(this@EditProfileActivity) {
                                                showProgressBar()
                                                if (it.data == true) {
                                                    finish()
                                                }
                                                hideProgressBar()
                                            }
                                    }
                                }

                                hideProgressBar()
                            }
                            Status.ERROR -> {
                                Log.println(Log.ERROR, "getProfile:failure", "fail get data")
                                Toast.makeText(
                                    baseContext,
                                    "Fail get profile",
                                    Toast.LENGTH_SHORT
                                ).show()

                                hideProgressBar()

                            }
                            Status.LOADING -> {
                                showProgressBar()

                            }
                        }
                    }
                }


                deleteBtn.setOnClickListener {
                    viewModel.deleteProfile().observe(this@EditProfileActivity) { data ->
                        if (data.data == true) {
                            navigateTo(this@EditProfileActivity, LoginActivity::class.java)
                        }
                    }
                }

                fieldBirthDate.editText?.setOnFocusChangeListener { _, b ->
                    if (b) {
                        datePicker.show(supportFragmentManager, datePicker.toString())
                    }
                }

                datePicker.addOnPositiveButtonClickListener {
                    fieldBirthDate.editText?.setText(outputDateFormat.format(it))
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val name = binding.fieldName.editText?.text.toString()
        val birthDate = binding.fieldName.editText?.text.toString()


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
        if (birthDate.isNotEmpty()) {
            try {
                val test = outputDateFormat.parse(birthDate)

                if (test == null) {
                    valid = false
                    binding.fieldBirthDate.error = "tanggal tidak valid"
                } else {
                    binding.fieldBirthDate.error = null

                }
            } catch (e: Exception) {
                valid = false
                binding.fieldBirthDate.error = "tanggal tidak valid"
            }
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