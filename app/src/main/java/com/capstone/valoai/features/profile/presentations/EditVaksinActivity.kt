package com.capstone.valoai.features.profile.presentations

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.valoai.R
import com.capstone.valoai.commons.Status
import com.capstone.valoai.databinding.ActivityEditVaksinBinding
import com.capstone.valoai.features.profile.data.remote.UserDataSourceRemote
import com.capstone.valoai.features.profile.domain.vmodel.ProfileViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class EditVaksinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditVaksinBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val user: FirebaseUser? by lazy { auth.currentUser }
    private lateinit var db: FirebaseFirestore
    private lateinit var vaksins: ArrayList<String>


    private val datePicker =
        MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

    private val datePicker2 =
        MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditVaksinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()

        getDataProfile()


        with(db) {
            collection("vaksins").get().addOnSuccessListener {
                vaksins =
                    it.documents.map { item -> item.get("name") as String } as ArrayList<String>
                setVaksin(vaksins)
            }


        }

        with(binding) {
            back.setOnClickListener {
                finish()
            }

            dateFieldVakin1.editText?.setOnFocusChangeListener { _, b ->
                if (b) {
                    datePicker.show(supportFragmentManager, datePicker.toString())
                }
            }

            datePicker.addOnPositiveButtonClickListener {
                dateFieldVakin1.editText?.setText(outputDateFormat.format(it))
            }

            dateFieldVakin2.editText?.setOnFocusChangeListener { _, b ->
                if (b) {
                    datePicker2.show(supportFragmentManager, datePicker2.toString())
                }
            }

            datePicker2.addOnPositiveButtonClickListener {
                dateFieldVakin2.editText?.setText(outputDateFormat.format(it))
            }
        }
    }

    private fun getDataProfile() {

        user?.let { user ->
            val dataSource = UserDataSourceRemote(user)
            val viewModel = ProfileViewModel(dataSource)
            showProgressBar()
            viewModel.getProfile().observe(this@EditVaksinActivity) { result ->
                result.let { st ->
                    when (st.status) {
                        Status.SUCCESS -> {
                            with(binding) {
                                fieldVakin1.editText?.setText(st.data?.vaksin1)
                                fieldVakin2.editText?.setText(st.data?.vaksin2)
                                dateFieldVakin1.editText?.setText(st.data?.vaksin1)
                                dateFieldVakin2.editText?.setText(st.data?.vaksin2)
                                btnSubmit.setOnClickListener {
                                    if (!validateForm()) return@setOnClickListener
                                    showProgressBar()

                                    val rVakin1 = fieldVakin1.editText?.text
                                    val rVakin2 = fieldVakin2.editText?.text
                                    val dVakin1 = dateFieldVakin1.editText?.text
                                    val dVakin2 = dateFieldVakin2.editText?.text

                                    val profile = st.data?.copyWith(
                                        null,
                                        null,
                                        rVakin1.toString(),
                                        rVakin2.toString(),
                                        null,
                                        dVakin1.toString(),
                                        dVakin2.toString(),
                                        null,
                                    )
                                    if (profile != null) {
                                        viewModel.putProfile(profile)
                                            .observe(this@EditVaksinActivity) {
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
        }

    }

    private fun validateForm(): Boolean {

        var valid = true

        val vaksin1 = binding.fieldVakin1.editText?.text.toString()
        val vaksin2 = binding.fieldVakin2.editText?.text.toString()
        val dateVaksin1 = binding.dateFieldVakin1.editText?.text.toString()
        val dateVaksin2 = binding.dateFieldVakin2.editText?.text.toString()



        if (vaksin1.isEmpty()) {
            binding.fieldVakin1.error = "Required."
            valid = false
        } else {
            binding.fieldVakin1.error = null
        }

        if (vaksin1.isNotEmpty() && dateVaksin1.isEmpty()) {
            binding.dateFieldVakin1.error = "Tidak boleh kosong"
            valid = false
        } else {
            binding.dateFieldVakin1.error = null
        }
        if (vaksin2.isNotEmpty() && dateVaksin2.isEmpty()) {
            binding.dateFieldVakin2.error = "Tidak boleh kosong"
            valid = false
        } else {
            binding.dateFieldVakin2.error = null
        }

        if (!vaksins.contains(vaksin1)) {
            valid = false
            binding.fieldVakin1.error = "Vaksin Tidak Valid"
        } else {
            binding.fieldVakin1.error = null
        }


        if (dateVaksin1.isNotEmpty()) {
            try {
                val test = outputDateFormat.parse(dateVaksin1)

                if(test == null){
                    valid = false
                    binding.dateFieldVakin1.error = "tanggal tidak valid"
                }else{
                    binding.dateFieldVakin1.error = null

                }
            } catch (e: Exception) {
                valid = false
                binding.dateFieldVakin1.error = "tanggal tidak valid"
            }
        }

        if (dateVaksin2.isNotEmpty()) {
            try {
                val test = outputDateFormat.parse(dateVaksin2)

                if(test == null){
                    valid = false
                    binding.dateFieldVakin2.error = "tanggal tidak valid"
                }else{
                    binding.dateFieldVakin2.error = null

                }
            } catch (e: Exception) {
                valid = false
                binding.dateFieldVakin2.error = "tanggal tidak valid"
            }
        }

        return valid
    }

    private fun setVaksin(vaksins: ArrayList<String>) {
        with(binding) {
            val adapterVaksin =
                ArrayAdapter(baseContext, R.layout.dropdown_menu_popup_item, vaksins)
            (fieldVakin1.editText as? AutoCompleteTextView)?.setAdapter(adapterVaksin)
            (fieldVakin2.editText as? AutoCompleteTextView)?.setAdapter(adapterVaksin)

            fieldVakin1.editText?.setOnFocusChangeListener { _, b ->
                if (!b) return@setOnFocusChangeListener
                vaksin1Auto.showDropDown()
            }

            fieldVakin2.editText?.setOnFocusChangeListener { _, b ->
                if (!b) return@setOnFocusChangeListener
                vaksin2Auto.showDropDown()
            }
        }

        Log.println(Log.INFO, "test", vaksins.toString())
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